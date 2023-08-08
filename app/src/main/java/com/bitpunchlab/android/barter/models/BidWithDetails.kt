package com.bitpunchlab.android.barter.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

// the class just make the
// access of product and bid of the corresponding accept bid more easily
@Parcelize
data class BidWithDetails(
    var acceptBid: AcceptBid,
    var product : ProductOffering,
    var bid : Bid
) : Parcelable