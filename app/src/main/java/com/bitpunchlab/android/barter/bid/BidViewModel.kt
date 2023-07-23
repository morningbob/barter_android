package com.bitpunchlab.android.barter.bid

import android.util.Log
import androidx.lifecycle.ViewModel
import com.bitpunchlab.android.barter.util.ImageHandler
import com.bitpunchlab.android.barter.firebase.FirebaseClient
import com.bitpunchlab.android.barter.models.Bid
import com.bitpunchlab.android.barter.models.ProductImageToDisplay
import com.bitpunchlab.android.barter.models.ProductOffering
import com.bitpunchlab.android.barter.productsOfferingList.ProductInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class BidViewModel : ViewModel() {

    private val _bid = MutableStateFlow<Bid?>(null)
    val bid : StateFlow<Bid?> get() = _bid.asStateFlow()

    private val _imagesDisplay = MutableStateFlow<MutableList<ProductImageToDisplay>>(mutableListOf())
    val imagesDisplay : StateFlow<MutableList<ProductImageToDisplay>> get() = _imagesDisplay.asStateFlow()

    // 1 is failed, 2 is succeeded, 3 is invalid info
    private val _biddingStatus = MutableStateFlow<Int>(0)
    val biddingStatus : StateFlow<Int> get() = _biddingStatus.asStateFlow()

    private val _shouldDisplayImages = MutableStateFlow<Boolean>(false)
    val shouldDisplayImages : StateFlow<Boolean> get() = _shouldDisplayImages.asStateFlow()

    private val _shouldPopImages = MutableStateFlow(false)
    val shouldPopImages : StateFlow<Boolean> get() = _shouldPopImages.asStateFlow()

    private val _shouldPopBid = MutableStateFlow(false)
    val shouldPopBid : StateFlow<Boolean> get() = _shouldPopBid.asStateFlow()

    private val _shouldStartBid = MutableStateFlow(false)
    val shouldStartBid : StateFlow<Boolean> get() = _shouldStartBid.asStateFlow()

    private val _shouldCancel = MutableStateFlow(false)
    val shouldCancel : StateFlow<Boolean> get() = _shouldCancel.asStateFlow()

    private val _loadingAlpha = MutableStateFlow(0f)
    val loadingAlpha : StateFlow<Float> get() = _loadingAlpha.asStateFlow()

    private val _deleteImageStatus = MutableStateFlow(0)
    val deleteImageStatus : StateFlow<Int> get() = _deleteImageStatus.asStateFlow()

    init {
        CoroutineScope(Dispatchers.IO).launch {
            ProductInfo.productChosen.collect() { product ->
                product?.let {
                    for (i in 0..product.images.size - 1) {
                        _imagesDisplay.value.add(
                            ProductImageToDisplay(
                                UUID.randomUUID().toString(),
                                ImageHandler.createPlaceholderImage(),
                                ""
                            )
                        )
                    }

                    ImageHandler.loadedImagesFlow(product.images).collect() { pairResult ->
                        _imagesDisplay.value.set(pairResult.first, pairResult.second)
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

    fun updateShouldPopBid(should: Boolean) {
        _shouldPopBid.value = should
    }

    fun updateShouldStartBid(should: Boolean) {
        _shouldStartBid.value = should
    }

    fun updateShouldCancel(should: Boolean) {
        _shouldCancel.value = should
    }

    fun updateBid(newBid: Bid) {
        _bid.value = newBid
    }

    fun updateBiddingStatus(status: Int) {
        _biddingStatus.value = status
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

    fun processBidding(product: ProductOffering, bid: Bid, images: List<ProductImageToDisplay>)  {
        _loadingAlpha.value = 100f

        val imagesBitmap = images.map { image ->
            image.image
        }

        Log.i("bid vm", "processing bid")

        //return CoroutineScope(Dispatchers.IO).launch {
         CoroutineScope(Dispatchers.IO).launch {
            //if (ProductBiddingInfo.product.value != null) {
            Log.i("bidVM", "process bidding")
            if (FirebaseClient.processBidding(product, bid, imagesBitmap)) {
                _biddingStatus.value = 2
                _loadingAlpha.value = 0f
            } else {
                _biddingStatus.value = 1
                _loadingAlpha.value = 0f
            }
         }
    }

}
/*
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
*/