package com.bitpunchlab.android.barter.messages

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MessageDetailsViewModel : ViewModel() {

    private val _shouldSendMessage = MutableStateFlow(false)
    val shouldSendMessage : StateFlow<Boolean> get() = _shouldSendMessage.asStateFlow()

    private val _shouldDismiss = MutableStateFlow(false)
    val shouldDismiss : StateFlow<Boolean> get() = _shouldDismiss.asStateFlow()

    fun updateShouldSendMessage(should: Boolean) {
        _shouldSendMessage.value = should
    }

    fun updateShouldDismiss(should: Boolean) {
        _shouldDismiss.value = should
    }
}