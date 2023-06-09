package com.bitpunchlab.android.barter.productsOfferingList

import androidx.compose.runtime.collectAsState
import com.bitpunchlab.android.barter.ProductsOffering
import com.bitpunchlab.android.barter.models.ProductAsking
import com.bitpunchlab.android.barter.models.ProductOffering
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object ProductInfo {

    val _productChosen = MutableStateFlow<ProductOffering?>(null)
    val productChosen : StateFlow<ProductOffering?> get() = _productChosen.asStateFlow()

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
}