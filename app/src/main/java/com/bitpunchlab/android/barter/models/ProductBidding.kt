package com.bitpunchlab.android.barter.models

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

// the reason I created product bidding, is that, the product offering might expose
// too much information about the user who offer the product.
// product bidding just hold enough info for users to bid
// the products available for bidding, will be stored as product bidding and download to
// the users' devices.  We store the product offering instance in the database.
// but the products offered by the user, will be in product offering objects form.
@Entity(tableName = "products_bidding")
data class ProductBidding(
    @PrimaryKey
    val productId : String,
    val productOfferingId : String,
    val name : String,
    val category: String,
    val dateCreated : String,
    val durationLeft: Int,
    var images : List<String>,
    @Embedded
    var bids : BidsHolder,

)