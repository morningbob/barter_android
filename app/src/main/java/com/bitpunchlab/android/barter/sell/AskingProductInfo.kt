package com.bitpunchlab.android.barter.sell

import android.graphics.Bitmap
import com.bitpunchlab.android.barter.models.ProductOffering
import com.bitpunchlab.android.barter.util.Category
import com.bitpunchlab.android.barter.util.SellingDuration

object AskingProductInfo {

    val productOffering : ProductOffering? = null
    val productName : String? = null
    val productCategory = Category.NOT_SET
    val sellingDuration = SellingDuration.NOT_SET

    val askingProducts = mutableListOf<ProductOffering>()
    val askingProductsImages = mutableListOf<List<Bitmap>>()

}