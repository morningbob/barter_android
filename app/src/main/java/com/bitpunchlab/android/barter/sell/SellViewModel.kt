package com.bitpunchlab.android.barter.sell

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.text.selection.DisableSelection
import androidx.lifecycle.ViewModel
import com.bitpunchlab.android.barter.firebase.FirebaseClient
import com.bitpunchlab.android.barter.models.ProductAsking
import com.bitpunchlab.android.barter.models.ProductOffering
import com.bitpunchlab.android.barter.util.Category
import com.bitpunchlab.android.barter.util.ImageType
import com.bitpunchlab.android.barter.util.ProductImage
import com.bitpunchlab.android.barter.util.SellingDuration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.*

class SellViewModel : ViewModel() {

    private val _productName = MutableStateFlow("")
    val productName : StateFlow<String> get() = _productName.asStateFlow()

    private val _shouldExpandCategory = MutableStateFlow(false)
    val shouldExpandCategory : StateFlow<Boolean> get() = _shouldExpandCategory.asStateFlow()

    private val _productCategory = MutableStateFlow(Category.NOT_SET)
    val productCategory : StateFlow<Category> get() = _productCategory.asStateFlow()

    private val _shouldExpandDuration = MutableStateFlow(false)
    val shouldExpandDuration : StateFlow<Boolean> get() = _shouldExpandDuration.asStateFlow()

    private val _sellingDuration = MutableStateFlow(SellingDuration.NOT_SET)
    val sellingDuration : StateFlow<SellingDuration> get() = _sellingDuration.asStateFlow()

    private val _productImages = MutableStateFlow<List<ProductImage>>(listOf())
    val productImages : StateFlow<List<ProductImage>> get() = _productImages.asStateFlow()

    private val _askingProductImages = MutableStateFlow<List<Bitmap>>(listOf())
    val askingProductImages : StateFlow<List<Bitmap>> get() = _askingProductImages.asStateFlow()

    private val _imageType = MutableStateFlow(ImageType.PRODUCT_IMAGE)
    val imageType : StateFlow<ImageType> get() = _imageType.asStateFlow()

    private val _shouldSetProduct = MutableStateFlow(false)
    val shouldSetProduct : StateFlow<Boolean> get() = _shouldSetProduct.asStateFlow()

    private val _imagesDisplay = MutableStateFlow<List<ProductImage>>(listOf())
    val imagesDisplay : StateFlow<List<ProductImage>> get() = _imagesDisplay.asStateFlow()

    private val _shouldDisplayImages = MutableStateFlow(false)
    val shouldDisplayImages : StateFlow<Boolean> get() = _shouldDisplayImages.asStateFlow()

    private val _shouldPopImages = MutableStateFlow(false)
    val shouldPopImages : StateFlow<Boolean> get() = _shouldPopImages.asStateFlow()

    private val userId = MutableStateFlow("")

    private val _processSellingStatus = MutableStateFlow(0)
    val processSellingStatus : StateFlow<Int> get() = _processSellingStatus.asStateFlow()

    init {
        CoroutineScope(Dispatchers.IO).launch {
            FirebaseClient.userId.collect() {
                userId.value = it
            }
        }
    }

    fun updateShouldExpandCategory(should: Boolean) {
        _shouldExpandCategory.value = should
    }

    fun updateCategory(cat: Category) {
        _productCategory.value = cat
    }

    fun updateShouldExpandDuration(should: Boolean) {
        _shouldExpandDuration.value = should
    }

    fun updateName(name: String) {
        _productName.value = name
    }

    fun updateSellingDuration(duration: SellingDuration) {
        _sellingDuration.value = duration
    }

    fun updateProductImages(image: Bitmap) {
        val productImage = ProductImage(id = UUID.randomUUID().toString(), image = image)
        val newList = productImages.value.toMutableList()
        newList.add(productImage)
        //Log.i("sellVM", "added one bitmap")
        _productImages.value = newList
    }

    fun updateAskingImages(bitmap: Bitmap) {
        val newList = askingProductImages.value.toMutableList()
        newList.add(bitmap)
        _askingProductImages.value = newList
    }

    fun updateImageType(type: ImageType) {
        _imageType.value = type
    }

    fun updateShouldSetProduct(set: Boolean) {
        _shouldSetProduct.value = set
    }
    fun updateShouldDisplayImages(should: Boolean) {
        _shouldDisplayImages.value = should
    }

    fun onSendClicked() {
        // validate inputs
        // here, I don't make images required
        if (productName.value != "" && productCategory.value != Category.NOT_SET &&
                AskingProductInfo.askingProducts.isNotEmpty() &&
             sellingDuration.value != SellingDuration.NOT_SET) {
            processSelling()
        } else {
            // invalid field
            _processSellingStatus.value = 3
        }
    }

    // I put empty images list, we will populate it in FirebaseClient
    fun processSelling() {
        val productOffering = ProductOffering(productId = UUID.randomUUID().toString(),
            name = productName.value, category = productCategory.value.name,
            userId = userId.value, images = listOf(), currentBids = listOf(),
            duration = sellingDuration.value.value
        )

        val updatedAskingProducts = mutableListOf<ProductAsking>()
        // we update the product offering id in all the asking products
        // since when we created them in asking product screen,
        // we don't have this id yet
        // for the images' url, we will update them in firebase client
        for (each in AskingProductInfo.askingProducts) {
            val newProduct = each.copy(productOfferingId = productOffering.productId)
            updatedAskingProducts.add(newProduct)
        }

        CoroutineScope(Dispatchers.IO).launch {
            Log.i("process selling", "got images size: ${productImages.value.size}")
            if (FirebaseClient.processSelling(productOffering, productImages.value,
                updatedAskingProducts, AskingProductInfo.askingProductsImages)) {
                Log.i("process selling, from sellVM", "succeeded")
                _processSellingStatus.value = 2
                clearFields()
            } else {
                Log.i("process selling, from sellVM", "failed")
                _processSellingStatus.value = 1
                clearFields()
            }
        }
    }

    fun prepareImagesDisplay() {
        _imagesDisplay.value = productImages.value
    }

    fun updateShouldPopImages(should: Boolean) {
        _shouldPopImages.value = should
    }

    fun deleteImage(image: ProductImage) {
        Log.i("askingVM", "got image")
        val newList = imagesDisplay.value.toMutableList()
        newList.remove(image)
        _imagesDisplay.value = newList
    }

    fun updateProcessSellingStatus(status: Int) {
        _processSellingStatus.value = status
    }

    fun clearFields() {
        _productName.value = ""
        _productCategory.value = Category.NOT_SET
        _sellingDuration.value = SellingDuration.NOT_SET
        _productImages.value = listOf()
        _askingProductImages.value = listOf()
        AskingProductInfo.askingProducts = mutableListOf<ProductAsking>()
        AskingProductInfo.askingProductsImages = mutableListOf()
    }
}