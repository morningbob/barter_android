package com.bitpunchlab.android.barter.sell

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import com.bitpunchlab.android.barter.firebase.FirebaseClient
import com.bitpunchlab.android.barter.models.ProductAsking
import com.bitpunchlab.android.barter.models.ProductImageToDisplay
import com.bitpunchlab.android.barter.models.ProductOffering
import com.bitpunchlab.android.barter.productsOfferingList.ProductInfo
import com.bitpunchlab.android.barter.util.Category
import com.bitpunchlab.android.barter.util.ImageType
import com.bitpunchlab.android.barter.util.ProcessSellingStatus
import com.bitpunchlab.android.barter.util.SellingDuration
import com.bitpunchlab.android.barter.util.getCurrentDateTime
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
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

    // we use SnapshotStateList because I want the removal of the image to be reflected
    // in lazy column
    private val _productImages = MutableStateFlow<SnapshotStateList<ProductImageToDisplay>>(
        mutableStateListOf()
    )
    val productImages : StateFlow<SnapshotStateList<ProductImageToDisplay>> get() = _productImages.asStateFlow()

    private val _askingProductImages = MutableStateFlow<List<Bitmap>>(listOf())
    val askingProductImages : StateFlow<List<Bitmap>> get() = _askingProductImages.asStateFlow()

    private val _imageType = MutableStateFlow(ImageType.PRODUCT_IMAGE)
    val imageType : StateFlow<ImageType> get() = _imageType.asStateFlow()

    private val _shouldSetProduct = MutableStateFlow(false)
    val shouldSetProduct : StateFlow<Boolean> get() = _shouldSetProduct.asStateFlow()

    private val _imagesDisplay = MutableStateFlow<List<ProductImageToDisplay>>(listOf())
    val imagesDisplay : StateFlow<List<ProductImageToDisplay>> get() = _imagesDisplay.asStateFlow()

    private val _shouldDisplayImages = MutableStateFlow(false)
    val shouldDisplayImages : StateFlow<Boolean> get() = _shouldDisplayImages.asStateFlow()

    private val _shouldPopImages = MutableStateFlow(false)
    val shouldPopImages : StateFlow<Boolean> get() = _shouldPopImages.asStateFlow()

    private val userId = MutableStateFlow("")

    private val _processSellingStatus = MutableStateFlow(ProcessSellingStatus.NORMAL)
    val processSellingStatus : StateFlow<ProcessSellingStatus> get() = _processSellingStatus.asStateFlow()

    private val _shouldShowAsking = MutableStateFlow(false)
    val shouldShowAsking : StateFlow<Boolean> get() = _shouldShowAsking.asStateFlow()

    private val _loadingAlpha = MutableStateFlow(0f)
    val loadingAlpha : StateFlow<Float> get() = _loadingAlpha.asStateFlow()

    private val _deleteImageStatus = MutableStateFlow(0)
    val deleteImageStatus : StateFlow<Int> get() = _deleteImageStatus.asStateFlow()

    //private val _triggerImageUpdate = MutableStateFlow(false)
    //val triggerImageUpdate : StateFlow<Boolean> get() = _triggerImageUpdate.asStateFlow()

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
        val productImage = ProductImageToDisplay(
            imageId = UUID.randomUUID().toString(),
            image = image,
            imageUrlCloud = "")
        val newList = productImages.value.toMutableStateList()
        newList.add(productImage)
        //Log.i("sellVM", "added one bitmap")
        _productImages.value = newList
    }

    fun updateDeleteImageStatus(status: Int) {
        _deleteImageStatus.value = status
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

    fun updateShouldShowAsking(should: Boolean) {
        _shouldShowAsking.value = should
    }

    //fun updateTriggerImageUpdate(trigger: Boolean) {
    //    _triggerImageUpdate.value = trigger
    //}

    fun prepareAskingProducts() {
        ProductInfo.updateAskingProducts(AskingProductInfo.askingProducts.value)
        ProductInfo.updateAskingImages(AskingProductInfo.askingImages.value)
    }

    fun onSendClicked() {
        // validate inputs
        // here, I don't make images required
        if (productName.value != "" && productCategory.value != Category.NOT_SET &&
                AskingProductInfo.askingProducts.value.isNotEmpty() &&
             sellingDuration.value != SellingDuration.NOT_SET) {
            _loadingAlpha.value = 100f
            CoroutineScope(Dispatchers.IO).launch {
                if (processSelling()) {
                    Log.i("process selling, from sellVM", "succeeded")
                    _processSellingStatus.value = ProcessSellingStatus.SUCCESS
                    // I clear the fields and stop loading spinner as such in 2 places
                    // because the timing of each are different.
                    // Can't put it after if clause
                    // need to wait for the processing finished
                    clearFields()
                    _loadingAlpha.value = 0f
                } else {
                    Log.i("process selling, from sellVM", "failed")
                    _processSellingStatus.value = ProcessSellingStatus.FAILURE
                    clearFields()
                    _loadingAlpha.value = 0f
                }
            }

        } else {
            // invalid field
            _processSellingStatus.value = ProcessSellingStatus.INVALID_INPUTS
        }
    }

    // we create the product offering here
    private suspend fun processSelling() : Boolean {
        val productOffering = ProductOffering(
            productId = UUID.randomUUID().toString(),
            name = productName.value, category = productCategory.value.name,
            userId = userId.value, images = listOf(),
            userName = FirebaseClient.currentUserFirebase.value?.name ?: "",
            duration = sellingDuration.value.value,
            dateCreated = getCurrentDateTime(),
            status = 0
        )

        val updatedAskingProducts = mutableListOf<ProductAsking>()
        // we update the product offering id in all the asking products
        // since when we created them in asking product screen,
        // we don't have this id yet
        // for the images' url, we will update them in firebase client
        for (each in AskingProductInfo.askingProducts.value) {
            val newProduct = each.copy(productOfferingId = productOffering.productId)
            updatedAskingProducts.add(newProduct)
        }

        return CoroutineScope(Dispatchers.IO).async {
            Log.i("process selling", "got images size: ${productImages.value.size}")
            FirebaseClient.processSelling(productOffering, productImages.value,
                updatedAskingProducts, AskingProductInfo.askingImages.value)
        }.await()
    }

    fun prepareImagesDisplay() {
        _imagesDisplay.value = productImages.value
    }

    fun updateShouldPopImages(should: Boolean) {
        _shouldPopImages.value = should
    }

    fun deleteImage(image: ProductImageToDisplay) {
        _productImages.value.remove(image)
    }

    fun updateProcessSellingStatus(status: ProcessSellingStatus) {
        _processSellingStatus.value = status
    }

    fun clearFields() {
        _productName.value = ""
        _productCategory.value = Category.NOT_SET
        _sellingDuration.value = SellingDuration.NOT_SET
        _productImages.value = mutableStateListOf()
        _askingProductImages.value = listOf()
        AskingProductInfo.updateAskingProducts(mutableListOf<ProductAsking>())
        AskingProductInfo.updateAskingImages(mutableListOf())
    }
}