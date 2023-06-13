package com.bitpunchlab.android.barter.productOfferingDetails

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.produceState
import androidx.compose.ui.platform.LocalContext
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.ViewModel
import com.bitpunchlab.android.barter.R
import com.bitpunchlab.android.barter.models.ProductOffering
import com.bitpunchlab.android.barter.productsOfferingList.ProductInfo
import com.bitpunchlab.android.barter.util.ImageType
import com.bitpunchlab.android.barter.util.ProductImage
import com.bitpunchlab.android.barter.util.createPlaceholderImage
import com.bitpunchlab.android.barter.util.loadImage
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.UUID


class ProductOfferingDetailsViewModel() : ViewModel() {

    private val _product = MutableStateFlow<ProductOffering?>(null)
    val product : StateFlow<ProductOffering?> get() = _product.asStateFlow()

    private val _shouldDisplayImages = MutableStateFlow<Boolean>(false)
    val shouldDisplayImages : StateFlow<Boolean> get() = _shouldDisplayImages.asStateFlow()

    private val _shouldPopImages = MutableStateFlow<Boolean>(false)
    val shouldPopImages : StateFlow<Boolean> get() = _shouldPopImages.asStateFlow()

    private val _shouldDisplayAskingProducts = MutableStateFlow<Boolean>(false)
    val shouldDisplayAskingProducts : StateFlow<Boolean> get() = _shouldDisplayAskingProducts.asStateFlow()

    //private val _shouldDisplayProductImages = MutableStateFlow<Boolean>(false)
    //val shouldDisplayProductImages : StateFlow<Boolean> get() = _shouldDisplayProductImages.asStateFlow()

    // when user clicks view product images, or view asking product images
    // we retrieve the images and put it here
    // so the images display screen can retrieve it here
    // that way, we can display both product images and asking product images
    // both in one images display screen
    val _imagesDisplay = MutableStateFlow<MutableList<ProductImage>>(mutableListOf())
    val imagesDisplay : StateFlow<MutableList<ProductImage>> get() = _imagesDisplay.asStateFlow()

    val _productImages = MutableStateFlow<MutableList<ProductImage>>(mutableListOf())
    val productImages : StateFlow<MutableList<ProductImage>> get() = _productImages.asStateFlow()

    val _askingImages = MutableStateFlow<MutableList<ProductImage>>(mutableListOf())
    val askingImages : StateFlow<MutableList<ProductImage>> get() = _askingImages.asStateFlow()

    fun updateShouldDisplayImages(should: Boolean) {
        _shouldDisplayImages.value = should
    }

    fun updateShouldPopImages(should: Boolean) {
        _shouldPopImages.value = should
    }

    fun updateImages(images: MutableList<ProductImage>) {
        _imagesDisplay.value = images
    }

    fun updateShouldDisplayAskingProducts(should: Boolean) {
        _shouldDisplayAskingProducts.value = should
    }

    // let me think how and when to retrieve the images from cloud storage
    // and store as bitmaps in this view model

    fun prepareImages(type: ImageType, imagesUrl: List<String>, context: Context) {
        // retrieve images from cloud storage and store in view model
        // we need to do like this because Images Display Screen's setup
        // can't be customized to use Glide to load images as needed
        //val images = mutableListOf<ProductImage?>()

        for (i in 0..imagesUrl.size - 1) {
            //loadImage(url = imagesUrl[i])
            //images.add(loadImage(imagesUrl[i], context)
            // so before we load the image, we show the placeholder image
            _imagesDisplay.value.add(i, ProductImage(UUID.randomUUID().toString(), createPlaceholderImage(context)))
            CoroutineScope(Dispatchers.IO).launch {
                //createPlaceholderImage()
                loadImage(imagesUrl[i], context)?.let {
                    _imagesDisplay.value.set(i, ProductImage(i.toString(), it))
                }
            }
        }

    }



    fun deleteImage(image: ProductImage) {
        Log.i("askingVM", "got image")
        //val newList = imagesDisplay.value.toMutableList()
        //newList.remove(image)
        //_imagesDisplay.value = newList
    }

    // show images as soon as it is loaded


    fun prepareAskingProducts() {
        ProductInfo.productChosen.value?.let {
            Log.i("product details vM", "product is not null")
            ProductInfo.updateAskingProducts(it.askingProducts.askingList)
        }
    }
}