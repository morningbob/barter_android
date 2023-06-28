package com.bitpunchlab.android.barter.models

import android.os.Parcelable
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

// the reason I created product bidding, is that, the product offering might expose
// too much information about the user who offer the product.
// product bidding just hold enough info for users to bid
// the products available for bidding, will be stored as product bidding and download to
// the users' devices.  We store the product offering instance in the database.
// but the products offered by the user, will be in product offering objects form.
@Entity(tableName = "products_bidding")
@Parcelize
data class ProductBidding(
    @PrimaryKey
    val productBidId : String,
    val productOfferingForBid : String,
    val productName : String,
    val ownerName : String,
    val productCategory: String,
    val dateCreated : String,
    val duration: Int,
    @Embedded
    val askingProducts: List<ProductAsking>,
    var productImages : List<String>,
    @Embedded
    var bidsHolder : BidsHolder,

) : Parcelable