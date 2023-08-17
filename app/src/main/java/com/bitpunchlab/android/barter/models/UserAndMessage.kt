package com.bitpunchlab.android.barter.models

import androidx.room.Embedded
import androidx.room.Relation

data class UserAndMessage(
    @Embedded
    val user : User,

    @Relation(
        parentColumn = "id",
        entityColumn = "ownerUserId"
    )
    val messages: List<Message> = listOf()
)