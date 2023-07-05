package com.bitpunchlab.android.barter.models

import androidx.room.Embedded
import androidx.room.Relation

data class AcceptBidAndProduct(
    @Embedded
    val acceptBid: AcceptBid,
    @Relation(
        parentColumn = "acceptId",
        entityColumn = "acceptBidId"
    )
    val product : ProductOffering
)