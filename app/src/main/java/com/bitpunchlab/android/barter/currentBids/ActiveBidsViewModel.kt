package com.bitpunchlab.android.barter.currentBids

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ActiveBidsViewModel : ViewModel() {

    private val _shouldDismiss = MutableStateFlow<Boolean>(false)
    val shouldDismiss : StateFlow<Boolean> get() = _shouldDismiss.asStateFlow()

    private val _shouldShowBid = MutableStateFlow<Boolean>(false)
    val shouldShowBid : StateFlow<Boolean> get() = _shouldShowBid.asStateFlow()

    fun updateShouldDismiss(should: Boolean) {
        _shouldDismiss.value = should
    }

    fun updateShouldShowBid(should: Boolean) {
        _shouldShowBid.value = should
    }
}