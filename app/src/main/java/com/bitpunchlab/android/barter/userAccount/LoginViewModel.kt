package com.bitpunchlab.android.barter.userAccount

import android.util.Log
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

    fun login() {
        FirebaseClient.login(userEmail.value, userPassword.value)
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