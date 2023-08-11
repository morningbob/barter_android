package com.bitpunchlab.android.barter.currentBids

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import com.bitpunchlab.android.barter.models.Bid
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class CurrentBidsViewModel : ViewModel() {

    private val _currentBids = MutableStateFlow<SnapshotStateList<Bid>>(mutableStateListOf())
    val currentBids : StateFlow<SnapshotStateList<Bid>> get() = _currentBids.asStateFlow()

    private val _shouldDisplayDetails = MutableStateFlow(false)
    val shouldDisplayDetails : StateFlow<Boolean> get() = _shouldDisplayDetails.asStateFlow()

    fun updateCurrentBids(bids: List<Bid>) {
        _currentBids.value = bids.toMutableStateList()
    }

    fun updateShouldDisplayDetails(should: Boolean) {
        _shouldDisplayDetails.value = should
    }
}