package com.bitpunchlab.android.barter.productOfferingDetails

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import com.bitpunchlab.android.barter.util.ImageHandler
import com.bitpunchlab.android.barter.firebase.FirebaseClient
import com.bitpunchlab.android.barter.models.Bid
import com.bitpunchlab.android.barter.models.ProductImageToDisplay
import com.bitpunchlab.android.barter.models.ProductOffering
import com.bitpunchlab.android.barter.productsOfferingList.ProductInfo
import com.bitpunchlab.android.barter.util.AcceptBidStatus
import com.bitpunchlab.android.barter.util.LocalDatabaseManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class ProductOfferingBidsListViewModel : ViewModel() {

    private val _bid = MutableStateFlow<Bid?>(null)
    val bid : StateFlow<Bid?> get() = _bid.asStateFlow()

    private val _product = MutableStateFlow<ProductOffering?>(null)
    val product : StateFlow<ProductOffering?> get() = _product.asStateFlow()

    private val _imagesDisplay = MutableStateFlow<SnapshotStateList<ProductImageToDisplay>>(mutableStateListOf())
    val imagesDisplay : StateFlow<SnapshotStateList<ProductImageToDisplay>> get() = _imagesDisplay.asStateFlow()

    private val _shouldShowBid = MutableStateFlow<Boolean>(false)
    val shouldShowBid : StateFlow<Boolean> get() = _shouldShowBid.asStateFlow()

    private val _shouldDisplayImages = MutableStateFlow<Boolean>(false)
    val shouldDisplayImages : StateFlow<Boolean> get() = _shouldDisplayImages.asStateFlow()

    private val _shouldPopImages = MutableStateFlow<Boolean>(false)
    val shouldPopImages : StateFlow<Boolean> get() = _shouldPopImages.asStateFlow()

    // this variable is used to pop off the current screen as cross cancelled
    private val _shouldPopBids = MutableStateFlow<Boolean>(false)
    val shouldPopBids : StateFlow<Boolean> get() = _shouldPopBids.asStateFlow()

    init {
        CoroutineScope(Dispatchers.IO).launch {
            LocalDatabaseManager.bidProductImages.collect() {
                if (it.isNotEmpty()) {
                    //Log.i("bid list vm", "images transferred for display")
                    _imagesDisplay.value = it
                }
            }
        }
    }

    fun updateImagesDisplay(images: SnapshotStateList<ProductImageToDisplay>) {
        _imagesDisplay.value = images
    }

    fun updateBid(bid: Bid) {
        _bid.value = bid
    }

    fun updateShouldShowBid(should: Boolean) {
        _shouldShowBid.value = should
    }

    fun updateShouldDisplayImages(should: Boolean) {
        _shouldDisplayImages.value = should
    }

    fun updateShouldPopImages(should: Boolean) {
        _shouldPopImages.value = should
    }

    fun updateShouldPopBids(should: Boolean) {
        _shouldPopBids.value = should
    }

    fun deleteImage(image: ProductImageToDisplay) {
        Log.i("askingVM", "got image")
        //val newList = imagesDisplay.value.toMutableList()
        //newList.remove(image)
        //_imagesDisplay.value = newList
    }

}
/*
    init {
        CoroutineScope(Dispatchers.IO).launch {
            bid.collect() { theBid ->
                theBid?.let {
                    val images = mutableListOf<ProductImage>()
                    theBid.bidProduct?.images?.map { image ->
                        val bitmap = loadImage(image, )
                            images.add(ProductImage(id = UUID.randomUUID().toString(), ))
                    }
                }
            }
        }
    }

 */
/*
    fun prepareImages(imagesUrl: List<String>, context: Context) {
        // retrieve images from cloud storage and store in view model
        // we need to do like this because Images Display Screen's setup
        // can't be customized to use Glide to load images as needed

        for (i in 0..imagesUrl.size - 1) {
            // so before we load the image, we show the placeholder image
            _imagesDisplay.value.add(i, ProductImage(UUID.randomUUID().toString(), createPlaceholderImage(context)))
            CoroutineScope(Dispatchers.IO).launch {
                loadImage(imagesUrl[i], context)?.let {
                    _imagesDisplay.value.set(i, ProductImage(i.toString(), it))
                }
            }
        }

    }

 */
/*
           ProductInfo.productChosen.collect() { productChosen ->
               productChosen?.let {
                   _product.value = productChosen
                   // we initialize the placeholders
                   for (i in 0..productChosen.images.size - 1) {
                       _imagesDisplay.value.add(
                           ProductImage(
                               UUID.randomUUID().toString(),
                               ImageHandler.createPlaceholderImage()
                           )
                       )
                   }

                   // we start to load the images for the product chosen here
                   ImageHandler.loadedImagesFlow(productChosen.images).collect()  { pairResult ->
                       _imagesDisplay.value.set(pairResult.first, pairResult.second)
                   }
               }
           }

            */
