package com.bitpunchlab.android.barter.sell

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.lifecycle.ViewModel
import com.bitpunchlab.android.barter.util.Category
import com.bitpunchlab.android.barter.util.ImageType
import com.bitpunchlab.android.barter.util.SellingDuration
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

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

    private val _productImage = MutableStateFlow<Bitmap?>(null)
    val productImage : StateFlow<Bitmap?> get() = _productImage.asStateFlow()

    private val _productImages = MutableStateFlow<List<Bitmap>>(listOf())
    val productImages : StateFlow<List<Bitmap>> get() = _productImages.asStateFlow()

    private val _askingProductImages = MutableStateFlow<List<Bitmap>>(listOf())
    val askingProductImages : StateFlow<List<Bitmap>> get() = _askingProductImages.asStateFlow()

    private val _imageType = MutableStateFlow(ImageType.PRODUCT_IMAGE)
    val imageType : StateFlow<ImageType> get() = _imageType.asStateFlow()

    private val _shouldSetProduct = MutableStateFlow(false)
    val shouldSetProduct : StateFlow<Boolean> get() = _shouldSetProduct.asStateFlow()

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

    fun updateProductImage(bitmap: Bitmap) {
        _productImage.value = bitmap
    }

    fun updateProductImages(bitmap: Bitmap) {
        val newList = productImages.value.toMutableList()
        newList.add(bitmap)
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
}