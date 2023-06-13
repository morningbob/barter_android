package com.bitpunchlab.android.barter.bid

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.ViewModel
import com.bitpunchlab.android.barter.util.Category
import com.bitpunchlab.android.barter.util.ProductImage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

class BidFormViewModel : ViewModel() {

    val _bidTime = MutableStateFlow("")
    val bidTime : StateFlow<String> get() = _bidTime.asStateFlow()

    val _bidProductName = MutableStateFlow("")
    val bidProductName : StateFlow<String> get() = _bidProductName.asStateFlow()

    val _bidProductCategory = MutableStateFlow(Category.NOT_SET)
    val bidProductCategory : StateFlow<Category> get() = _bidProductCategory.asStateFlow()

    val _imagesDisplay = MutableStateFlow<MutableList<ProductImage>>(mutableListOf())
    val imagesDisplay : StateFlow<MutableList<ProductImage>> get() = _imagesDisplay.asStateFlow()

    val _shouldExpandCategoryDropdown = MutableStateFlow<Boolean>(false)
    val shouldExpandCategoryDropdown : StateFlow<Boolean>
        get() = _shouldExpandCategoryDropdown.asStateFlow()

    val _shouldDisplayImages = MutableStateFlow<Boolean>(false)
    val shouldDisplayImages : StateFlow<Boolean> get() = _shouldDisplayImages.asStateFlow()

    val _shouldPopImages = MutableStateFlow<Boolean>(false)
    val shouldPopImages : StateFlow<Boolean> get() = _shouldPopImages.asStateFlow()

    fun updateBidProductName(name: String) {
        _bidProductName.value = name
    }

    fun updateBidProductCategory(cat: Category) {
        _bidProductCategory.value = cat
    }

    fun updateShouldExpandCategoryDropdown(should: Boolean) {
        _shouldExpandCategoryDropdown.value = should
    }

    fun updateImagesDisplay(bitmap: Bitmap) {
        val productImage = ProductImage(UUID.randomUUID().toString(), bitmap)
        _imagesDisplay.value.add(productImage)
    }

    fun updateShouldDisplayImages(should: Boolean) {
        _shouldDisplayImages.value = should
    }

    fun updateShouldPopImages(should: Boolean) {
        _shouldPopImages.value = should
    }

    fun deleteImage(image: ProductImage) {
        //Log.i("askingVM", "got image")
        //val newList = imagesDisplay.value.toMutableList()
        //newList.remove(image)
        //_imagesDisplay.value = newList
    }
}