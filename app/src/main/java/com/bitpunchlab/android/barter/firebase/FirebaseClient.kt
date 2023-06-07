package com.bitpunchlab.android.barter.firebase

import android.graphics.Bitmap
import android.util.Log
import com.bitpunchlab.android.barter.database.BarterDatabase
import com.bitpunchlab.android.barter.database.BarterRepository
import com.bitpunchlab.android.barter.firebase.models.ProductAskingFirebase
import com.bitpunchlab.android.barter.firebase.models.ProductOfferingFirebase
import com.bitpunchlab.android.barter.firebase.models.UserFirebase
import com.bitpunchlab.android.barter.models.ProductAsking
import com.bitpunchlab.android.barter.models.ProductOffering
import com.bitpunchlab.android.barter.models.User
import com.bitpunchlab.android.barter.util.ProductImage
import com.bitpunchlab.android.barter.util.ProductType
import com.bitpunchlab.android.barter.util.convertBitmapToBytes
import com.bitpunchlab.android.barter.util.convertProductAskingToFirebase
import com.bitpunchlab.android.barter.util.convertProductFirebaseToProduct
import com.bitpunchlab.android.barter.util.convertProductOfferingToFirebase
import com.bitpunchlab.android.barter.util.convertUserFirebaseToUser
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

object FirebaseClient {

    private val storageRef = Firebase.storage.reference
    // we need the id as soon as it is available from Auth
    private val _userId = MutableStateFlow<String>("")

