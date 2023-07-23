package com.bitpunchlab.android.barter.models

import android.graphics.Bitmap

data class ProductImageToDisplay(
    val id : String,
    val image: Bitmap,
    var url : String
)