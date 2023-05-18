package com.bitpunchlab.android.barter.main

import androidx.lifecycle.ViewModel
import com.bitpunchlab.android.barter.models.ProductOffering
import kotlinx.coroutines.flow.MutableStateFlow

class MainViewModel : ViewModel() {

    var productOfferingList = MutableStateFlow<List<ProductOffering>>(listOf())
    var productBidding = MutableStateFlow<List<ProductOffering>>(listOf())
}