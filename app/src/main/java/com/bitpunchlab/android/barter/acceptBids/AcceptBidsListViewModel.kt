package com.bitpunchlab.android.barter.acceptBids

import android.util.Log
import androidx.lifecycle.ViewModel
import com.bitpunchlab.android.barter.database.BarterRepository
import com.bitpunchlab.android.barter.firebase.FirebaseClient
import com.bitpunchlab.android.barter.models.AcceptBid
import com.bitpunchlab.android.barter.models.BidAndAcceptBid
import com.bitpunchlab.android.barter.models.BidWithDetails
import com.bitpunchlab.android.barter.models.ProductImageToDisplay
import com.bitpunchlab.android.barter.util.ImageHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import java.util.UUID

class AcceptBidsListViewModel : ViewModel() {
    val _acceptBids = MutableStateFlow<List<AcceptBid>>(listOf())
    val acceptBids : StateFlow<List<AcceptBid>> get() = _acceptBids.asStateFlow()

    val _acceptedBids = MutableStateFlow<MutableList<AcceptBid>>(mutableListOf())
    val acceptedBids : StateFlow<MutableList<AcceptBid>> get() = _acceptedBids.asStateFlow()

    val _bidsAccepted = MutableStateFlow<MutableList<AcceptBid>>(mutableListOf())
    val bidsAccepted : StateFlow<MutableList<AcceptBid>> get() = _bidsAccepted.asStateFlow()

    val _bidsDetail = MutableStateFlow<MutableList<BidWithDetails>>(mutableListOf())
    val bidsDetail : StateFlow<MutableList<BidWithDetails>> get() = _bidsDetail.asStateFlow()

    val _acceptedBidsDetail = MutableStateFlow<MutableList<BidWithDetails>>(mutableListOf())
    val acceptedBidsDetail : StateFlow<MutableList<BidWithDetails>> get() = _acceptedBidsDetail.asStateFlow()

    val _bidsAcceptedDetail = MutableStateFlow<MutableList<BidWithDetails>>(mutableListOf())
    val bidsAcceptedDetail : StateFlow<MutableList<BidWithDetails>> get() = _bidsAcceptedDetail.asStateFlow()

    val _shouldDisplayDetails = MutableStateFlow<Boolean>(false)
    val shouldDisplayDetails : StateFlow<Boolean> get() = _shouldDisplayDetails.asStateFlow()

    private val _shouldDisplayImages = MutableStateFlow<Boolean>(false)
    val shouldDisplayImages : StateFlow<Boolean> get() = _shouldDisplayImages.asStateFlow()

    private val _shouldPopImages = MutableStateFlow<Boolean>(false)
    val shouldPopImages : StateFlow<Boolean> get() = _shouldPopImages.asStateFlow()

    private val _imagesDisplay = MutableStateFlow<List<ProductImageToDisplay>>(listOf())
    val imagesDisplay : StateFlow<List<ProductImageToDisplay>> get() = _imagesDisplay.asStateFlow()

    init {
        //_bidsDetail.value = acceptedBidsDetail.value
        prepareBidDetails()
        // retrieve all accepted bids and put them in appropriate bids list
        CoroutineScope(Dispatchers.IO).launch {
            FirebaseClient.userId.collect() { id ->
                Log.i("accept bid list vm", "got user id")
                val userAcceptBidsFlow = CoroutineScope(Dispatchers.IO).async {
                    BarterRepository.getUserAndAcceptBids(id)
                }.await()
                userAcceptBidsFlow?.collect() { userAcceptBids ->
                    Log.i("accept bid list vm", "userAcceptedBids ${userAcceptBids.size}")
                    Log.i("accept bid list vm", "got no accept bids: ${userAcceptBids[0].acceptBids.size}")
                    _acceptBids.value = userAcceptBids[0].acceptBids
                    //prepareBidDetails(userAcceptBids[0].acceptBids)
                }
            }
        }
    }

    // retrieve the bid's details from AcceptBidAndProduct, and AcceptBidAndBid
    private fun prepareBidDetails() {
        CoroutineScope(Dispatchers.IO).launch {
            acceptBids.collect() { theBids ->
                for (theBid in theBids) {
                    Log.i("accept bid view model", "processing the bid ${theBid.acceptId}")
                    CoroutineScope(Dispatchers.IO).launch {
                        val bidProductDeferred = CoroutineScope(Dispatchers.IO).async {
                            BarterRepository.getAcceptBidAndProductById(theBid.acceptId)
                        }
                        val bidBidDeferred = CoroutineScope(Dispatchers.IO).async {
                            BarterRepository.getAcceptBidAndBidById(theBid.acceptId)
                        }
                        val product = bidProductDeferred.await()?.get(0)?.product
                        val bid = bidBidDeferred.await()?.get(0)?.bid
                        if (product != null && bid != null) {
                            val bidDetails = BidWithDetails(
                                acceptBid = theBid,
                                product = product,
                                bid = bid
                            )
                            if (theBid.isSeller) {
                                //_acceptedBidsDetail.value.add(bidDetails)
                                _bidsDetail.value.add(bidDetails)
                            } else {
                                _bidsAcceptedDetail.value.add(bidDetails)
                            }
                        }
                    }
                }
            }
        }
    }

    fun updateShouldDisplayDetails(should: Boolean) {
        _shouldDisplayDetails.value = should
    }

    fun updateShouldDisplayImages(should: Boolean) {
        _shouldDisplayImages.value = should
    }

    fun updateShouldPopImages(should: Boolean) {
        _shouldPopImages.value = should
    }

    fun prepareImagesDisplay(images: List<ProductImageToDisplay>) {
        _imagesDisplay.value = images
    }

}