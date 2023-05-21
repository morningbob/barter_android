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

    private val database = BarterDatabase.getInstance(app.applicationContext)

    val _productOfferingList = MutableStateFlow<List<ProductOffering>>(listOf())
    val productOfferingList : StateFlow<List<ProductOffering>> get() = _productOfferingList.asStateFlow()
    //var productBidding = MutableStateFlow<List<ProductOffering>>(listOf())
    //val currentUser = database.barterDao.getUser(FirebaseClient.currentUserFirebase.value.id!!)

    //private val _userId = MutableStateFlow("")
    //val userId : StateFlow<String> get() = _userId.asStateFlow()

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser : StateFlow<User?> get() = _currentUser.asStateFlow()
    init {

        //_productOfferingList.value = retrieveProductsOffering()
        CoroutineScope(Dispatchers.IO).launch {
            retrieveProductsOffering()
        }
        CoroutineScope(Dispatchers.IO).launch {
            FirebaseClient.userId.collect() {
            //_userId.value = it
                if (it != "") {
                    Log.i("mainVM", "userid: $it")
                    BarterRepository.getCurrentUser(it, database).collect() { currentUserList ->
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
                    BarterRepository.insertCurrentUser(convertUserFirebaseToUser(it), database)
                }
            }
        }
        CoroutineScope(Dispatchers.IO).launch {
            //database.barterDao.getAllUsers().collect() {
            BarterRepository.getAllUsers(database).collect() {
                Log.i("mainVM", "all users")
                it.map { user ->
                    Log.i("mainVM", "user ${user.name} ${user.id}")
                }
            }
        }
    }
    // new relation, to get products
    suspend fun retrieveProductsOffering()   {
        BarterRepository.getAllProductOffering(database).collect() {
            _productOfferingList.value = it
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