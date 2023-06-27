package com.bitpunchlab.android.barter.transactionRecords

import com.bitpunchlab.android.barter.models.AcceptBid
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object RecordInfo {

    val _recordChosen = MutableStateFlow<AcceptBid?>(null)
    val recordChosen : StateFlow<AcceptBid?> get() = _recordChosen.asStateFlow()

    fun updateRecordChosen(record: AcceptBid) {
        _recordChosen.value = record
    }
}