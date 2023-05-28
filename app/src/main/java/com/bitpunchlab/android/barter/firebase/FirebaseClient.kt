package com.bitpunchlab.android.barter.firebase

import android.graphics.Bitmap
import android.util.Log
import com.bitpunchlab.android.barter.firebase.models.ProductOfferingFirebase
import com.bitpunchlab.android.barter.firebase.models.UserFirebase
import com.bitpunchlab.android.barter.models.ProductOffering
import com.bitpunchlab.android.barter.util.ProductType
import com.bitpunchlab.android.barter.util.convertBitmapToBytes
import com.bitpunchlab.android.barter.util.convertProductOfferingToFirebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.*
import kotlin.collections.HashMap

//class FirebaseClient(val application: Application) : AndroidViewModel(application) {
object FirebaseClient {

    private val storageRef = Firebase.storage.reference
    // we need the id as soon as it is available from Auth
    private val _userId = MutableStateFlow<String>("")
    val userId : StateFlow<String> get() = _userId.asStateFlow()

    private val _currentUserFirebase = MutableStateFlow<UserFirebase?>(null)
    val currentUserFirebase : StateFlow<UserFirebase?> get() = _currentUserFirebase.asStateFlow()

    private var auth = FirebaseAuth.getInstance()
    var isLoggedIn = MutableStateFlow<Boolean>(false)
    var createAccount = false
    // 0 - no registration, 1 - failure, 2 - success
    private val _createACStatus = MutableStateFlow<Int>(0)
    val createACStatus : StateFlow<Int> get() = _createACStatus.asStateFlow()

    val _finishedAuthSignup = MutableStateFlow<Boolean>(false)
    val finishedAuthSignup : StateFlow<Boolean> get() = _finishedAuthSignup.asStateFlow()


    private var authStateListener = FirebaseAuth.AuthStateListener { auth ->
        if (auth.currentUser != null) {
            Log.i("fire auth", "auth != null")
            isLoggedIn.value = true
            _userId.value = auth.currentUser!!.uid
            Log.i("auth listener", "got user id ${userId.value}")
            if (createAccount) {
                _finishedAuthSignup.value = true
                /*
                CoroutineScope(Dispatchers.IO).launch {
                    if (processSignupUserObject(userName, userEmail)) {
                        _createACStatus.value = 2
                    } else {
                        _createACStatus.value = 1
                    }
                }

                 */
            } else {
                // retrieve user from firestore
                CoroutineScope(Dispatchers.IO).launch {
                    //_userId.value = auth.cu
                    Log.i("auth", "user id: ${auth.currentUser!!.uid}")
                    _currentUserFirebase.value = retrieveUserFirebase(auth.currentUser!!.uid)
                }
            }
        } else {
            Log.i("fire auth", "auth is null")
            isLoggedIn.value = false
        }
    }

    init {
        auth.addAuthStateListener(authStateListener)
    }

