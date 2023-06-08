package com.bitpunchlab.android.barter.productsOfferingList

import androidx.compose.runtime.collectAsState
import com.bitpunchlab.android.barter.ProductsOffering
import com.bitpunchlab.android.barter.models.ProductOffering
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object ProductInfo {

    val _productChosen = MutableStateFlow<ProductOffering?>(null)
    val productChosen : StateFlow<ProductOffering?> get() = _productChosen.asStateFlow()

    fun updateProductChosen(product: ProductOffering) {
        _productChosen.value = product
    }

}