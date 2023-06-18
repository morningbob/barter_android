package com.bitpunchlab.android.barter.firebase

import android.graphics.Bitmap
import android.util.Log
import com.bitpunchlab.android.barter.database.BarterDatabase
import com.bitpunchlab.android.barter.database.BarterRepository
import com.bitpunchlab.android.barter.firebase.models.BidFirebase
import com.bitpunchlab.android.barter.firebase.models.ProductAskingFirebase
import com.bitpunchlab.android.barter.firebase.models.ProductBiddingFirebase
import com.bitpunchlab.android.barter.firebase.models.ProductOfferingFirebase
import com.bitpunchlab.android.barter.firebase.models.UserFirebase
import com.bitpunchlab.android.barter.models.AskingProductsHolder
import com.bitpunchlab.android.barter.models.Bid
import com.bitpunchlab.android.barter.models.BidsHolder
import com.bitpunchlab.android.barter.models.ProductAsking
import com.bitpunchlab.android.barter.models.ProductBidding
import com.bitpunchlab.android.barter.models.ProductOffering
import com.bitpunchlab.android.barter.models.User
import com.bitpunchlab.android.barter.util.ProductImage
import com.bitpunchlab.android.barter.util.ProductType
import com.bitpunchlab.android.barter.util.convertBidToBidFirebase
import com.bitpunchlab.android.barter.util.convertBitmapToBytes
import com.bitpunchlab.android.barter.util.convertProductAskingToFirebase
import com.bitpunchlab.android.barter.util.convertProductBiddingFirebaseToProductBidding
import com.bitpunchlab.android.barter.util.convertProductBiddingToProductBiddingFirebase
import com.bitpunchlab.android.barter.util.convertProductFirebaseToProduct
import com.bitpunchlab.android.barter.util.convertProductOfferingToFirebase
import com.bitpunchlab.android.barter.util.convertUserFirebaseToUser
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
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
                        prepareProductsBiddingForLocalDatabase()
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
        BarterRepository.insertCurrentUser(user)
    }

    private fun prepareProductsOfferingForLocalDatabase(userFirebase: UserFirebase) {
        // retrieve the products offering and products bidding info
        // create those objects and save in local database
        // this includes removing the outdated products objects in the database
        // here the productsOffering in the user firebase object
        // has the full product offering object stored in it
        val productsOffering = userFirebase.productsOffering.map { (key, value) ->
            convertProductFirebaseToProduct(value)
        }

        BarterRepository.insertProductsOffering(productsOffering)

    }

    private fun prepareProductsBiddingForLocalDatabase() {
        CoroutineScope(Dispatchers.IO).launch {
            val productsBiddingFirebaseDeferred = CoroutineScope(Dispatchers.IO).async {
                retrieveProductsBidding()
            }
            val productsBiddingFirebase = productsBiddingFirebaseDeferred.await()
            Log.i("prepare products bidding", "got from firestore ${productsBiddingFirebase.size}")
            val productsBidding = productsBiddingFirebase.map { product ->
                convertProductBiddingFirebaseToProductBidding(product)
            }
            BarterRepository.insertProductsBidding(productsBidding)
        }
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

    private suspend fun retrieveProductsBidding() =
        suspendCancellableCoroutine<List<ProductBiddingFirebase>> {
            cancellableContinuation ->

            Firebase.firestore
                .collection("productsBidding")
                .get()
                .addOnSuccessListener { snapshot ->
                    Log.i("retrieve products bidding", "success")
                    if (snapshot.documents.isNotEmpty()) {
                        val productsBidding = mutableListOf<ProductBiddingFirebase>()
                        for (productDoc in snapshot.documents) {
                            productDoc.toObject<ProductBiddingFirebase>()?.let {
                                productsBidding.add(it)
                            }
                        }
                        cancellableContinuation.resume(productsBidding) {}

                    } else {
                        Log.i("retrieve products bidding", "is empty")
                    }
                }
                .addOnFailureListener { e ->
                    Log.i("retrieve products bidding", "failed ${e}")
                }
    }

    suspend fun retrieveProductBidding(productOfferingId: String) : ProductBiddingFirebase? =
        suspendCancellableCoroutine { cancellableContinuation ->
            Firebase.firestore
                .collection("productsBidding")
                .whereEqualTo("productOfferingId", productOfferingId)
                .get()
                .addOnSuccessListener { snapshot ->
                    if (snapshot.documents.isNotEmpty()) {
                        Log.i("retrieve product bidding", "success")
                        val productBidding = snapshot.documents[0].toObject<ProductBiddingFirebase>()
                        cancellableContinuation.resume(productBidding) {}
                    } else {
                        Log.i("retrieve product bidding", "product doesn't exist")
                        cancellableContinuation.resume(null) {}
                    }
                }
                .addOnFailureListener { e ->
                    Log.i("retrieve product bidding", "failure $e")
                    cancellableContinuation.resume(null) {}
                }

        }

    suspend fun processSelling(productOffering: ProductOffering,
                               productImages: List<ProductImage>,
        askingProducts: List<ProductAsking>, askingProductImages: List<List<ProductImage>>) : Boolean {

        val pairResult = uploadImagesAndGetDownloadUrl(productOffering, askingProducts, askingProductImages, productImages)
        val semiUpdatedProductOffering = pairResult.first
        val askingProductsList = pairResult.second
        semiUpdatedProductOffering.askingProducts = AskingProductsHolder(askingProductsList)
        // here, we need to wait for the processEachProduct to upload the images to cloud storage
        // and get back download url, then record in the product offering and asking product
        // so the product offering will be updated with both its own images and the asking products
        // objects. They can then be saved in user object in firestore.
        val productFirebase =
            convertProductOfferingToFirebase(semiUpdatedProductOffering)

        val askingProductsFirebase = askingProductsList.map { each ->
            convertProductAskingToFirebase(each)
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

        val resultProduct = resultProductDeferred.await()

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
                    processEachProduct(productOffering.productId, productImages)

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

    private suspend fun processEachProduct(
                                               productId: String,
        productImages: List<ProductImage>) : Pair<List<String>, List<String>> {
        val downloadUrlList = mutableListOf<String>()
        val imageFilenames = mutableListOf<String>()

        val images = productImages.map { it.image }

        val pairProductResult = uploadImages(
            productId = productId,
            images = images)

        downloadUrlList.addAll(pairProductResult.first)
        imageFilenames.addAll(pairProductResult.second)

        Log.i(
            "process selling",
            "adding downloadUrl to product offering, items: ${downloadUrlList.size}"
        )

        return Pair(downloadUrlList, imageFilenames)
    }

    private suspend fun uploadImages(productId: String, images: List<Bitmap>) :
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

        suspendCancellableCoroutine { cancellableContinuation ->
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

    // we modify the product bidding's bids field.  add the bid to it.
    suspend fun processBidding(productBidding: ProductBidding, bid: Bid, images: List<Bitmap>) : Boolean {

        val downloadUrlResult = uploadImages(productBidding.productId, images)
        val downloadUrls = downloadUrlResult.first
        val filenames = downloadUrlResult.second

        val newBids = productBidding.bidsHolder.bids.toMutableList()
        // we update the url we got after uploading the images
        bid.bidProduct?.images = downloadUrls
        newBids.add(bid)
        val newProduct = productBidding.copy(bidsHolder = BidsHolder(newBids))

        //newProduct.images = downloadUrls

        return CoroutineScope(Dispatchers.IO).async {
            return@async saveProductBiddingFirebase(convertProductBiddingToProductBiddingFirebase(newProduct))
        }.await()
    }
    private suspend fun saveProductBiddingFirebase(productBiddingFirebase: ProductBiddingFirebase) =
        suspendCancellableCoroutine<Boolean> { cancellableContinuation ->
            Firebase.firestore
                .collection("productsBidding")
                .document(productBiddingFirebase.id)
                .set(productBiddingFirebase)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.i("update product bidding for bids", "success")
                        cancellableContinuation.resume(true) {}
                    } else {
                        Log.i("update product bidding for bids", "failed ${task.exception}")
                        cancellableContinuation.resume(false) {}
                    }
                }
        }
/*
    private suspend fun saveProductBiddingFirebase(bidFirebase: BidFirebase) : Boolean =
        suspendCancellableCoroutine { cancellableContinuation ->
            cancellableContinuation.resume(false) {}
        }

*/
}
