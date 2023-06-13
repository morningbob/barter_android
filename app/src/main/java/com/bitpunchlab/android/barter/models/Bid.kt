package com.bitpunchlab.android.barter.models

import android.os.Parcelable
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity
@Parcelize
data class Bid(
    @PrimaryKey
    val id : String,
    val userName : String,
    // the asking product that the bidder offers
    @Embedded
    val askingProduct : ProductAsking? = null,
    val bidTime : String

) : Parcelable

data class BidsHolder(
    val bids : List<Bid>
)