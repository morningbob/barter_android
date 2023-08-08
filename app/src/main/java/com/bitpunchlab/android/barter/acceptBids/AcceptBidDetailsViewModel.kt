package com.bitpunchlab.android.barter.acceptBids

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import com.bitpunchlab.android.barter.models.ProductImageToDisplay
import com.bitpunchlab.android.barter.util.ImageHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class AcceptBidDetailsViewModel : ViewModel() {

    private val _productOfferingImages = MutableStateFlow<MutableList<ProductImageToDisplay>>(mutableListOf())
    val productOfferingImages : StateFlow<MutableList<ProductImageToDisplay>> get() = _productOfferingImages.asStateFlow()

    private val _productInExchangeImages = MutableStateFlow<MutableList<ProductImageToDisplay>>(mutableListOf())
    val productInExchangeImages : StateFlow<MutableList<ProductImageToDisplay>> get() = _productInExchangeImages.asStateFlow()

    val _shouldPopSelf = MutableStateFlow<Boolean>(false)
    val shouldPopSelf : StateFlow<Boolean> get() = _shouldPopSelf.asStateFlow()

    private val _shouldDisplayImages = MutableStateFlow<Boolean>(false)
    val shouldDisplayImages : StateFlow<Boolean> get() = _shouldDisplayImages.asStateFlow()

    private val _shouldPopImages = MutableStateFlow<Boolean>(false)
    val shouldPopImages : StateFlow<Boolean> get() = _shouldPopImages.asStateFlow()

    private val _imagesDisplay = MutableStateFlow<SnapshotStateList<ProductImageToDisplay>>(
        mutableStateListOf()
    )
    val imagesDisplay : StateFlow<SnapshotStateList<ProductImageToDisplay>> get() = _imagesDisplay.asStateFlow()

    private val _deleteImageStatus = MutableStateFlow(0)
    val deleteImageStatus : StateFlow<Int> get() = _deleteImageStatus.asStateFlow()

    init {
        CoroutineScope(Dispatchers.IO).launch {
            AcceptBidInfo.acceptBid.collect() { bidWithDetails ->
                bidWithDetails?.let {
                    retrieveImages(
                        bidWithDetails.product.images,
                        bidWithDetails.bid.bidProduct.images
                    )
                }
            }
        }
    }

    private suspend fun retrieveImages(productImageList: List<String>, exchangedImageList: List<String>) {
        for (i in 0..productImageList.size - 1) {
            _productOfferingImages.value.add(
                ProductImageToDisplay(

                    //image = ImageHandler.createPlaceholderImage(),
                    imageUrlCloud = "",
                    //"",
                    //imageId = UUID.randomUUID().toString(),
                )
            )
        }

        for (i in 0..exchangedImageList.size - 1) {
            _productInExchangeImages.value.add(
                ProductImageToDisplay(
                    //imageId = UUID.randomUUID().toString(),
                    //image = ImageHandler.createPlaceholderImage(),
                    imageUrlCloud = ""
                )
            )
        }

        CoroutineScope(Dispatchers.IO).launch {
            // we start to load the images for the product chosen here
            ImageHandler.loadedImagesFlow(productImageList).collect() { pairResult ->
                _productOfferingImages.value.set(pairResult.first, pairResult.second)
            }
        }
        CoroutineScope(Dispatchers.IO).launch {
            // we start to load the images for the product chosen here
            ImageHandler.loadedImagesFlow(exchangedImageList).collect() { pairResult ->
                _productInExchangeImages.value.set(pairResult.first, pairResult.second)
            }
        }
    }

    fun updateShouldPopSelf(should: Boolean) {
        _shouldPopSelf.value = should
    }

    fun updateShouldDisplayImages(should: Boolean) {
        _shouldDisplayImages.value = should
    }

    fun updateShouldPopImages(should: Boolean) {
        _shouldPopImages.value = should
    }

    fun prepareImagesDisplay(images: List<ProductImageToDisplay>) {
        _imagesDisplay.value = images.toMutableStateList()
    }

    fun deleteImage(image: ProductImageToDisplay) {
        //Log.i("askingVM", "got image")
        //val newList = imagesDisplay.value.toMutableList()
        //newList.remove(image)
        //_imagesDisplay.value = newList
    }

    fun updateDeleteImageStatus(status: Int) {
        _deleteImageStatus.value = status
    }
}