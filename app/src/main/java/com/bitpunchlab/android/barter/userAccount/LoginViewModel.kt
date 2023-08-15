package com.bitpunchlab.android.barter.userAccount

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bitpunchlab.android.barter.firebase.FirebaseClient
import com.bitpunchlab.android.barter.util.LoginStatus
import com.bitpunchlab.android.barter.util.MainStatus
import com.bitpunchlab.android.barter.util.validateEmail
import com.bitpunchlab.android.barter.util.validatePassword
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
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

    private val _emailInput = MutableStateFlow("")
    val emailInput : StateFlow<String> get() = _emailInput.asStateFlow()

    private val _emailInputError = MutableStateFlow("")
    val emailInputError : StateFlow<String> get() = _emailInputError.asStateFlow()

    private val _loginStatus = MutableStateFlow<LoginStatus>(LoginStatus.LOGGED_OUT)
    val loginStatus : StateFlow<LoginStatus> get() = _loginStatus

    private val _loadingAlpha = MutableStateFlow<Float>(0f)
    val loadingAlpha : StateFlow<Float> get() = _loadingAlpha


    init {
        CoroutineScope(Dispatchers.IO).launch {
            combine(userEmail, userPassword, emailError, passError) { email, pass, emailEr, passEr ->
                _readyLogin.value = emailEr == "" && passEr == "" && email != "" && pass != ""
            }.collect() {
                //Log.i("test errors", "ready login ${readyLogin.value}")
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

    fun updateLoginStatus(status: LoginStatus) {
        _loginStatus.value = status
    }

    fun updateEmailInput(email: String) {
        _emailInput.value = email
        _emailInputError.value = validateEmail(email)
    }

    private fun clearLoginFields() {
        _userEmail.value = ""
        _userPassword.value = ""
    }

    private fun clearResetPass() {
        _emailInput.value = ""
    }

    fun login() {
        _loadingAlpha.value = 100f
        CoroutineScope(Dispatchers.IO).launch {
            _loginStatus.value = CoroutineScope(Dispatchers.IO).async {
                FirebaseClient.login(userEmail.value, userPassword.value)
            }.await()
            _loadingAlpha.value = 0f
            clearLoginFields()
        }
    }

    fun resetPassword() {
        _loadingAlpha.value = 100f
        CoroutineScope(Dispatchers.IO).launch {
            _loginStatus.value = CoroutineScope(Dispatchers.IO).async {
                FirebaseClient.sendResetPasswordLink(emailInput.value)
            }.await()
            clearResetPass()
            _loadingAlpha.value = 0f
        }
    }
}


