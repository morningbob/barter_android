package com.bitpunchlab.android.barter.models

import android.graphics.Bitmap
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "images_table")
data class ProductImageToDisplay @JvmOverloads constructor(
    @PrimaryKey val imageId : String,
    @Ignore var image: Bitmap? = null,
    var imageUrlCloud : String,
    var imageUrlLocal : String? = null,

) : Parcelable