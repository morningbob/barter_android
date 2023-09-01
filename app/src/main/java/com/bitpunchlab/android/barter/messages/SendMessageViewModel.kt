package com.bitpunchlab.android.barter.messages

import androidx.lifecycle.ViewModel
import com.bitpunchlab.android.barter.firebase.FirebaseClient
import com.bitpunchlab.android.barter.models.Message
import com.bitpunchlab.android.barter.util.SendMessageStatus
import com.bitpunchlab.android.barter.util.getCurrentDateTime
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class SendMessageViewModel : ViewModel() {

    private val _shouldDismiss = MutableStateFlow(false)
    val shouldDismiss : StateFlow<Boolean> get() = _shouldDismiss.asStateFlow()

    private val _sendMessageStatus = MutableStateFlow<SendMessageStatus>(SendMessageStatus.NORMAL)
    val sendMessageStatus : StateFlow<SendMessageStatus> get() = _sendMessageStatus.asStateFlow()

    private val _messageText = MutableStateFlow("")
    val messageText : StateFlow<String> get() = _messageText.asStateFlow()

    private val _loadingAlpha = MutableStateFlow<Float>(0f)
    val loadingAlpha : StateFlow<Float> get() = _loadingAlpha.asStateFlow()
    fun updateShouldDismiss(should: Boolean) {
        _shouldDismiss.value = should
    }

    fun updateSendMessageStatus(status: SendMessageStatus) {
        _sendMessageStatus.value = status
    }

    fun updateMessageText(text: String) {
        _messageText.value = text
    }

    private fun clearFields() {
        _messageText.value = ""
    }

    fun sendMessage(text: String, otherUserId: String, otherUserName: String) {
        _loadingAlpha.value = 100f
        if (text != "") {

            val message = Message(
                id = UUID.randomUUID().toString(),
                messageText = text,
                ownerUserId = FirebaseClient.currentUserFirebase.value!!.id,
                otherUserId = otherUserId,
                sender = true,
                ownerName = FirebaseClient.currentUserFirebase.value!!.name,
                otherName = otherUserName,
                date = getCurrentDateTime()
            )
            CoroutineScope(Dispatchers.IO).launch {
                _sendMessageStatus.value = if (CoroutineScope(Dispatchers.IO).async {
                        FirebaseClient.processSendMessage(message)
                    }.await())
                    SendMessageStatus.SUCCESS else
                        SendMessageStatus.FAILURE
                clearFields()
                _loadingAlpha.value = 0f
            }
        } else {
            _sendMessageStatus.value = SendMessageStatus.INVALID_INPUT
            clearFields()
            _loadingAlpha.value = 0f
        }

    }

}