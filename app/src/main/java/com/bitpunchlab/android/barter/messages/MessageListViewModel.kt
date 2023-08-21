package com.bitpunchlab.android.barter.messages

import androidx.lifecycle.ViewModel
import com.bitpunchlab.android.barter.models.Message
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MessageListViewModel : ViewModel() {


    private val _shouldDismiss = MutableStateFlow<Boolean>(false)
    val shouldDismiss : StateFlow<Boolean> get() = _shouldDismiss.asStateFlow()

    private val _shouldShowDetails = MutableStateFlow<Boolean>(false)
    val shouldShowDetails : StateFlow<Boolean> get() = _shouldShowDetails.asStateFlow()

    private val _chosenMessage = MutableStateFlow<Message?>(null)
    val chosenMessage : StateFlow<Message?> get() = _chosenMessage.asStateFlow()

    fun updateShouldDismiss(should: Boolean) {
        _shouldDismiss.value = should
    }

    fun updateShouldShowDetails(should: Boolean) {
        _shouldShowDetails.value = should
    }

    fun updateChosenMessage(message: Message) {
        _chosenMessage.value = message
    }
}