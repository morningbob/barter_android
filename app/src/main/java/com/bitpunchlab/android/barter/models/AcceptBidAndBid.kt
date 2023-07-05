package com.bitpunchlab.android.barter.models

import androidx.room.Embedded
import androidx.room.Relation


data class AcceptBidAndBid(
    @Embedded
    val acceptBid: AcceptBid,
    @Relation(
        parentColumn = "acceptId",
        entityColumn = "bidAcceptId"
    )
    val bid: Bid
)