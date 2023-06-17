package com.bitpunchlab.android.barter.productOfferingDetails

import com.bitpunchlab.android.barter.models.Bid
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object BidInfo {

    private val _bids = MutableStateFlow<List<Bid>>(listOf())
    val bids : StateFlow<List<Bid>> get() = _bids.asStateFlow()

    val _bid = MutableStateFlow<Bid?>(null)
    val bid : StateFlow<Bid?> get() = _bid.asStateFlow()

    fun updateBid(bid: Bid) {
        _bid.value = bid
    }

    fun updateBids(bidList: List<Bid>) {
        _bids.value = bidList
    }
}