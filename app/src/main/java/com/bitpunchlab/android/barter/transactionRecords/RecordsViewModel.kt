package com.bitpunchlab.android.barter.transactionRecords

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.ViewModel
import com.bitpunchlab.android.barter.database.BarterRepository
import com.bitpunchlab.android.barter.models.AcceptBid
import com.bitpunchlab.android.barter.util.loadImage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class RecordsViewModel : ViewModel() {

    val _acceptedRecords = MutableStateFlow<List<AcceptBid>>(listOf())
    val acceptedRecords : StateFlow<List<AcceptBid>> get() = _acceptedRecords.asStateFlow()

    val _shouldShowRecord = MutableStateFlow<Boolean>(false)
    val shouldShowRecord : StateFlow<Boolean> get() = _shouldShowRecord.asStateFlow()

    val _productOfferedImage = MutableStateFlow<MutableList<Bitmap?>>(mutableListOf())
    val productOfferedImage : StateFlow<MutableList<Bitmap?>> get() = _productOfferedImage.asStateFlow()

    val _productExchangedImage = MutableStateFlow<MutableList<Bitmap?>>(mutableListOf())
    val productExchangedImage : StateFlow<MutableList<Bitmap?>> get() = _productExchangedImage.asStateFlow()

    init {
        CoroutineScope(Dispatchers.IO).launch {
            //BarterRepository.retrieveAcceptedBids()?.collect() { records ->
            //    Log.i("records vm", "retrieved ${records.size} records")
            //    _acceptedRecords.value = records

            //}
        }
    }

    fun updateShouldShowRecord(should: Boolean) {
        _shouldShowRecord.value = should
    }

    fun prepareImages() {
        CoroutineScope(Dispatchers.IO).launch {
            acceptedRecords.value.map { bid ->
                //_productOfferedImage.value.add(prepareProductImage(bid.acceptProductInConcern.images, ))
            }
        }
    }

    suspend fun prepareProductImage(imageUrls: List<String>, context: Context) : Bitmap? {
        //imageUrls.map { bid ->
        return CoroutineScope(Dispatchers.IO).async {
            if (imageUrls.isNotEmpty()) {
                loadImage(imageUrls[0], context)
                //_productOfferedImage.value.add(offeredImage)
            } else {
                //_productOfferedImage.value.add(null)
                null
            }
        }.await()
        //}
    }

}