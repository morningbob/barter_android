package com.bitpunchlab.android.barter.models

import android.graphics.Bitmap

data class ProductImage(
    val id : String,
    val image: Bitmap,
    val url : String
)