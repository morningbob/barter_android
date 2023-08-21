package com.bitpunchlab.android.barter.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "messages")
@Parcelize
data class Message(
    @PrimaryKey
    val id: String,
    val messageText: String,
    // this is to use to distinguish if this is a sender or receiver message for the user
    // this is the id of the user of the app
    // for the database to hook the messages for the user
    val ownerUserId: String,
    val otherUserId: String,
    val sender: Boolean,
    val ownerName: String,
    val otherName: String,
    val date: String
) : Parcelable