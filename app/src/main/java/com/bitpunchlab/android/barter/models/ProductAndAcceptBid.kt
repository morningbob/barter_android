package com.bitpunchlab.android.barter.models

import androidx.room.Embedded
import androidx.room.Relation

data class ProductAndAcceptBid(
    @Embedded
    val productId : String,
    @Relation(
        parentColumn = "productId",
        entityColumn = "productId"
    )
    val acceptBid : AcceptBid
)