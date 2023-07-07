package com.bitpunchlab.android.barter.models

// the class just make the access of product and bid of the corresponding accept bid more easily
data class BidWithDetails(
    var acceptBid: AcceptBid,
    var product : ProductOffering,
    var bid : Bid
)