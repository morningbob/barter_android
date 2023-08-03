package com.bitpunchlab.android.barter.models

import androidx.room.Embedded
import androidx.room.Relation


data class UserAndProductOffering(
    @Embedded val user: User,
    @Relation(
        parentColumn = "id",
        entityColumn = "userId"
    )
    var productsOffering: List<ProductOffering> = listOf()
)