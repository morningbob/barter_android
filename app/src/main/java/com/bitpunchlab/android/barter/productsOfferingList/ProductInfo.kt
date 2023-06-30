package com.bitpunchlab.android.barter.productsOfferingList

import androidx.compose.runtime.collectAsState
import com.bitpunchlab.android.barter.ProductsOffering
import com.bitpunchlab.android.barter.models.ProductAsking
import com.bitpunchlab.android.barter.models.ProductOffering
import com.bitpunchlab.android.barter.models.ProductOfferingAndBid
import com.bitpunchlab.android.barter.models.ProductOfferingAndProductAsking
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object ProductInfo {

    val _productChosen = MutableStateFlow<ProductOffering?>(null)
    val productChosen : StateFlow<ProductOffering?> get() = _productChosen.asStateFlow()

    private val _productOfferingWithProductsAsking = MutableStateFlow<ProductOfferingAndProductAsking?>(null)
    val productOfferingWithProductsAsking : StateFlow<ProductOfferingAndProductAsking?>
        get() = _productOfferingWithProductsAsking.asStateFlow()

    private val _productOfferingWithBids = MutableStateFlow<ProductOfferingAndBid?>(null)
    val productOfferingWithBids : StateFlow<ProductOfferingAndBid?>
        get() = _productOfferingWithBids.asStateFlow()

    // asking product are Product Asking objects
    // it can be from the product the user offers, or the product the user bids
    val _askingProducts = MutableStateFlow<List<ProductAsking>>(listOf())
    val askingProducts : StateFlow<List<ProductAsking>> get() = _askingProducts.asStateFlow()

    fun updateProductChosen(product: ProductOffering?) {
        _productChosen.value = product
    }

    fun updateAskingProducts(products: List<ProductAsking>) {
        _askingProducts.value = products
    }

    fun updateProductOfferingWithProductsAsking(product: ProductOfferingAndProductAsking) {
        _productOfferingWithProductsAsking.value = product
    }

    fun updateProductOfferingWithBids(product: ProductOfferingAndBid) {
        _productOfferingWithBids.value = product
    }
}