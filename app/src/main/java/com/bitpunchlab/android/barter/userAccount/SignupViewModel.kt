package com.bitpunchlab.android.barter.userAccount

import android.util.Log
import androidx.lifecycle.ViewModel
import com.bitpunchlab.android.barter.firebase.FirebaseClient
import com.bitpunchlab.android.barter.util.SignUpStatus
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

    private val _shouldDismiss = MutableStateFlow<Boolean>(false)
    val shouldDismiss : StateFlow<Boolean> get() = _shouldDismiss.asStateFlow()

    private val _createACStatus = MutableStateFlow<SignUpStatus>(SignUpStatus.NORMAL)
    val createACStatus : StateFlow<SignUpStatus> get() = _createACStatus.asStateFlow()

    // 1 - failed, 2 - success, 0 - no dialog
    //private val _shouldShowStatus = MutableStateFlow(0)
    //val shouldShowStatus : StateFlow<Int> get() = _shouldShowStatus.asStateFlow()

    private val _loadingAlpha = MutableStateFlow<Float>(0f)
    val loadingAlpha : StateFlow<Float> get() = _loadingAlpha

    init {
        CoroutineScope(Dispatchers.IO).launch {
            combine(
                name,
                email,
                password,
                confirmPassword,
                nameError,
                emailError,
                passError,
                confirmPassError
            ) { flows: Array<String> ->
                _readySignup.value = (flows[0] != "" && flows[1] != "" && flows[2] != "" && flows[3] != "" && flows[4] == "" && flows[5] == "" &&
                        flows[6] == "" && flows[7] == "")
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

    fun signup() {
        _loadingAlpha.value = 100f
        //CoroutineScope(Dispatchers.IO).launch {
            Log.i("signup", "firebase client processing")
            FirebaseClient.processSignupAuth(name.value, email.value, password.value)
        //}
    }

    fun updateLoadingAlpha(alpha: Float) {
        _loadingAlpha.value = alpha
    }

    fun updateShouldDismiss(should: Boolean) {
        _shouldDismiss.value = should
    }

    fun updateCreateACStatus(status: SignUpStatus) {
        _createACStatus.value = status
    }

    fun clearFields() {
        _email.value = ""
        _name.value = ""
        _password.value = ""
        _confirmPassword.value = ""
    }

}