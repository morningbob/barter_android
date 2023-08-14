package com.bitpunchlab.android.barter.currentBids

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class CurrentBidDetailsViewModel : ViewModel() {

    private val _shouldShowActiveBids = MutableStateFlow(false)
    val shouldShowActiveBids : StateFlow<Boolean> get() = _shouldShowActiveBids.asStateFlow()

    private val _shouldDismiss = MutableStateFlow(false)
    val shouldDismiss : StateFlow<Boolean> get() = _shouldDismiss.asStateFlow()

    fun updateShouldShowActiveBids(should: Boolean) {
        _shouldShowActiveBids.value = should
    }

    fun updateShouldDismiss(should: Boolean) {
        _shouldDismiss.value = should
    }
}