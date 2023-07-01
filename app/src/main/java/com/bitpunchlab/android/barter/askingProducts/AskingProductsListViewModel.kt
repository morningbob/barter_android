package com.bitpunchlab.android.barter.askingProducts

import androidx.lifecycle.ViewModel
import com.bitpunchlab.android.barter.models.ProductAsking
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AskingProductsListViewModel : ViewModel() {

    val _shouldDismiss = MutableStateFlow<Boolean>(false)
    val shouldDismiss : StateFlow<Boolean> get() = _shouldDismiss.asStateFlow()

    fun updateShouldDismiss(should: Boolean) {
        _shouldDismiss.value = should
    }
}