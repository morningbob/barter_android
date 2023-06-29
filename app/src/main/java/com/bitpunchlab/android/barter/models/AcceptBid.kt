package com.bitpunchlab.android.barter.models

import android.os.Parcelable
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "accept_bids")
@Parcelize
data class AcceptBid(
    @PrimaryKey
    val acceptId : String,
    @Embedded
    val acceptProductInConcern: ProductBidding,
    @Embedded
    val acceptBid: Bid
) : Parcelable