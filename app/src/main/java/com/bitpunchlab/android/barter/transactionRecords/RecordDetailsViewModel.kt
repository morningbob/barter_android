package com.bitpunchlab.android.barter.transactionRecords

import androidx.lifecycle.ViewModel
import com.bitpunchlab.android.barter.util.ProductImage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class RecordDetailsViewModel : ViewModel() {

    private val _productOfferingImages = MutableStateFlow<MutableList<ProductImage>>(mutableListOf())
    val productOfferingImages : StateFlow<MutableList<ProductImage>> get() = _productOfferingImages.asStateFlow()

    private val _productInExchangeImages = MutableStateFlow<MutableList<ProductImage>>(mutableListOf())
    val productInExchangeImages : StateFlow<MutableList<ProductImage>> get() = _productInExchangeImages.asStateFlow()

    private val _shouldDisplayImages = MutableStateFlow<Boolean>(false)
    val shouldDisplayImages : StateFlow<Boolean> get() = _shouldDisplayImages.asStateFlow()

    private val _shouldPopImages = MutableStateFlow<Boolean>(false)
    val shouldPopImages : StateFlow<Boolean> get() = _shouldPopImages.asStateFlow()

    private val _imagesDisplay = MutableStateFlow<List<ProductImage>>(listOf())
    val imagesDisplay : StateFlow<List<ProductImage>> get() = _imagesDisplay.asStateFlow()

    init {
        /*
        CoroutineScope(Dispatchers.IO).launch {
            RecordInfo.recordChosen.collect() { record ->
                record?.let {
                    // a coroutine is required for the first collect won't end the second collect
                    CoroutineScope(Dispatchers.IO).launch {
                        for (i in 0..record.acceptProductInConcern.images.size - 1) {
                            _productOfferingImages.value.add(
                                ProductImage(
                                    UUID.randomUUID().toString(),
                                    ImageHandler.createPlaceholderImage()
                                )
                            )
                        }

                        ImageHandler.loadedImagesFlow(record.acceptProductInConcern.images)
                            .collect() { pairResult ->
                                _productOfferingImages.value.set(
                                    pairResult.first,
                                    pairResult.second
                                )
                            }
                    }
                    Log.i("loading images", "images number ${record.acceptBid.bidProduct!!.images.size}")
                    CoroutineScope(Dispatchers.IO).launch {
                        for (i in 0..record.acceptBid.bidProduct!!.images.size - 1) {
                            _productInExchangeImages.value.add(
                                ProductImage(
                                    UUID.randomUUID().toString(),
                                    ImageHandler.createPlaceholderImage()
                                )
                            )
                        }

                        ImageHandler.loadedImagesFlow(record.acceptBid.bidProduct!!.images)
                            .collect() { pairResult ->
                                _productInExchangeImages.value.set(
                                    pairResult.first,
                                    pairResult.second
                                )
                            }
                    }
                }
            }
        }

         */
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