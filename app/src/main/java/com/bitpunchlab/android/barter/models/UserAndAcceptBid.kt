package com.bitpunchlab.android.barter.models

import androidx.room.Embedded
import androidx.room.Relation

data class UserAndAcceptBid(
    @Embedded
    val user : User,
    @Relation(
        parentColumn = "id",
        entityColumn = "userId"
    )
    val acceptBids : List<AcceptBid>
)