package com.bitpunchlab.android.barter.productOfferingDetails

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import com.bitpunchlab.android.barter.base.loadImage
import com.bitpunchlab.android.barter.models.ProductOffering
import com.bitpunchlab.android.barter.util.ImageType
import com.bitpunchlab.android.barter.util.ProductImage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow


class ProductOfferingDetailsViewModel() : ViewModel() {

    val _product = MutableStateFlow<ProductOffering?>(null)
    val product : StateFlow<ProductOffering?> get() = _product.asStateFlow()

    val _shouldDisplayImages = MutableStateFlow<Boolean>(false)
    val shouldDisplayImages : StateFlow<Boolean> get() = _shouldDisplayImages.asStateFlow()

    val _shouldPopImages = MutableStateFlow<Boolean>(false)
    val shouldPopImages : StateFlow<Boolean> get() = _shouldPopImages.asStateFlow()

    // when user clicks view product images, or view asking product images
    // we retrieve the images and put it here
    // so the images display screen can retrieve it here
    // that way, we can display both product images and asking product images
    // both in one images display screen
    val _imagesDisplay = MutableStateFlow<List<ProductImage>>(listOf())
    val imagesDisplay : StateFlow<List<ProductImage>> get() = _imagesDisplay.asStateFlow()

    val _productImages = MutableStateFlow<List<Bitmap>>(listOf())
    val productImages : StateFlow<List<Bitmap>> get() = _productImages.asStateFlow()

    val _askingImages = MutableStateFlow<List<Bitmap>>(listOf())
    val askingImages : StateFlow<List<Bitmap>> get() = _askingImages.asStateFlow()

    fun updateShouldDisplayImages(should: Boolean) {
        _shouldDisplayImages.value = should
    }

    fun updateShouldPopImages(should: Boolean) {
        _shouldPopImages.value = should
    }

    fun updateImages(images: List<ProductImage>) {
        _imagesDisplay.value = images
    }

    // let me think how and when to retrieve the images from cloud storage
    // and store as bitmaps in this view model

    fun prepareImages(type: ImageType, imagesUrl: List<String>) {
        // retrieve images from cloud storage and store in view model
        // we need to do like this because Images Display Screen's setup
        // can't be customized to use Glide to load images as needed
        for (i in 0..imagesUrl.size - 1) {
            //loadImage(url = imagesUrl[i])
        }
        // create Product Images and store in view model

        // put Product Images in imagesDisplay
    }

    fun deleteImage(image: ProductImage) {
        Log.i("askingVM", "got image")
        //val newList = imagesDisplay.value.toMutableList()
        //newList.remove(image)
        //_imagesDisplay.value = newList
    }
}