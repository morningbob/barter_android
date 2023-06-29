package com.bitpunchlab.android.barter.models

import androidx.room.Embedded
import androidx.room.Relation

data class ProductOfferingAndBid(
    @Embedded
    val productOffering : ProductOffering,
    @Relation(
        parentColumn = "productId",
        entityColumn = "bidProductId"
    )
    val bids : List<Bid>
)