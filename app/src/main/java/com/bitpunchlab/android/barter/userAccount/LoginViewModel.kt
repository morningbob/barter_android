package com.bitpunchlab.android.barter.userAccount

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bitpunchlab.android.barter.firebase.FirebaseClient
import com.bitpunchlab.android.barter.util.validateEmail
import com.bitpunchlab.android.barter.util.validatePassword
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class LoginViewModel() : ViewModel() {

    private val _userEmail = MutableStateFlow<String>("")
    val userEmail : StateFlow<String> get() = _userEmail.asStateFlow()

    private val _userPassword = MutableStateFlow<String>("")
    val userPassword : StateFlow<String> get() = _userPassword.asStateFlow()

    private val _readyLogin = MutableStateFlow<Boolean>(false)
    val readyLogin : StateFlow<Boolean> get() = _readyLogin.asStateFlow()

    private val _emailError = MutableStateFlow("")
    val emailError : StateFlow<String> get() = _emailError.asStateFlow()

    private val _passError = MutableStateFlow("")
    val passError : StateFlow<String> get() = _passError.asStateFlow()

    // 0 - no login, 1 - failed, 2 - success
    private val _loginStatus = MutableStateFlow<Int>(0)
    val loginStatus : StateFlow<Int> get() = _loginStatus

    private val _loadingAlpha = MutableStateFlow<Float>(0f)
    val loadingAlpha : StateFlow<Float> get() = _loadingAlpha

    init {
        CoroutineScope(Dispatchers.IO).launch {
            combine(emailError, passError) { email, pass ->
                _readyLogin.value = email == "" && pass == ""
            }.collect() {
                Log.i("test errors", "ready login ${readyLogin.value}")
            }
        }
    }

    fun updateEmail(newEmail: String) {
        _userEmail.value = newEmail
        _emailError.value = validateEmail(newEmail)
    }

    fun updatePassword(newPass: String) {
        _userPassword.value = newPass
        _passError.value = validatePassword(newPass)
    }

    fun updateLoginStatus(status: Int) {
        _loginStatus.value = status
    }

    fun clearFields() {
        _userEmail.value = ""
        _userPassword.value = ""
    }

    fun login() {
        _loadingAlpha.value = 100f
        CoroutineScope(Dispatchers.IO).launch {
            if (FirebaseClient.login(userEmail.value, userPassword.value)) {
                _loginStatus.value = 2
                _loadingAlpha.value = 0f // put this here, not after if clause, since login function has delay
            } else {
                _loginStatus.value = 1
                _loadingAlpha.value = 0f //
            }
            clearFields()
        }
    }
}

class LoginViewModelFactory(private val firebaseClient: FirebaseClient)
    : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}