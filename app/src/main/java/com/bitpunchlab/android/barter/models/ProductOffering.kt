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
    var userName : String,
    var name: String,
    var category: String,
    var duration: Int,
    // the date will be in UTC format
    var dateCreated: String,
    var images : List<String> = listOf(),
    var status : Int,
    var acceptBidId : String = ""
    //var currentBids : List<String> = listOf(),
    //var productOfferingId : String = "",
    //@Embedded
    //var askingProducts : AskingProductsHolder = AskingProductsHolder(listOf())
) : Parcelable

@Parcelize
data class AskingProductsHolder(
    val askingList : List<ProductAsking>
) : Parcelable