package com.bitpunchlab.android.barter.firebase

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bitpunchlab.android.barter.firebase.models.UserFirebase
import com.bitpunchlab.android.barter.userAccount.LoginViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.Calendar
import java.util.Locale
import java.util.UUID

//class FirebaseClient(val application: Application) : AndroidViewModel(application) {
object FirebaseClient {
    //val loginViewModel = LoginViewModel()
    //val email = MutableStateFlow<String>("")
    //val password = MutableStateFlow<String>("")
    private var auth = FirebaseAuth.getInstance()
    var isLoggedIn = MutableStateFlow<Boolean>(false)
    //private val firestore = Firebase.firestore

    var authStateListener = FirebaseAuth.AuthStateListener { auth ->
        if (auth.currentUser != null) {
            Log.i("fire auth", "auth != null")
            isLoggedIn.value = true
        } else {
            Log.i("fire auth", "auth is null")
            isLoggedIn.value = false
        }
    }

    init {
        auth.addAuthStateListener(authStateListener)
    }

    fun login(email: String, password: String) {
        auth
            .signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.i("fire auth", "login successful")
                } else {
                    Log.i("fire auth", "failed to login ${task.exception}")
                }
            }
    }

    fun logout() {
        auth.signOut()
    }

    suspend fun signupAuth(email: String, password: String) =
        suspendCancellableCoroutine<Boolean> {cancellableContinuation ->
            auth
                .createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.i("fire auth", "created new account")
                        cancellableContinuation.resume(true) {}
                    } else {
                        Log.i("fire auth", "failed to create new account ${task.exception}")
                        cancellableContinuation.resume(false) {}
                }
        }
    }

    suspend fun processSignup(name: String, email: String, password: String, firestore: FirebaseFirestore) : Boolean {
        return CoroutineScope(Dispatchers.IO).async {
            if (signupAuth(email, password)) {
                val user = createUserFirebase(name, email)
                saveUserFirebase(firestore, user)
                //createAndSaveUser(name, email, firestore)
            } else {
                false
            }

        }.await()
    }

    // after we successfully created an user in FirebaseAuth,
    // we proceed to create the user object in both Firestore and local database
    private fun createUserFirebase(name: String, email: String) : UserFirebase {
        val user = UserFirebase(userId = UUID.randomUUID().toString(), userName = name,
        userEmail = email, userDateCreated = Calendar.getInstance().time.toString() )
        Log.i("created user", "time is ${Calendar.getInstance().time.toString()}")
        return user
    }

    private suspend fun saveUserFirebase(firestore: FirebaseFirestore, user: UserFirebase) : Boolean =
        suspendCancellableCoroutine<Boolean> { cancellableContinuation ->
            firestore
                .collection("users")
                .document(user.id)
                .set(user)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.i("save user in firestore", "success")
                        cancellableContinuation.resume(true) {}
                    } else {
                        Log.i("save user in firestore", "failure ${task.exception}")
                        cancellableContinuation.resume(false) {}
                    }
                }
        }
}
/*
class FirebaseClientViewModelFactory(private val application: Application)
    : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FirebaseClient::class.java)) {
            return FirebaseClient(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

 */