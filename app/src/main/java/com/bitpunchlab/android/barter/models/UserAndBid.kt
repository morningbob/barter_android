package com.bitpunchlab.android.barter.models

import androidx.room.Embedded
import androidx.room.Relation

data class UserAndBid(
    @Embedded val user : User,
    @Relation(
        parentColumn = "id",
        entityColumn = "bidUserId"
    )

    val currentBids : List<Bid> = listOf()
)