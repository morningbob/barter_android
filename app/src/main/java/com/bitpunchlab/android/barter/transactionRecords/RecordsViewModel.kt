package com.bitpunchlab.android.barter.transactionRecords

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import com.bitpunchlab.android.barter.database.BarterRepository
import com.bitpunchlab.android.barter.models.AcceptBid
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class RecordsViewModel : ViewModel() {

    val _acceptedRecords = MutableStateFlow<List<AcceptBid>>(listOf())
    val acceptedRecords : StateFlow<List<AcceptBid>> get() = _acceptedRecords.asStateFlow()

    val _productOfferedImage = MutableStateFlow<Bitmap?>(null)
    val productOfferedImage : StateFlow<Bitmap?> get() = _productOfferedImage.asStateFlow()

    val _productExchangedImage = MutableStateFlow<Bitmap?>(null)
    val productExchangedImage : StateFlow<Bitmap?> get() = _productExchangedImage.asStateFlow()

    init {
        CoroutineScope(Dispatchers.IO).launch {
            BarterRepository.retrieveAcceptedBids()?.collect() { records ->
                _acceptedRecords.value = records
            }
        }
    }

    fun prepareProductImages() {

    }

    //private fun retrieve
}