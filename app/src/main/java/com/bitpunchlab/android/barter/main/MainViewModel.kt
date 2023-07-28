package com.bitpunchlab.android.barter.main

import android.app.Application
import android.util.Log
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bitpunchlab.android.barter.database.BarterDatabase
import com.bitpunchlab.android.barter.database.BarterRepository

import com.bitpunchlab.android.barter.firebase.FirebaseClient
import com.bitpunchlab.android.barter.models.ProductOffering
import com.bitpunchlab.android.barter.models.User
import com.bitpunchlab.android.barter.userAccount.LoginViewModel
import com.bitpunchlab.android.barter.util.convertUserFirebaseToUser
import com.bitpunchlab.android.barter.util.validatePassword
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@OptIn(InternalCoroutinesApi::class)
class MainViewModel(val app: Application) : AndroidViewModel(app) {

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser : StateFlow<User?> get() = _currentUser.asStateFlow()

    private val _currentPassword = MutableStateFlow<String>("")
    val currentPassword : StateFlow<String> get() = _currentPassword.asStateFlow()

    private val _newPassword = MutableStateFlow<String>("")

    val newPassword : StateFlow<String> get() = _newPassword.asStateFlow()

    private val _confirmPassword = MutableStateFlow<String>("")
    val confirmPassword : StateFlow<String> get() = _confirmPassword.asStateFlow()

    private val _currentPassError = MutableStateFlow<String>("")
    val currentPassError : StateFlow<String> get() = _currentPassError.asStateFlow()

    private val _newPassError = MutableStateFlow<String>("")
    val newPassError : StateFlow<String> get() = _newPassError.asStateFlow()

    private val _confirmPassError = MutableStateFlow<String>("")
    val confirmPassError : StateFlow<String> get() = _confirmPassError.asStateFlow()



    private val _passwordOptionStatus = MutableStateFlow<Int>(0)
    val passwordOptionStatus : StateFlow<Int> get() = _passwordOptionStatus.asStateFlow()

    private val _loadingAlpha = MutableStateFlow(0f)
    val loadingAlpha : StateFlow<Float> get() = _loadingAlpha.asStateFlow()



    init {
        CoroutineScope(Dispatchers.IO).launch {
            FirebaseClient.userId.collect() {
                if (it != "") {
                    Log.i("mainVM", "userid: $it")
                    BarterRepository.getCurrentUser(it)?.collect() { currentUserList ->
                        if (currentUserList.isNotEmpty()) {
                            Log.i("barter repo", "got current user ${currentUserList[0].name}")
                            _currentUser.value = currentUserList[0]
                        } else {
                            Log.i("barter repo", "got empty list of user")
                        }
                    }
                } else {
                    Log.i("mainVM", "userId is null")
                }

            }
        }
        CoroutineScope(Dispatchers.IO).launch {
            FirebaseClient.currentUserFirebase.collect() {
                it?.let {
                    Log.i("barter repo", "saving current user")
                    BarterRepository.insertCurrentUser(convertUserFirebaseToUser(it))
                }
            }
        }
        CoroutineScope(Dispatchers.IO).launch {
            BarterRepository.getAllUsers()?.collect() {
                Log.i("mainVM", "all users")
                it.map { user ->
                    Log.i("mainVM", "user ${user.name} ${user.id}")
                }
            }
        }

        CoroutineScope(Dispatchers.IO).launch {

        }
    }

    fun updateCurrentPassword(pass: String) {
        _currentPassword.value = pass
        _currentPassError.value = validatePassword(pass)
    }

    fun updateNewPassword(pass: String) {
        _newPassword.value = pass
        _newPassError.value = validatePassword(pass)
    }

    fun updateConfirmPassword(pass: String) {
        _confirmPassword.value = pass
        _confirmPassError.value = validatePassword(pass)
    }

    fun updatePasswordOptionStatus(status: Int) {
        _passwordOptionStatus.value = status
    }

    fun changePassword() {
        _loadingAlpha.value = 100f
        CoroutineScope(Dispatchers.IO).launch {
            if (FirebaseClient.changePasswordFirebaseAuth(currentPassword.value, newPassword.value)) {
                _passwordOptionStatus.value = 2
                _loadingAlpha.value = 0f
            } else {
                _passwordOptionStatus.value = 3
                _loadingAlpha.value = 0f
            }
        }
    }

    fun logout() {
        FirebaseClient.logout()
    }
}

class MainViewModelFactory(private val application: Application)
    : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}