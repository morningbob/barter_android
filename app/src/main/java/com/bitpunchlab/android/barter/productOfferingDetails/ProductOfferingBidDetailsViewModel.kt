package com.bitpunchlab.android.barter.productOfferingDetails

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import com.bitpunchlab.android.barter.firebase.FirebaseClient
import com.bitpunchlab.android.barter.models.Bid
import com.bitpunchlab.android.barter.models.ProductImageToDisplay
import com.bitpunchlab.android.barter.models.ProductOffering
import com.bitpunchlab.android.barter.util.AcceptBidStatus
import com.bitpunchlab.android.barter.util.LocalDatabaseManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class ProductOfferingBidDetailsViewModel : ViewModel() {

    private val _imagesDisplay = MutableStateFlow<SnapshotStateList<ProductImageToDisplay>>(
        mutableStateListOf()
    )
    val imagesDisplay : StateFlow<SnapshotStateList<ProductImageToDisplay>> get() = _imagesDisplay.asStateFlow()

    private val _shouldDisplayImages = MutableStateFlow<Boolean>(false)
    val shouldDisplayImages : StateFlow<Boolean> get() = _shouldDisplayImages.asStateFlow()

    private val _shouldPopImages = MutableStateFlow<Boolean>(false)
    val shouldPopImages : StateFlow<Boolean> get() = _shouldPopImages.asStateFlow()

    private val _shouldShowBid = MutableStateFlow<Boolean>(true)
    val shouldShowBid : StateFlow<Boolean> get() = _shouldShowBid.asStateFlow()

    private val _acceptBidStatus = MutableStateFlow<AcceptBidStatus>(AcceptBidStatus.NORMAL)
    val acceptBidStatus : StateFlow<AcceptBidStatus> get() = _acceptBidStatus.asStateFlow()

    private val _deleteImageStatus = MutableStateFlow(0)
    val deleteImageStatus : StateFlow<Int> get() = _deleteImageStatus.asStateFlow()

    private val _productChosen = MutableStateFlow<ProductOffering?>(null)
    val productChosen: StateFlow<ProductOffering?> get() = _productChosen.asStateFlow()

    private val _bidChosen = MutableStateFlow<Bid?>(null)
    val bidChosen: StateFlow<Bid?> get() = _bidChosen.asStateFlow()

    init {
        CoroutineScope(Dispatchers.IO).launch {
            LocalDatabaseManager.bidProductImages.collect() {
                if (it.isNotEmpty()) {
                    Log.i("bid detail vm", "images transferred for display")
                    _imagesDisplay.value = it.toMutableStateList()
                }
            }
        }
        CoroutineScope(Dispatchers.IO).launch {
            LocalDatabaseManager.productChosen.collect() {
                it?.let {
                    Log.i("bid details vm", "got product chosen")
                    _productChosen.value = it
                }
            }
        }
        CoroutineScope(Dispatchers.IO).launch {
            LocalDatabaseManager.bidChosen.collect() {
                it?.let {
                    _bidChosen.value = it
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
    fun updateAcceptBidStatus(status: AcceptBidStatus) {
        _acceptBidStatus.value = status
    }

    fun updateShouldShowBid(should: Boolean) {
        _shouldShowBid.value = should
    }

    fun deleteImage(image: ProductImageToDisplay) {
        Log.i("askingVM", "got image")
        //val newList = imagesDisplay.value.toMutableList()
        //newList.remove(image)
        //_imagesDisplay.value = newList
    }

    fun updateDeleteImageStatus(status: Int) {
        _deleteImageStatus.value = status
    }


    // we retrieve the product bidding object stored in the local database
    // it should be retrieved from the server together with the other products bidding.
    fun acceptBid() {
        CoroutineScope(Dispatchers.IO).launch {
            //combine(LocalDatabaseManager.productChosen, LocalDatabaseManager.bidChosen) { product, bid ->
            if (bidChosen.value != null && productChosen.value != null) {
                Log.i("bid details vm", "bid and product not null")
                if (FirebaseClient.processAcceptBid(productChosen.value!!, bidChosen.value!!)) {
                    updateAcceptBidStatus(AcceptBidStatus.SUCCESS)
                } else {
                    updateAcceptBidStatus(AcceptBidStatus.SERVER_FAILURE)
                }
            } else {
                Log.i("accept bid", "product info not available")
                updateAcceptBidStatus(AcceptBidStatus.APP_FAILURE)
            }
            //}
        }
    }

}