package com.bitpunchlab.android.barter.models

import android.os.Parcelable
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "bids")
@Parcelize
data class Bid(
    @PrimaryKey
    val bidId : String,
    val bidAcceptId : String = "",
    val bidUserName : String,
    val bidUserId : String,
    // the asking product that the bidder offers
    @Embedded
    val bidProduct : ProductAsking,
    val bidProductId : String,
    val bidTime : String,
    val acceptBidId : String = ""
) : Parcelable

@Parcelize
data class BidsHolder(
    val bids : List<Bid>
) : Parcelable