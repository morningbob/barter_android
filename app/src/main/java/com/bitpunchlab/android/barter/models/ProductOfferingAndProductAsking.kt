package com.bitpunchlab.android.barter.models

import androidx.room.Embedded
import androidx.room.Relation

data class ProductOfferingAndProductAsking(
    @Embedded
    val productOffering : ProductOffering,
    @Relation(
        parentColumn = "productId",
        entityColumn = "productOfferingId"
    )

    val askingProducts : List<ProductAsking>
)