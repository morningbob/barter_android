package com.bitpunchlab.android.barter.sell

import androidx.lifecycle.ViewModel
import com.bitpunchlab.android.barter.models.ProductAsking
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AskingProductsListViewModel : ViewModel() {

    val _askingProducts = MutableStateFlow<List<ProductAsking>>(listOf())
    val askingProducts : StateFlow<List<ProductAsking>> get() = _askingProducts.asStateFlow()

    
}