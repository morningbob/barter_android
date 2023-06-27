package com.bitpunchlab.android.barter.transactionRecords

import android.util.Log
import androidx.lifecycle.ViewModel
import com.bitpunchlab.android.barter.ImageHandler
import com.bitpunchlab.android.barter.util.ProductImage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.UUID

class RecordDetailsViewModel : ViewModel() {

    val _productOfferingImages = MutableStateFlow<MutableList<ProductImage>>(mutableListOf())
    val productOfferingImages : StateFlow<List<ProductImage>> get() = _productOfferingImages.asStateFlow()

    val _productInExchangeImages = MutableStateFlow<MutableList<ProductImage>>(mutableListOf())
    val productInExchangeImages : StateFlow<List<ProductImage>> get() = _productInExchangeImages.asStateFlow()

    val _shouldDisplayImages = MutableStateFlow<Boolean>(false)
    val shouldDisplayImages : StateFlow<Boolean> get() = _shouldDisplayImages.asStateFlow()

    val _shouldPopImages = MutableStateFlow<Boolean>(false)
    val shouldPopImages : StateFlow<Boolean> get() = _shouldPopImages.asStateFlow()

    private val _imagesDisplay = MutableStateFlow<List<ProductImage>>(listOf())
    val imagesDisplay : StateFlow<List<ProductImage>> get() = _imagesDisplay.asStateFlow()

    init {
        CoroutineScope(Dispatchers.IO).launch {
            RecordInfo.recordChosen.collect() { record ->
                record?.let {
                    for (i in 0..record.acceptProductInConcern.images.size - 1) {
                        _productOfferingImages.value.add(
                            ProductImage(
                                UUID.randomUUID().toString(),
                                ImageHandler.createPlaceholderImage()
                            )
                        )
                    }

                    ImageHandler.loadedImagesFlow(record.acceptProductInConcern.images).collect() { pairResult ->
                        _productOfferingImages.value.set(pairResult.first, pairResult.second)
                    }
                    Log.i("loading images", "images number ${record.acceptBid.bidProduct!!.productImages.size}")
                    for (i in 0..record.acceptBid.bidProduct!!.productImages.size - 1) {

                        _productInExchangeImages.value.add(
                            ProductImage(
                                UUID.randomUUID().toString(),
                                ImageHandler.createPlaceholderImage()
                            )
                        )
                    }

                    ImageHandler.loadedImagesFlow(record.acceptBid.bidProduct.productImages).collect() { pairResult ->
                        _productInExchangeImages.value.set(pairResult.first, pairResult.second)
                    }
                }
            }
        }
    }


    fun updateShouldDisplayImages(should: Boolean) {
        _shouldDisplayImages.value = should
    }

    fun updateShouldPopImages(should: Boolean) {
        _shouldPopImages.value = should
    }

    fun prepareImagesDisplay(images: List<ProductImage>) {
        _imagesDisplay.value = images
    }

    fun deleteImage(image: ProductImage) {

    }
}