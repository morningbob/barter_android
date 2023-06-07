package com.bitpunchlab.android.barter.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "products_asking")
@Parcelize
data class ProductAsking(
    @PrimaryKey
    var productId: String,
    var userId: String,
    var name: String,
    var category: String,
    //var duration: Int,
    var images : List<String> = listOf(),
    //var currentBids : List<String> = listOf(),
    var productOfferingId : String = "",
) : Parcelable