    var localDatabase : BarterDatabase? = null
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
            } else {
                // retrieve user from firestore
                CoroutineScope(Dispatchers.IO).launch {
                    //_userId.value = auth.cu
                    Log.i("auth", "user id: ${auth.currentUser!!.uid}")
                    val currentUser = retrieveUserFirebase(auth.currentUser!!.uid)
                    currentUser?.let {
                        _currentUserFirebase.value = currentUser
                        saveUserLocalDatabase(convertUserFirebaseToUser(currentUser))
                        prepareProductsOfferingForLocalDatabase(currentUser)
                    }
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

    private fun saveUserLocalDatabase(user: User) {
        BarterRepository.insertCurrentUser(user, localDatabase!!)
    }

    private fun prepareProductsOfferingForLocalDatabase(
                                                userFirebase: UserFirebase) {
        // retrieve the products offering and products bidding info
        // create those objects and save in local database
        // this includes removing the outdated products objects in the database
        // here the productsOffering in the user firebase object
        // has the full product offering object stored in it
        val productsOffering = userFirebase.productsOffering.map { (key, value) ->
            convertProductFirebaseToProduct(value)
        }
        //val askingProducts = mutableListOf<ProductOffering>()

        //for (product in productsOffering.as)
        BarterRepository.insertProductsOffering(localDatabase!!, productsOffering)

    }

    // after we successfully created an user in FirebaseAuth,
    // we proceed to create the user object in both Firestore and local database
    private fun createUserFirebase(id: String, name: String, email: String) : UserFirebase {
        val user = UserFirebase(userId = id, userName = name,
        userEmail = email, userDateCreated = Calendar.getInstance().time.toString(),
        offering = HashMap<String, ProductOfferingFirebase>()
        )
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

    suspend fun processSelling(productOffering: ProductOffering,
                               productImages: List<ProductImage>,
        askingProducts: List<ProductAsking>, askingProductImages: List<List<ProductImage>>) : Boolean {

        val pairResult = uploadImagesAndGetDownloadUrl(productOffering, askingProducts, askingProductImages, productImages)
        val semiUpdatedProductOffering = pairResult.first
        val askingProductsList = pairResult.second
        semiUpdatedProductOffering.askingProducts = askingProductsList
        // here, we need to wait for the processEachProduct to upload the images to cloud storage
        // and get back download url, then record in the product offering and asking product
        // so the product offering will be updated with both its own images and the asking products
        // objects. They can then be saved in user object in firestore.
        //val productFirebase =
        //    convertProductOfferingToFirebase(semiUpdatedProductOffering, askingProductsList)
        val productFirebase =
            convertProductOfferingToFirebase(semiUpdatedProductOffering)

        val askingProductsFirebase = askingProductsList.map { each ->
            convertProductAskingToFirebase(each, productFirebase.id)
        }
        val resultAskingDeferred = CoroutineScope(Dispatchers.IO).async {
            saveAllAskingProductFirebase(askingProductsFirebase)
        }

        //val productOfferingFirebase = convertProductOfferingToFirebase(productOffering)
        val askingProductsFirebaseMap = HashMap<String, ProductAskingFirebase>()
        for (i in 0..askingProductsFirebase.size - 1) {
            askingProductsFirebaseMap.put(i.toString(), askingProductsFirebase[i])
        }

        productFirebase.askingProducts = askingProductsFirebaseMap

        val resultProductDeferred = CoroutineScope(Dispatchers.IO).async {
            saveProductOfferingFirebase(productFirebase, productFirebase.id, ProductType.PRODUCT)
        }

        val resultAsking = resultAskingDeferred.await()

        val resultUpdateUser = updateProductSellingInUserFirebase(
            currentUserFirebase.value!!,
            productFirebase,
        )

        //Log.i("process selling", "resultAsking and resultUpdateUser done")

        val resultProduct = resultProductDeferred.await()

        //Log.i("process selling", "resultUpdateUser $resultUpdateUser")

        //Log.i("process selling", "resultAsking ${resultAsking}")
        //Log.i("process selling", "resultProduct $resultProduct")

        return resultProduct && resultAsking && resultUpdateUser
    }

    private suspend fun uploadImagesAndGetDownloadUrl(productOffering: ProductOffering,
                                                      listOfAskingProducts: List<ProductAsking>,
                                                      askingProductImages: List<List<ProductImage>>,
                                                      productImages: List<ProductImage>) =
        suspendCancellableCoroutine<Pair<ProductOffering, List<ProductAsking>>> { cancellableContinuation ->

            CoroutineScope(Dispatchers.IO).launch {
                val askingProductsList = mutableListOf<ProductAsking>()
                CoroutineScope(Dispatchers.IO).async {
                    for (i in 0..listOfAskingProducts.size - 1) {
                        val pairResult = processEachProduct(
                            listOfAskingProducts[i],
                            listOfAskingProducts[i].productId,
                            askingProductImages[i]
                        )
                        val downloadUrl = pairResult.first
                        val imageFilenames = pairResult.second
                        for (i in 0..listOfAskingProducts.size - 1) {
                            listOfAskingProducts[i].images = downloadUrl
                        }
                    }
                }.await()
                val semiUpdatedProductOfferingDeferred = CoroutineScope(Dispatchers.IO).async {
                    processEachProduct(productOffering, productOffering.productId, productImages)

                }
                val pairResult = semiUpdatedProductOfferingDeferred.await()
                productOffering.images = pairResult.first
                cancellableContinuation.resume(Pair(productOffering, listOfAskingProducts)) {}
            }
    }

    private suspend fun saveAllAskingProductFirebase(
        askingProductsFirebase: List<ProductAskingFirebase>) =
        suspendCancellableCoroutine<Boolean> { cancellableContinuation ->
            //val results = mutableListOf<Boolean>()
            for (i in 0..askingProductsFirebase.size - 1) {
                CoroutineScope(Dispatchers.IO).launch {
                    val result = CoroutineScope(Dispatchers.IO).async {
                        saveProductOfferingFirebase(askingProductsFirebase[i],
                            askingProductsFirebase[i].id,
                            ProductType.ASKING_PRODUCT)
                    }.await()
                    if (!result) {
                        cancellableContinuation.resume(false) {}
                    }
                }
            }
            cancellableContinuation.resume(true) {}
        }

    private suspend fun <T> processEachProduct(product: T,
                                               productId: String,
        productImages: List<ProductImage>) : Pair<List<String>, List<String>> {
        val downloadUrlList = mutableListOf<String>()
        val imageFilenames = mutableListOf<String>()

        val images = productImages.map { it.image }

        //val productMembers = product!!::class.members
        //val field = product.javaClass.getDeclaredField("")

        val pairProductResult = uploadImages(product = product,
            productId = productId,
            images = images)

        downloadUrlList.addAll(pairProductResult.first)
        imageFilenames.addAll(pairProductResult.second)

        //val productMembers = product!!::class.members
        //val productImages = productMembers.first { it.name == "images" }
        //val field = product.javaClass.getDeclaredField("_images")
        //field.isAccessible = true
        //val productImages = field.get(product) as List<String>

        // create a new object, copy all field


        //val newList = mutableListOf<String>()
        //val newList = productImages.toMutableList()
        //newList.addAll(downloadUrlList)
        Log.i(
            "process selling",
            "adding downloadUrl to product offering, items: ${downloadUrlList.size}"
        )

        //productImages.set(product, newList)
        //product.images = newList
        return Pair(downloadUrlList, imageFilenames)
    }

    private suspend fun <T> uploadImages(product: T, productId: String, images: List<Bitmap>) :
     Pair<List<String>, List<String>> {
        val lock1 = Any()
        val downloadUrlList = mutableListOf<String>()
        val imageFilenames = mutableListOf<String>()

        for (i in 0..images.size - 1) {
            CoroutineScope(Dispatchers.IO).launch {
                val filename = "${productId}_${i}.jpg"
                Log.i("process selling", "creating filename $filename")
                val pair = saveImageCloudStorage(images[i], filename)
                synchronized(lock1) {

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

    private suspend fun <T : Any> saveProductOfferingFirebase(product: T, productId: String,
                                                              type: ProductType) : Boolean =

        suspendCancellableCoroutine {cancellableContinuation ->
            var collection = if (type == ProductType.PRODUCT) "productsOffering" else "askingProducts"

            Firebase.firestore
                .collection(collection)
                .document(productId)
                .set(product)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.i("save product offering firebase", "success")
                        cancellableContinuation.resume(true) {}
                    } else {
                        Log.i("save product offering firebase", "failed ${task.exception}")
                        cancellableContinuation.resume(false) {}
                    }
                }
    }

    private suspend fun updateProductSellingInUserFirebase(
        userFirebase: UserFirebase, productOfferingFirebase: ProductOfferingFirebase) : Boolean =
        suspendCancellableCoroutine<Boolean> { cancellableContinuation ->
            val newUser = userFirebase

            // I like to maintain the order of the product user asked for
            // it is important to show the most favorable product in exchange
            // the key is used to maintain the order of the asking products

            val newProductsMap = newUser.productsOffering.toMutableMap()
            newProductsMap.put(productOfferingFirebase.id, productOfferingFirebase)
            //val updatedUser = newUser.productsOffering
            newUser.productsOffering = newProductsMap as HashMap<String, ProductOfferingFirebase>
            CoroutineScope(Dispatchers.IO).launch {
                val result = saveUserFirebase(newUser)
                Log.i("update user firebase", "result: $result")
                cancellableContinuation.resume(result) {}
            }
    }
}
