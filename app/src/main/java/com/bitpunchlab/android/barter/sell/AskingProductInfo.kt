package com.bitpunchlab.android.barter.sell

import android.graphics.Bitmap
import com.bitpunchlab.android.barter.models.ProductAsking
import com.bitpunchlab.android.barter.models.ProductOffering
import com.bitpunchlab.android.barter.util.Category
import com.bitpunchlab.android.barter.util.ProductImage
import com.bitpunchlab.android.barter.util.SellingDuration

object AskingProductInfo {

    var askingProducts = mutableListOf<ProductAsking>()
    var askingProductsImages = mutableListOf<List<ProductImage>>()

}