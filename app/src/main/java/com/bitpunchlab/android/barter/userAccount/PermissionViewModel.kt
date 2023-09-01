package com.bitpunchlab.android.barter.userAccount

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow


class PermissionViewModel : ViewModel() {

    private val _shouldNavigateLogin = MutableStateFlow(false)
    val shouldNavigateLogin : StateFlow<Boolean> get() = _shouldNavigateLogin.asStateFlow()

    private val _shouldRequestPermission = MutableStateFlow(false)
    val shouldRequestPermission : StateFlow<Boolean> get() = _shouldRequestPermission.asStateFlow()

    private val _permissionsGranted = MutableStateFlow(false)
    val permissionsGranted : StateFlow<Boolean> get() = _permissionsGranted.asStateFlow()


    fun updateShouldRequestPermission(should: Boolean) {
        _shouldRequestPermission.value = should
    }

    fun updatePermissionGranted(granted: Boolean) {
        _permissionsGranted.value = granted
    }

}