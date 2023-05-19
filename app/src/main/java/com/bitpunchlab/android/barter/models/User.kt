package com.bitpunchlab.android.barter.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey
    val id: String,
    val name : String,
    var email : String,
    var dataCreated : String,
    //var productsOffering : List<ProductOffering>,
    //var productsBidding : List<ProductBidding>
)