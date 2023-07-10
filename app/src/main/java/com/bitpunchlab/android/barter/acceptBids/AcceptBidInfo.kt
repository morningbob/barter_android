package com.bitpunchlab.android.barter.acceptBids

import com.bitpunchlab.android.barter.models.AcceptBid
import com.bitpunchlab.android.barter.models.BidWithDetails
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object AcceptBidInfo {

    val _acceptBid = MutableStateFlow<BidWithDetails?>(null)
    val acceptBid : StateFlow<BidWithDetails?> get() = _acceptBid.asStateFlow()

    fun updateAcceptBid(bid: BidWithDetails) {
        _acceptBid.value = bid
    }
}