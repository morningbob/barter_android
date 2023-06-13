package com.bitpunchlab.android.barter.productBiddingList

import com.bitpunchlab.android.barter.models.ProductBidding
import com.bitpunchlab.android.barter.util.ProductImage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object ProductBiddingInfo {

    //val _productsBidding = MutableStateFlow<List<ProductBidding>>(listOf())
    //val productBidding : StateFlow<List<ProductBidding>> get() = _productsBidding.asStateFlow()

    // once the product has value, we prepare it's image to show in the bid page
    private val _product = MutableStateFlow<ProductBidding?>(null)
    val product : StateFlow<ProductBidding?> get() = _product.asStateFlow()





    fun updateProduct(productBidding: ProductBidding?) {
        _product.value = productBidding
        retrieveImages()
    }

    private fun retrieveImages() {

    }
}