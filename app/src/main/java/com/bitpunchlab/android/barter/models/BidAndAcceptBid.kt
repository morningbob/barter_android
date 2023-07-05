package com.bitpunchlab.android.barter.models

import androidx.room.Embedded
import androidx.room.Relation

data class BidAndAcceptBid(
    @Embedded
    val bidId : String,
    @Relation(
        parentColumn = "bidId",
        entityColumn = "bidId"
    )

    val acceptBid: AcceptBid
)