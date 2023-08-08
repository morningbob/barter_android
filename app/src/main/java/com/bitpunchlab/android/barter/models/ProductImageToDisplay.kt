package com.bitpunchlab.android.barter.models

import android.graphics.Bitmap
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "images_table")
data class ProductImageToDisplay( //@JvmOverloads constructor(

    //@Ignore var image: Bitmap? = null,
    @PrimaryKey
    var imageUrlCloud : String,
    var imageUrlLocal : String? = null,
    //@PrimaryKey val imageId : String,
) : Parcelable