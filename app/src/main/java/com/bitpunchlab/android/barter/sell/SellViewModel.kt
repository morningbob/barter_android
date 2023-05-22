package com.bitpunchlab.android.barter.sell

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SellViewModel : ViewModel() {

    private val _productName = MutableStateFlow("")
    val productName : StateFlow<String> get() = _productName.asStateFlow()

    //private val _productName = MutableStateFlow("")
    //val productName : StateFlow<String> get() = _productName.asStateFlow()


}