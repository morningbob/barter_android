package com.bitpunchlab.android.barter.productBiddingList

import com.bitpunchlab.android.barter.models.ProductBidding
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object ProductBiddingInfo {

    val _productsBidding = MutableStateFlow<List<ProductBidding>>(listOf())
    val productBidding : StateFlow<List<ProductBidding>> get() = _productsBidding.asStateFlow()


}