package com.bitpunchlab.android.barter.models


import android.os.Parcelable
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "products_offering")
@Parcelize
data class ProductOffering(
    @PrimaryKey
    var productId: String,
    var userId: String,
    var name: String,
    var category: String,
    var images : List<String> = listOf(),
    var currentBids : List<String> = listOf(),
    var productOfferingId : String = ""
    //@Embedded var askingProducts : List<ProductOffering> = listOf()
) : Parcelable