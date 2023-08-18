package com.bitpunchlab.android.barter.messages

import androidx.lifecycle.ViewModel
import com.bitpunchlab.android.barter.database.BarterRepository
import com.bitpunchlab.android.barter.models.ProductOffering
import com.bitpunchlab.android.barter.util.SendMessageStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SendMessageViewModel : ViewModel() {

    private val _shouldDismiss = MutableStateFlow(false)
    val shouldDismiss : StateFlow<Boolean> get() = _shouldDismiss.asStateFlow()

    private val _sendMessageStatus = MutableStateFlow<SendMessageStatus>(SendMessageStatus.NORMAL)
    val sendMessageStatus : StateFlow<SendMessageStatus> get() = _sendMessageStatus.asStateFlow()

    //private val _product = MutableStateFlow<ProductOffering?>(null)
    //val product : StateFlow<ProductOffering?> get() = _product.asStateFlow()

    private val _messageText = MutableStateFlow("")
    val messageText : StateFlow<String> get() = _messageText.asStateFlow()

    fun updateShouldDismiss(should: Boolean) {
        _shouldDismiss.value = should
    }

    fun updateSendMessageStatus(status: SendMessageStatus) {
        _sendMessageStatus.value = status
    }

    fun updateMessageText(text: String) {
        _messageText.value = text
    }

    fun sendMessage(text: String) {

    }


    //suspend fun getProduct(id: String)  {
    //    _product.value = BarterRepository.getProductOfferingById(id)
    //}
}