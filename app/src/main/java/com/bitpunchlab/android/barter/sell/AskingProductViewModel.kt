package com.bitpunchlab.android.barter.sell

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import com.bitpunchlab.android.barter.firebase.FirebaseClient
import com.bitpunchlab.android.barter.models.ProductAsking
import com.bitpunchlab.android.barter.models.ProductImageToDisplay
import com.bitpunchlab.android.barter.models.ProductOffering
import com.bitpunchlab.android.barter.util.Category
import com.bitpunchlab.android.barter.util.SellingDuration
import com.bitpunchlab.android.barter.util.SetAskingProductStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.*
import kotlin.collections.HashMap

class AskingProductViewModel : ViewModel() {

    private val _productName = MutableStateFlow("")
    val productName : StateFlow<String> get() = _productName.asStateFlow()

    private val _shouldExpandCategory = MutableStateFlow(false)
    val shouldExpandCategory : StateFlow<Boolean> get() = _shouldExpandCategory.asStateFlow()

    private val _productCategory = MutableStateFlow(Category.NOT_SET)
    val productCategory : StateFlow<Category> get() = _productCategory.asStateFlow()

    private val _askingProductImages = MutableStateFlow<SnapshotStateList<ProductImageToDisplay>>(mutableStateListOf())
    val askingProductImages : StateFlow<SnapshotStateList<ProductImageToDisplay>> get() = _askingProductImages.asStateFlow()

    private val _status = MutableStateFlow(SetAskingProductStatus.NORMAL)
    val status : StateFlow<SetAskingProductStatus> get() = _status.asStateFlow()

    private val _update = MutableStateFlow(false)
    val update : StateFlow<Boolean> get() = _update.asStateFlow()

    private val _imagesDisplay = MutableStateFlow<List<ProductImageToDisplay>>(listOf())
    val imagesDisplay : StateFlow<List<ProductImageToDisplay>> get() = _imagesDisplay.asStateFlow()

    private val _shouldDisplayImages = MutableStateFlow(false)
    val shouldDisplayImages : StateFlow<Boolean> get() = _shouldDisplayImages.asStateFlow()

    private val _shouldPopImages = MutableStateFlow(false)
    val shouldPopImages : StateFlow<Boolean> get() = _shouldPopImages.asStateFlow()

    private val _deleteImageStatus = MutableStateFlow(0)
    val deleteImageStatus : StateFlow<Int> get() = _deleteImageStatus.asStateFlow()

    fun updateShouldExpandCategory(should: Boolean) {
        _shouldExpandCategory.value = should
    }

    fun updateCategory(cat: Category) {
        _productCategory.value = cat
    }

    fun updateName(name: String) {
        _productName.value = name
    }

    fun updateAskingImages(image: Bitmap) {
        val productImage = ProductImageToDisplay(
            //imageId = UUID.randomUUID().toString(),
            //image = image,
            imageUrlCloud = "")
        _askingProductImages.value.add(productImage)
    }

    fun updateShouldDisplayImages(should: Boolean) {
        //Log.i("askingVM", "updating should display images $should")
        _shouldDisplayImages.value = should
    }

    fun prepareImagesDisplay() {
        //Log.i("askingVM", "prepare images, no of images ${askingProductImages.value.size}")
        _imagesDisplay.value = askingProductImages.value
    }

    fun updateShouldPopImages(should: Boolean) {
        _shouldPopImages.value = should
    }

    fun processAskingProduct() {
        // validate inputs
        if (validateInputs()) {
            val newProduct = ProductAsking(
                productId = UUID.randomUUID().toString(),
                category = productCategory.value.name,
                //userId = FirebaseClient.currentUserFirebase.value!!.id,
                name = productName.value,
                // the product offering id is not known at this moment
                // it is not yet created
                // it is set in sellVM
                productOfferingId = ""
            )

            AskingProductInfo.addAskingProduct(newProduct)
            AskingProductInfo.addAskingImages(askingProductImages.value)

            // clean up
            clearForm()
            _status.value = SetAskingProductStatus.SUCCESS
            _update.value = true
        } else {
            _status.value = SetAskingProductStatus.INVALID_INPUTS
        }
    }


    fun updateStatus(status: SetAskingProductStatus) {
        _status.value = status
    }

    private fun validateInputs() : Boolean {
        return !(productName.value == "" || productCategory.value == Category.NOT_SET)
    }

    fun deleteImage(image: ProductImageToDisplay) {
        _askingProductImages.value.remove(image)
    }


    fun updateDeleteImageStatus(status: Int) {
        _deleteImageStatus.value = status
    }
    private fun clearForm() {
        _productName.value = ""
        _productCategory.value = Category.NOT_SET
        _askingProductImages.value = mutableStateListOf()
        _shouldExpandCategory.value = false
    }
}
