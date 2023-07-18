package com.bitpunchlab.android.barter.productOfferingDetails

import android.util.Log
import androidx.lifecycle.ViewModel
import com.bitpunchlab.android.barter.firebase.FirebaseClient
import com.bitpunchlab.android.barter.util.LocalDatabaseManager
import com.bitpunchlab.android.barter.util.ProductImage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class ProductOfferingBidDetailsViewModel : ViewModel() {

    private val _imagesDisplay = MutableStateFlow<List<ProductImage>>(listOf())
    val imagesDisplay : StateFlow<List<ProductImage>> get() = _imagesDisplay.asStateFlow()

    private val _shouldDisplayImages = MutableStateFlow<Boolean>(false)
    val shouldDisplayImages : StateFlow<Boolean> get() = _shouldDisplayImages.asStateFlow()

    private val _shouldPopImages = MutableStateFlow<Boolean>(false)
    val shouldPopImages : StateFlow<Boolean> get() = _shouldPopImages.asStateFlow()

    private val _shouldShowBid = MutableStateFlow<Boolean>(false)
    val shouldShowBid : StateFlow<Boolean> get() = _shouldShowBid.asStateFlow()

    private val _acceptBidStatus = MutableStateFlow<Int>(0)
    val acceptBidStatus : StateFlow<Int> get() = _acceptBidStatus.asStateFlow()

    init {
        CoroutineScope(Dispatchers.IO).launch {
            LocalDatabaseManager.bidProductImages.collect() {
                if (it.isNotEmpty()) {
                    Log.i("bid detail vm", "images transferred for display")
                    _imagesDisplay.value = it
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
    fun updateAcceptBidStatus(status: Int) {
        _acceptBidStatus.value = status
    }

    fun updateShouldShowBid(should: Boolean) {
        _shouldShowBid.value = should
    }

    fun deleteImage(image: ProductImage) {
        Log.i("askingVM", "got image")
        //val newList = imagesDisplay.value.toMutableList()
        //newList.remove(image)
        //_imagesDisplay.value = newList
    }


    // we retrieve the product bidding object stored in the local database
    // it should be retrieved from the server together with the other products bidding.
    fun acceptBid() {
        CoroutineScope(Dispatchers.IO).launch {
            combine(LocalDatabaseManager.productChosen, LocalDatabaseManager.bidChosen) { product, bid ->
                if (bid != null && product != null) {
                    if (FirebaseClient.processAcceptBid(product, bid)) {
                        updateAcceptBidStatus(3)
                    } else {
                        updateAcceptBidStatus(4)
                    }
                } else {
                    Log.i("accept bid", "product info not available")
                    updateAcceptBidStatus(5)
                }
            }
        }
    }

}