package com.bitpunchlab.android.barter.util

import android.os.Build
import com.bitpunchlab.android.barter.models.Bid
import com.bitpunchlab.android.barter.models.ProductAsking
import com.bitpunchlab.android.barter.models.ProductImageToDisplay
import com.bitpunchlab.android.barter.models.ProductOffering

inline fun <T> sdk29AndUp(onSdk29: () -> T): T? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        onSdk29()
    } else return null
}

data class ProductOfferingDecomposed(
    val productsOffering : List<ProductOffering>,
    val askingProducts : List<ProductAsking>,
    val bids : List<Bid>,
    val images : List<ProductImageToDisplay>
    )