    suspend fun login(email: String, password: String) : Boolean =
        suspendCancellableCoroutine<Boolean> { cancellableContinuation ->
        auth
            .signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.i("fire auth", "login successful")
                    cancellableContinuation.resume(true) {}
                } else {
                    Log.i("fire auth", "failed to login ${task.exception}")
                    cancellableContinuation.resume(false) {}
                }
            }
    }

    fun logout() {
        auth.signOut()
    }

    fun updateCreateACStatus(status: Int) {
        _createACStatus.value = status
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

    suspend fun processSignupAuth(name: String, email: String, password: String) : Boolean {
        return CoroutineScope(Dispatchers.IO).async {
            createAccount = true
            if (signupAuth(email, password)) {
                // pass the info to auth, when it is ready,
                // we can create user and save to firestore
                //userEmail = email
                //userName = name
                finishedAuthSignup.collect() { finished ->
                    // reset
                    createAccount = false
                    if (finished) {
                        if (processSignupUserObject(name, email)) {
                            _createACStatus.value = 2
                        } else {
                            _createACStatus.value = 1

                        }
                    }
                }
                true
            } else {
                false
            }
        }.await()
    }

    private suspend fun processSignupUserObject(name: String, email: String) : Boolean {
        val user = createUserFirebase(auth.currentUser!!.uid, name, email)
        _currentUserFirebase.value = user
        return CoroutineScope(Dispatchers.IO).async {
            saveUserFirebase(user)
        }.await()
    }

    private suspend fun retrieveUserFirebase(uid: String) : UserFirebase? =
        suspendCancellableCoroutine<UserFirebase?> { cancellableContinuation ->
            Firebase.firestore
                .collection("users")
                .document(uid)
                .get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        Log.i("retrieve user firebase", "success ${document.data?.get("name")}")
                        val user = document.toObject(UserFirebase::class.java)
                        cancellableContinuation.resume(user) {}
                    } else {
                        Log.i("retrieve user firebase", "document is empty")
                        cancellableContinuation.resume(null) {}
                    }
                }
                .addOnFailureListener { e ->
                    Log.i("retrieve user firebase", "failed $e" )
                    cancellableContinuation.resume(null) {}
                }
    }

    // after we successfully created an user in FirebaseAuth,
    // we proceed to create the user object in both Firestore and local database
    private fun createUserFirebase(id: String, name: String, email: String) : UserFirebase {
        val user = UserFirebase(userId = id, userName = name,
        userEmail = email, userDateCreated = Calendar.getInstance().time.toString() )
        Log.i("created user", "time is ${Calendar.getInstance().time.toString()}")
        return user
    }

    private suspend fun saveUserFirebase(user: UserFirebase) : Boolean =
        suspendCancellableCoroutine<Boolean> { cancellableContinuation ->
            Firebase.firestore
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
/*
    suspend fun processSelling(productOffering: ProductOffering, askImages: List<Bitmap>)
    : ProductOffering {
        // convert product to firebase model
        // save images in cloud store and local device, and get the url and store in the product
        // store product id in user's object, both local and firebase
        // save user's object in firebase
        // save product in productsOffering collection in firebase
        // trigger cloud function to

    }
*/
    suspend fun processSelling(productOffering: ProductOffering, productImages: List<Bitmap>,
        askingProducts: List<ProductOffering>, askingProductImages: List<List<Bitmap>>) : Boolean {

        val askingProductsList = mutableListOf<ProductOffering>()
        for (i in 0..askingProducts.size - 1) {
            askingProductsList.add(processEachProduct(askingProducts[i], askingProductImages[i]))
        }

        val semiUpdatedProductOffering = processEachProduct(productOffering, productImages)
        //semiUpdatedProductOffering.askingProducts = askingProductsList

        val productFirebase = convertProductOfferingToFirebase(semiUpdatedProductOffering, askingProductsList)
        val resultProductDeferred = CoroutineScope(Dispatchers.IO).async {
            saveProductOfferingFirebase(productFirebase, ProductType.PRODUCT)

        }
        val resultAskingDeferred = CoroutineScope(Dispatchers.IO).async {
            askingProductsList.map { each ->
                saveProductOfferingFirebase(
                    convertProductOfferingToFirebase(each),
                    ProductType.ASKING_PRODUCT
                )
            }
        }
        val resultAsking = resultAskingDeferred.await()
        var count = 0
        for (each in resultAsking) {
            if (each) {
                count += 1
            } else {
                break
            }
        }

        return resultProductDeferred.await() && (count == resultAsking.size)
    }

    private suspend fun processEachProduct(productOffering: ProductOffering,
        productImages: List<Bitmap>) : ProductOffering {
        val downloadUrlList = mutableListOf<String>()
        val imageFilenames = mutableListOf<String>()

        val pairProductResult = uploadImages(productOffering = productOffering,
            images = productImages)

        downloadUrlList.addAll(pairProductResult.first)
        imageFilenames.addAll(pairProductResult.second)

        val newList = productOffering.images.toMutableList()
        newList.addAll(downloadUrlList)
        Log.i(
            "process selling",
            "adding downloadUrl to product offering, items: ${downloadUrlList.size}"
        )
        productOffering.images = newList
        return productOffering
    }

    private suspend fun uploadImages(productOffering: ProductOffering, images: List<Bitmap>) :
     Pair<List<String>, List<String>> {
        val lock1 = Any()
        val downloadUrlList = mutableListOf<String>()
        val imageFilenames = mutableListOf<String>()

        for (i in 0..images.size - 1) {
            CoroutineScope(Dispatchers.IO).launch {
                val filename = "${productOffering.productId}_${i}.jpg"
                Log.i("process selling", "creating filename $filename")
                val pair = saveImageCloudStorage(images[i], filename)
                synchronized(lock1) {
                    //val newProductOffering = processSaveImage(askImages[i], productOffering, filename)
                    if (pair.second != null) {
                        Log.i("process selling", "editing downloadUrlList")
                        downloadUrlList.add(pair.second!!)
                        imageFilenames.add(pair.first)
                    }
                }
            }.join()
        }
        return Pair(downloadUrlList, imageFilenames)
    }
/*
    private suspend fun processSaveImage(bitmap: Bitmap,
                                         filename: String) : Pair<String, String?> =
        suspendCancellableCoroutine<Pair<String, String?>> { cancellableContinuation ->
            CoroutineScope(Dispatchers.IO).launch {

                val downloadUrl = saveImageCloudStorage(bitmap, filename)
                if (downloadUrl != null) {
                    Log.i("process save image", "got downloadUrl $downloadUrl")
                    cancellableContinuation.resume(Pair(filename, downloadUrl)) {}
                } else {
                    // not put that after if because need to wait for the result back
                    Log.i("process save image", "failed to get downloadUrl")
                    cancellableContinuation.resume(Pair(filename, null)) {}
                }
            }

    }
*/
    private suspend fun saveImageCloudStorage(image: Bitmap, filename: String) : Pair<String, String?> =
        suspendCancellableCoroutine { cancellableContinuation ->

            val imageRef = storageRef.child("images/${filename}")
            val imageBytes = convertBitmapToBytes(image)

            val uploadTask = imageRef
                .putBytes(imageBytes)
                .addOnSuccessListener { taskSnapshot ->
                    Log.i("save image to storage", "success")
                    //val urlString = taskSnapshot.metadata?.reference?.downloadUrl.toString()
                    taskSnapshot.storage.downloadUrl
                        .addOnSuccessListener { url ->
                            Log.i("upload image to storage", url.toString())
                            cancellableContinuation.resume(Pair(filename, url.toString())) {}
                        }
                        .addOnFailureListener { e ->
                            Log.i("upload image to storage", "failed ${e.message}")
                            cancellableContinuation.resume(Pair(filename, null)) {}
                        }
                }
                .addOnFailureListener { e ->
                    Log.i("save image to storage", "failed ${e.message}")
                    cancellableContinuation.resume(Pair(filename, null)) {}
                }
    }

    private suspend fun saveProductOfferingFirebase(productOffering: ProductOfferingFirebase, type: ProductType) : Boolean =

        suspendCancellableCoroutine {cancellableContinuation ->
            var collection = if (type == ProductType.PRODUCT) "productsOffering" else "askingProducts"

            Firebase.firestore
                .collection(collection)
                .document(productOffering.id)
                .set(productOffering)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.i("save product offering firebase", "success")
                    } else {
                        Log.i("save product offering firebase", "failed ${task.exception}")
                    }
                }
    }


}
