package com.bitpunchlab.android.barter.models

import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "images_table")
data class ProductImageToDisplay @JvmOverloads constructor(
    @PrimaryKey val imageId : String,
    @Ignore var image: Bitmap? = null,
    val imageUrlCloud : String,
    var imageUrlLocal : String? = null
)