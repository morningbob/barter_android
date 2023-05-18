package com.bitpunchlab.android.barter.models

import androidx.room.Entity

@Entity(tableName = "products_bidding")
data class ProductBidding(
    val id : String,
    val ownerId : String,
    val name : String,
    val category: String,
    //var timeRemaining: String,
    var images : List<String>

)