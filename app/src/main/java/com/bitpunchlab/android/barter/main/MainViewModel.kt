package com.bitpunchlab.android.barter.main

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bitpunchlab.android.barter.database.BarterRepository

import com.bitpunchlab.android.barter.firebase.FirebaseClient
import com.bitpunchlab.android.barter.models.User
import com.bitpunchlab.android.barter.util.DeleteAccountStatus
import com.bitpunchlab.android.barter.util.MainStatus
import com.bitpunchlab.android.barter.util.convertUserFirebaseToUser
import com.bitpunchlab.android.barter.util.validatePassword
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.*
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

    private val _mainStatus = MutableStateFlow<MainStatus>(MainStatus.NORMAL)
    val mainStatus : StateFlow<MainStatus> get() = _mainStatus.asStateFlow()

    private val _deleteACStatus = MutableStateFlow<DeleteAccountStatus>(DeleteAccountStatus.NORMAL)
    val deleteACStatus : StateFlow<DeleteAccountStatus> get() = _deleteACStatus.asStateFlow()

    private val _shouldNavigateMessages = MutableStateFlow(false)
    val shouldNavigateMessages : StateFlow<Boolean> get() = _shouldNavigateMessages.asStateFlow()

    private val _loadingAlpha = MutableStateFlow(0f)
    val loadingAlpha : StateFlow<Float> get() = _loadingAlpha.asStateFlow()


    init {
        /*
        CoroutineScope(Dispatchers.IO).launch {
            FirebaseClient.userId.collect() {
                if (it != "") {
                    //Log.i("mainVM", "userid: $it")
                    BarterRepository.getCurrentUser(it)?.collect() { currentUserList ->
                        if (currentUserList.isNotEmpty()) {
                            Log.i("local database mgr", "got current user ${currentUserList[0].name}")
                            _currentUser.value = currentUserList[0]
                        } else {
                            Log.i("local database mgr", "got empty list of user")
                        }
                    }
                } else {
                    Log.i("local database mgr", "userId is null")
                }

            }
        }

         */
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
            combine(currentPassword, newPassword, confirmPassword, currentPassError, newPassError, confirmPassError ) { flows: Array<String> ->
                if (flows[0] != "" && flows[1] != "" && flows[2] != "" && flows[3] == "" && flows[4] == "" && flows[5] == "") {
                    _mainStatus.value = MainStatus.READY_CHANGE_PASSWORD
                }
            }.collect() {
                //Log.i("f", "s")
            }
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

    fun updateMainStatus(status: MainStatus) {
        _mainStatus.value = status
    }

    fun updateDeleteAccountStatus(status: DeleteAccountStatus) {
        _deleteACStatus.value = status
    }

    fun updateShouldNavigateMessages(should: Boolean) {
        _shouldNavigateMessages.value = should
    }

    fun changePassword() {
        _loadingAlpha.value = 100f
        CoroutineScope(Dispatchers.IO).launch {
            _mainStatus.value = CoroutineScope(Dispatchers.IO).async {
                FirebaseClient.changePasswordFirebaseAuth(currentPassword.value, newPassword.value)
            }.await()
            _loadingAlpha.value = 0f
        }
    }

    fun deleteAccount() {
        _loadingAlpha.value = 100f
        CoroutineScope(Dispatchers.IO).launch {
            val result = CoroutineScope(Dispatchers.IO).async {
                FirebaseClient.processDeleteAccount()
            }.await()
            if (result) {
                _deleteACStatus.value = DeleteAccountStatus.SUCCESS
            } else {
                _deleteACStatus.value = DeleteAccountStatus.FAILURE
            }
            _loadingAlpha.value = 0f
        }
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
