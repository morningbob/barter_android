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
    @Embedded var askingProducts : List<ProductOffering> = listOf()
) : Parcelable {
    constructor(productId: String = "", userId: String = "", name: String = "",
                category: String = "", images: List<String> = listOf<String>(),
                currentBids: List<String> = listOf(),
                //askingProducts: List<ProductOffering> = listOf()
    ) : this(productId, userId, name, category, images,
    currentBids)//, askingProducts)
}