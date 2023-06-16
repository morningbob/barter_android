package com.bitpunchlab.android.barter.productOfferingDetails

import androidx.lifecycle.ViewModel
import com.bitpunchlab.android.barter.models.Bid
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ProductOfferingBidsListViewModel : ViewModel() {

    val _bids = MutableStateFlow<List<Bid>>(listOf())
    val bids : StateFlow<List<Bid>> get() = _bids.asStateFlow()


}