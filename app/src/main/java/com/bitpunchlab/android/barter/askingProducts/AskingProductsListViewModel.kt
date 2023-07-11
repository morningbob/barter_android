package com.bitpunchlab.android.barter.askingProducts

import androidx.lifecycle.ViewModel
import com.bitpunchlab.android.barter.models.ProductAsking
import com.bitpunchlab.android.barter.productsOfferingList.ProductInfo
import com.bitpunchlab.android.barter.sell.AskingProductInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AskingProductsListViewModel : ViewModel() {

    val _shouldDismiss = MutableStateFlow<Boolean>(false)
    val shouldDismiss : StateFlow<Boolean> get() = _shouldDismiss.asStateFlow()

    private val _askingProducts = MutableStateFlow<List<ProductAsking>>(listOf())
    val askingProducts : StateFlow<List<ProductAsking>> get() = _askingProducts.asStateFlow()

    fun updateShouldDismiss(should: Boolean) {
        _shouldDismiss.value = should
    }

    fun updateAskingProducts(asking: List<ProductAsking>) {
        _askingProducts.value = asking
    }
}