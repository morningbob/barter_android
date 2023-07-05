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
    //val productId : String,
    //val bidId : String
) : Parcelable
/*
@Parcelize
data class AcceptBid(
    @PrimaryKey
    val acceptId : String,
    val productName : String,
    val productCategory: String,
    val productId : String,
    val productSellerName : String,
    val productImages : List<String>,
    val productBidName : String,
    val productBidCategory : String,
    val productBidderName : String,
    val productBidImages : List<String>,
    val bidDate : String
    //@Embedded
    //val acceptProductInConcern: ProductOffering,
    //@Embedded
    //val acceptBid: Bid
) : Parcelable

@Entity(tableName = "accept_bids")
data class AcceptBid2(
    @PrimaryKey
    val
)

 */