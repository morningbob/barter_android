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

    val _deleteProductStatus = MutableStateFlow<Int>(0)
    val deleteProductStatus : StateFlow<Int> get() = _deleteProductStatus.asStateFlow()

    //private val _askingProducts = MutableStateFlow<List<ProductAsking>>(listOf())
    //val askingProducts : StateFlow<List<ProductAsking>> get() = _askingProducts.asStateFlow()

    fun updateShouldDismiss(should: Boolean) {
        _shouldDismiss.value = should
    }

    fun updateDeleteProductStatus(status: Int) {
        _deleteProductStatus.value = status
    }


}