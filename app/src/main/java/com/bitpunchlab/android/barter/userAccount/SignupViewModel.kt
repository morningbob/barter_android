package com.bitpunchlab.android.barter.userAccount

import android.util.Log
import androidx.lifecycle.ViewModel
import com.bitpunchlab.android.barter.firebase.FirebaseClient
import com.bitpunchlab.android.barter.util.validateConfirmPassword
import com.bitpunchlab.android.barter.util.validateEmail
import com.bitpunchlab.android.barter.util.validatePassword
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class SignupViewModel() : ViewModel() {

    private val _name = MutableStateFlow<String>("")
    val name : StateFlow<String> get() = _name.asStateFlow()

    private val _email = MutableStateFlow<String>("")
    val email : StateFlow<String> get() = _email.asStateFlow()

    private val _password = MutableStateFlow<String>("")
    val password : StateFlow<String> get() = _password.asStateFlow()

    private val _confirmPassword = MutableStateFlow<String>("")
    val confirmPassword : StateFlow<String> get() = _confirmPassword.asStateFlow()

    private val _readySignup = MutableStateFlow<Boolean>(false)
    val readySignup : StateFlow<Boolean> get() = _readySignup.asStateFlow()

    private val _nameError = MutableStateFlow("")
    val nameError : StateFlow<String> get() = _nameError.asStateFlow()

    private val _emailError = MutableStateFlow("")
    val emailError : StateFlow<String> get() = _emailError.asStateFlow()

    private val _passError = MutableStateFlow("")
    val passError : StateFlow<String> get() = _passError.asStateFlow()

    private val _confirmPassError = MutableStateFlow("")
    val confirmPassError : StateFlow<String> get() = _confirmPassError.asStateFlow()

    init {
        CoroutineScope(Dispatchers.IO).launch {
            combine(
                nameError,
                emailError,
                passError,
                confirmPassError
            ) { name, email, pass, confirmPass ->
                _readySignup.value = name == "" && email == "" && pass == "" && confirmPass == ""
            }.collect() {
                Log.i("test error", "ready sign up ${readySignup.value}")
            }
        }
    }

    fun updateName(newName: String) {
        _name.value = newName
        _nameError.value = if (newName == "") "Name must not be empty." else ""
    }
    fun updateEmail(newEmail: String) {
        _email.value = newEmail
        _emailError.value = validateEmail(newEmail)
    }

    fun updatePassword(newPassword: String) {
        _password.value = newPassword
        _passError.value = validatePassword(newPassword)
    }

    fun updateConfirmPassword(newPass: String) {
        _confirmPassword.value = newPass
        _confirmPassError.value = validateConfirmPassword(password.value, newPass)
    }

    fun signup(email: String, password: String) {
        CoroutineScope(Dispatchers.IO).launch {
            FirebaseClient.signup(email, password)
        }
    }

}