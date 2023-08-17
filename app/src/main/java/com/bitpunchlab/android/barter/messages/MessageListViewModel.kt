package com.bitpunchlab.android.barter.messages

import androidx.lifecycle.ViewModel
import com.bitpunchlab.android.barter.models.Message
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MessageListViewModel : ViewModel() {

    private val _messagesReceived = MutableStateFlow<List<Message>>(listOf())
    val messagesReceived : StateFlow<List<Message>> get() = _messagesReceived.asStateFlow()

    private val _shouldDismiss = MutableStateFlow<Boolean>(false)
    val shouldDismiss : StateFlow<Boolean> get() = _shouldDismiss.asStateFlow()

    fun updateShouldDismiss(should: Boolean) {
        _shouldDismiss.value = should
    }
}