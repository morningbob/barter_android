package com.bitpunchlab.android.barter.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products_bidding")
data class ProductBidding(
    @PrimaryKey
    val productId : String,
    val ownerId : String,
    val name : String,
    val category: String,
    //var timeRemaining: String,
    var images : List<String>

)