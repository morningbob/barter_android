package com.bitpunchlab.android.barter.firebase

import android.graphics.Bitmap
import android.util.Log
import com.bitpunchlab.android.barter.database.BarterDatabase
import com.bitpunchlab.android.barter.database.BarterRepository
import com.bitpunchlab.android.barter.firebase.models.AcceptBidFirebase
import com.bitpunchlab.android.barter.firebase.models.BidFirebase
import com.bitpunchlab.android.barter.firebase.models.ProductAskingFirebase
import com.bitpunchlab.android.barter.firebase.models.ProductBiddingFirebase
import com.bitpunchlab.android.barter.firebase.models.ProductOfferingFirebase
import com.bitpunchlab.android.barter.firebase.models.UserFirebase
import com.bitpunchlab.android.barter.models.AcceptBid
import com.bitpunchlab.android.barter.models.Bid
import com.bitpunchlab.android.barter.models.ProductAsking
import com.bitpunchlab.android.barter.models.ProductImageToDisplay
import com.bitpunchlab.android.barter.models.ProductOffering
import com.bitpunchlab.android.barter.models.ProductOfferingAndBids
import com.bitpunchlab.android.barter.models.ProductOfferingAndProductsAsking
import com.bitpunchlab.android.barter.models.User
import com.bitpunchlab.android.barter.util.AppStatus
import com.bitpunchlab.android.barter.util.ImageHandler
import com.bitpunchlab.android.barter.util.LoginStatus
import com.bitpunchlab.android.barter.util.MainStatus
import com.bitpunchlab.android.barter.util.ProductOfferingDecomposed
import com.bitpunchlab.android.barter.util.convertBidFirebaseToBid
import com.bitpunchlab.android.barter.util.convertBidToBidFirebase
import com.bitpunchlab.android.barter.util.convertBitmapToBytes
import com.bitpunchlab.android.barter.util.convertProductAskingFirebaseToProductAsking
import com.bitpunchlab.android.barter.util.convertProductAskingToFirebase
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
import kotlin.math.acos

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
            } //else {
                // retrieve user from firestore
                CoroutineScope(Dispatchers.IO).launch {
                    //_userId.value = auth.cu
                    Log.i("auth", "user id: ${auth.currentUser!!.uid}")
                    val currentUser = retrieveUserFirebase(auth.currentUser!!.uid)
                    currentUser?.let {
                        _currentUserFirebase.value = currentUser
                        saveUserLocalDatabase(convertUserFirebaseToUser(currentUser))
                        prepareProductsOffering()
                        prepareProductsInUserForLocalDatabase(currentUser)
                        prepareOpenTransactions(currentUser)
                        prepareTransactionRecords(currentUser)
                    }
               // }
            }
        } else {
            Log.i("fire auth", "auth is null")
            isLoggedIn.value = false
        }
    }

    init {
        auth.addAuthStateListener(authStateListener)
    }

    suspend fun login(email: String, password: String) : LoginStatus =
        suspendCancellableCoroutine<LoginStatus> { cancellableContinuation ->
        auth
            .signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.i("fire auth", "login successful")
                    cancellableContinuation.resume(LoginStatus.LOGGED_IN) {}
                } else {
                    Log.i("fire auth", "failed to login ${task.exception}")
                    cancellableContinuation.resume(LoginStatus.LOGIN_SERVER_ERROR) {}
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

    suspend fun changePasswordFirebaseAuth(currentPassword: String, newPassword: String) : MainStatus =
        suspendCancellableCoroutine { cancellableContinuation ->
            if (auth.currentUser != null) {
                auth
                    .signInWithEmailAndPassword(auth.currentUser!!.email!!, currentPassword)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.i("change password firebase auth", "signed in")
                            auth.currentUser?.updatePassword(newPassword)
                                ?.addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        Log.i("change password firebase auth", "change pass sucess")
                                        cancellableContinuation.resume(MainStatus.SUCCESS) {}
                                    } else {
                                        Log.i(
                                            "change password firebase auth",
                                            "failed ${task.exception}"
                                        )
                                        cancellableContinuation.resume(MainStatus.FAILED_SERVER_ERROR) {}
                                    }
                                }
                        } else {
                            Log.i("change password firebase auth", "failed to sign in, probably password wrong. ${task.exception}")
                            cancellableContinuation.resume(MainStatus.FAILED_INCORRECT_PASSWORD) {}
                        }
                    }
            } else {
                Log.i("change password firebase auth", "auth null")
                cancellableContinuation.resume(MainStatus.FAILED_APPLICATION_ERROR) {}
            }
    }

    suspend fun sendResetPasswordLink(email: String) : LoginStatus =
        suspendCancellableCoroutine { cancellableContinuation ->
            auth.sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.i("send reset email", "success")
                        cancellableContinuation.resume(LoginStatus.RESET_PASSWORD_SUCCESS) {}
                    } else {
                        Log.i("send reset email", "failed ${task.exception}")
                        cancellableContinuation.resume(LoginStatus.RESET_SERVER_ERROR) {}
                    }
                }
    }

    private suspend fun prepareProductsOffering() {
        val productsFirebase = CoroutineScope(Dispatchers.IO).async {
           retrieveProductsOfferingFirebase()
        }.await()

        val (products, askingProducts, bids) =
            decomposeProductOfferingFirebase(productsFirebase)

        //Log.i("firebase client", "products retrieved")
        BarterRepository.insertProductsOffering(products)
        BarterRepository.insertProductsAsking(askingProducts)
        BarterRepository.insertBids(bids)
    }

    private suspend fun retrieveProductsOfferingFirebase() =
        suspendCancellableCoroutine<List<ProductOfferingFirebase>> { cancellableContinuation ->
            Firebase.firestore
                .collection("productsOffering")
                .get()
                .addOnSuccessListener { snapshot ->
                    if (snapshot.documents.isNotEmpty()) {
                        Log.i("retrieve products", "succeeded")
                        val products = mutableListOf<ProductOfferingFirebase>()

                        snapshot.documents.map { doc ->
                            doc.toObject(ProductOfferingFirebase::class.java)?.let {
                                products.add(it)
                            }
                        }
                        cancellableContinuation.resume(products) {}
                    }
                }
                .addOnFailureListener { e ->
                    Log.i("retrieve products", "failed")
                    cancellableContinuation.resume(listOf()) {}
                }
        }

    // we prepare the asking products, bids,
    private fun prepareProductsInUserForLocalDatabase(userFirebase: UserFirebase) {
        // retrieve the products offering and products bidding info
        // create those objects and save in local database
        // this includes removing the outdated products objects in the database
        // here the productsOffering in the user firebase object
        // has the full product offering object stored in it
        // asking products
        val products = userFirebase.productsOffering.map {
                (key, product) ->
            product
        }

        CoroutineScope(Dispatchers.IO).launch {
            val decomposed = CoroutineScope(Dispatchers.IO).async {
                decomposeProductOfferingFirebase(products)
            }.await()

            //Log.i("firebase client", "in user, got products ${decomposed.productsOffering.size}")
            BarterRepository.insertProductsOffering(decomposed.productsOffering)
            BarterRepository.insertProductsAsking(decomposed.askingProducts)
            BarterRepository.insertBids(decomposed.bids)
            BarterRepository.insertImages(decomposed.images)
        }
        //Log.i("prepare product offering", "no of products asking ${askingProducts.size}")
        //Log.i("prepare product offering", "no of bids ${bids.size}")
    }

    private suspend fun decomposeProductOfferingFirebase(productsOfferingFirebase: List<ProductOfferingFirebase>) :
        ProductOfferingDecomposed
    {
        //Triple<List<ProductOffering>, List<ProductAsking>, List<Bid>> {
        val askingProducts = mutableListOf<ProductAsking>()
        val bids = mutableListOf<Bid>()
        var productImages = mutableListOf<ProductImageToDisplay>()
        val productsOffering = productsOfferingFirebase.map {
                product ->
            product.askingProducts.map { (askingKey, asking) ->
                askingProducts.add(convertProductAskingFirebaseToProductAsking(asking))
            }
            product.currentBids.map { (bidKey, bid) ->
                bids.add(convertBidFirebaseToBid(bid))
            }
            // before we create a new product image,
            // we try to retrieve the image locally by the imageUrlCloud
            // if we get back the product image, we do nothing
            // if not, we create a new product image
            // retrieve the image from cloud storage
            // and save locally, we got the local uri and save it in product image's imageUrlLocal
            val productImagesUrl = product.images.map { (imageKey, imageUrl) ->
                imageUrl
            }
            productImages = CoroutineScope(Dispatchers.IO).async {
                processImagesFromProduct(productImagesUrl)
            }.await().toMutableList()

            convertProductFirebaseToProduct(product)
        }
        return ProductOfferingDecomposed(
            productsOffering = productsOffering,
            askingProducts = askingProducts,
            bids = bids,
            images = productImages
        )
        //return Triple(productsOffering, askingProducts, bids)
    }

    private suspend fun processImagesFromProduct(images: List<String>) : List<ProductImageToDisplay> {
        val productImages = mutableListOf<ProductImageToDisplay>()

        images.map { imageUrl ->
            // now, for every image, launch a scope to wait for the result
            Log.i("decompose product offering", "processing each image in a product")
            CoroutineScope(Dispatchers.IO).launch {
                val result = CoroutineScope(Dispatchers.IO).async {
                    Log.i("decompose product offering", "try to retrieve image locally")
                    retrieveImageLocally(imageUrl)
                }.await()
                if (result.isNullOrEmpty()) {
                    Log.i("decompose product offering", "result is null")
                    // retrieve the image from cloud storage
                    //val image = ImageHandler.loadImageFromCloud(imageUrl)
                    ImageHandler.loadImageFromCloud(imageUrl)?.let { bitmap ->
                        Log.i("decompose product offering", "got image")
                        val localUrl = ImageHandler.saveImageExternalStorage(imageUrl, bitmap)
                        Log.i("decompose product offering", "processing imageUrl ${imageUrl}")
                        localUrl?.let {
                            Log.i("decompose product offering", "got localUrl ${it}")
                            productImages.add(
                                ProductImageToDisplay(
                                    imageId = UUID.randomUUID().toString(),
                                    imageUrlCloud = imageUrl,
                                    imageUrlLocal = it.toString()
                                )
                            )
                        }
                    }
                } else {
                    Log.i("decompose product offering", "retrieved the product image!")
                    Log.i("decompose product offering", "image url: ${result[0].imageUrlCloud}")
                }
            }.join()  // we wait for all coroutines to finish before we return
        }
        return productImages
    }

    private suspend fun retrieveImageLocally(imageUrl: String) : List<ProductImageToDisplay>? {
        return CoroutineScope(Dispatchers.IO).async {
            BarterRepository.getImage(imageUrl)
        }.await()
    }

    // the bids that users bidding, the bids that users accepted
    private fun prepareOpenTransactions(userFirebase: UserFirebase) {
        // save the product and bid from userFirebase, accepted bids and bidAccepted
        val acceptedBids = mutableListOf<AcceptBid>()
        //val bidsAccepted = mutableListOf<AcceptBid>()
        val products = mutableListOf<ProductOffering>()
        val bids = mutableListOf<Bid>()

        userFirebase.userAcceptedBids.map { (bidKey, acceptBid) ->
            Log.i("prepare open transactions", "processing one accepted bid")
           //Log.i("prepare open transactions", "the userId of the accept bid ${acceptBid.product?.userId}")
            //Log.i("prepare open transactions", "current userId ${currentUserFirebase.value!!.id}")
            acceptedBids.add(AcceptBid(acceptId = acceptBid.id, isSeller = true, userId = acceptBid.product!!.userId))
            products.add(convertProductFirebaseToProduct(acceptBid.product!!))
            bids.add(convertBidFirebaseToBid(acceptBid.bid!!))
        }

        userFirebase.userBidsAccepted.map { (bidKey, acceptBid) ->
            //Log.i("prepare open transactions", "processing one bid accepted")
            acceptedBids.add(AcceptBid(acceptId = acceptBid.id, isSeller = false, userId = acceptBid.bid!!.userId))
            products.add(convertProductFirebaseToProduct(acceptBid.product!!))
            bids.add(convertBidFirebaseToBid(acceptBid.bid!!))
        }

        CoroutineScope(Dispatchers.IO).launch {
            localDatabase!!.barterDao.insertAcceptBids(*acceptedBids.toTypedArray())
            localDatabase!!.barterDao.insertBids(*bids.toTypedArray())
            Log.i("prepare open transactions", "bids to be saved ${bids.size}")
            localDatabase!!.barterDao.insertProductsOffering(*products.toTypedArray())
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

    private suspend fun prepareTransactionRecords(currentUser: UserFirebase) {
        val acceptedBids =
            currentUser.userAcceptedBids.map { (key, value) ->
                //convertAcceptBidFirebaseToAcceptBid(value)
            }
        val bidsAccepted =
        currentUser.userBidsAccepted.map { (key, value) ->
            //convertAcceptBidFirebaseToAcceptBid(value)
        }

        //localDatabase!!.barterDao.insertAcceptedBids(*acceptedBids.toTypedArray())
        //localDatabase!!.barterDao.insertAcceptedBids(*bidsAccepted.toTypedArray())
    }

    suspend fun processSelling(productOffering: ProductOffering,
                               productImages: List<ProductImageToDisplay>,
        askingProducts: List<ProductAsking>, askingProductImages: List<List<ProductImageToDisplay>>) : Boolean {

        val pairResult =
            uploadImagesAndGetDownloadUrl(productOffering, askingProducts, askingProductImages, productImages)
        val semiUpdatedProductOffering = pairResult.first
        val askingProductsList = pairResult.second
        // query the asking products associated with the product offering

        // here, we need to wait for the processEachProduct to upload the images to cloud storage
        // and get back download url, then record in the product offering and asking product
        // so the product offering will be updated with both its own images and the asking products
        // objects. They can then be saved in user object in firestore.
        val productFirebase =
            convertProductOfferingToFirebase(semiUpdatedProductOffering, askingProductsList, listOf())

        val askingProductsFirebase = askingProductsList.map { each ->
            convertProductAskingToFirebase(each)
        }

        val askingProductsFirebaseMap = HashMap<String, ProductAskingFirebase>()
        for (i in 0..askingProductsFirebase.size - 1) {
            askingProductsFirebaseMap.put(i.toString(), askingProductsFirebase[i])
        }

        productFirebase.askingProducts = askingProductsFirebaseMap

        val resultProductDeferred = CoroutineScope(Dispatchers.IO).async {
            saveProductOfferingFirebase(productFirebase)
        }

        val resultUpdateUser = updateProductSellingInUserFirebase(
            currentUserFirebase.value!!,
            productFirebase,
        )

        val resultProduct = resultProductDeferred.await()

        return resultProduct && resultUpdateUser
    }

    private suspend fun uploadImagesAndGetDownloadUrl(productOffering: ProductOffering,
                                                      listOfAskingProducts: List<ProductAsking>,
                                                      askingProductImages: List<List<ProductImageToDisplay>>,
                                                      productImages: List<ProductImageToDisplay>) =
        suspendCancellableCoroutine<Pair<ProductOffering, List<ProductAsking>>> { cancellableContinuation ->
            Log.i("upload", "started")
            CoroutineScope(Dispatchers.IO).launch {
                //val askingProductsList = mutableListOf<ProductAsking>()
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
    private suspend fun processEachProduct(productId: String,
        productImages: List<ProductImageToDisplay>) : Pair<List<String>, List<String>> {
        Log.i("process each product", "started")
        val downloadUrlList = mutableListOf<String>()
        val imageFilenames = mutableListOf<String>()

        val images = productImages.map { it.image!! }

        val pairProductResult = uploadImages(
            productId = productId,
            images = images)

        downloadUrlList.addAll(pairProductResult.first)
        imageFilenames.addAll(pairProductResult.second)

        //Log.i(
        //    "process selling",
        //    "adding downloadUrl to product offering, items: ${downloadUrlList.size}"
        //)

        return Pair(downloadUrlList, imageFilenames)
    }

    private suspend fun uploadImages(productId: String, images: List<Bitmap>) :
     Pair<List<String>, List<String>> {
        Log.i("upload images", "started")
        val lock1 = Any()
        val downloadUrlList = mutableListOf<String>()
        val imageFilenames = mutableListOf<String>()

        for (i in 0..images.size - 1) {
            CoroutineScope(Dispatchers.IO).launch {
                val filename = "${productId}_${i}.jpg"
                Log.i("process selling", "creating filename $filename")
                val pair = saveImageCloudStorage(images[i], filename)
                synchronized(lock1) {
                    Log.i("lock", "inside lock")
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
            Log.i("save image in cloud storage", "started")

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

    private suspend fun saveProductOfferingFirebase(product: ProductOfferingFirebase) : Boolean =

        suspendCancellableCoroutine { cancellableContinuation ->
            //var collection = if (type == ProductType.PRODUCT) "productsOffering" else "askingProducts"

            //Log.i("save product offering", "started")
            Firebase.firestore
                .collection("productsOffering")
                .document(product.id)
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
            Log.i("update user", "started")

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
    suspend fun processBidding(productOffering: ProductOffering, bid: Bid, images: List<Bitmap>) : Boolean {

        val downloadUrlResult = uploadImages(bid.bidProductId, images)
        val downloadUrls = downloadUrlResult.first
        //val filenames = downloadUrlResult.second

        val newBidProduct = bid.bidProduct.copy(images = downloadUrls)
        val newBid = bid.copy(bidProduct = newBidProduct)
        //var newProduct : ProductOfferingFirebase

        // retrieve the bids the product has, in the past, add the latest to the end
        val productBidList = CoroutineScope(Dispatchers.IO).async {
            retrieveProductBids(productOffering.productId)
        }.await()

        val productAskingList = CoroutineScope(Dispatchers.IO).async {
            retrieveProductAskingProducts(productOffering.productId)
        }.await()

        val newBids = productBidList[0].bids.toMutableList()
        Log.i("process bidding", "bids got from old list ${newBids.size}")
        newBids.add(newBid)

        // need to update user object of the seller
        // cloud function
        // also create a cloud function call bid

        val resultProductOfferingDeferred = CoroutineScope(Dispatchers.IO).async {
            return@async saveProductOfferingFirebase(
                convertProductOfferingToFirebase(productOffering, productAskingList[0].askingProducts, newBids)
            )
        }//.await()

        val resultBidCollectionDeferred = CoroutineScope(Dispatchers.IO).async {
            return@async uploadBid(convertBidToBidFirebase(bid))
        }//.await()

        // we await them in these way, so the 2 requests can be processed concurrently.
        val resultProductOffering = resultProductOfferingDeferred.await()
        val resultBidCollection = resultBidCollectionDeferred.await()

        return resultProductOffering && resultBidCollection
    }

    private suspend fun retrieveProductBids(id: String) : List<ProductOfferingAndBids> {
        return CoroutineScope(Dispatchers.IO).async {
            localDatabase!!.barterDao.getProductOfferingAndBidsAsList(id)
        }.await()
    }
  
    private suspend fun retrieveProductAskingProducts(id: String) : List<ProductOfferingAndProductsAsking> {
        return CoroutineScope(Dispatchers.IO).async {
            localDatabase!!.barterDao.getProductOfferingAndProductsAskingAsList(id)
        }.await()
    }

    private suspend fun uploadBid(bid: BidFirebase) =
        suspendCancellableCoroutine<Boolean> { cancellableContinuation ->
            Firebase.firestore
                .collection("bid")
                .document(bid.id)
                .set(bid)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.i("upload bid", "success")
                        cancellableContinuation.resume(true) {}
                    } else {
                        Log.i("upload bid", "failed ${task.exception}")
                        cancellableContinuation.resume(false) {}
                }
        }
    }

    suspend fun processAcceptBid(product: ProductOffering, bid: Bid) : Boolean {
        // write to accept bid collection

        // turn  the product offering into waiting status in collection
        // update user object (buyers and seller) for waiting transaction

        // let buyer and seller send exchange message
        //
        //return uploadBidAccepted(convertBidToBidFirebase(bid))
        val acceptBid = AcceptBidFirebase(
            acceptId = UUID.randomUUID().toString(),
            // we provide empty asking products and empty bids
            // we only need the other info
            productOffering = convertProductOfferingToFirebase(product, listOf(), listOf()),
            theBid = convertBidToBidFirebase(bid)
        )
        return uploadAcceptBid(acceptBid)
        //return false
    }

    suspend fun processDeleteProduct(product: ProductOffering) : Boolean {
        val report = HashMap<String, String>()
        report.put("productId", product.productId)
        report.put("userId", product.userId)

        return CoroutineScope(Dispatchers.IO).async { deleteProductFirebase(report) }.await()

    }

    // write to the delete collection
    // the cloud function deletes the product from productsOffering collection
    // and the owner user object.
    // this approach is better for the server to reverse changes

    suspend fun deleteProductFirebase(report: HashMap<String, String>) =
        suspendCancellableCoroutine<Boolean> { cancellableContinuation ->

        Firebase.firestore
            .collection("deleteProduct")
            .document(report.get("productId")!!)
            .set(report)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.i("delete product firebase", "success")
                    cancellableContinuation.resume(true) {}
                } else {
                    Log.i("delete product firebase", "failed ${task.exception}")
                    cancellableContinuation.resume(false) {}
                }
            }
    }

    suspend fun processDeleteAskingProduct(product: ProductOffering, asking: ProductAsking)
    : Boolean {
        var updatedAskingProduct = mapOf<String, ProductAskingFirebase>()

        currentUserFirebase.value?.let { user ->
            user.productsOffering.map { (productKey, productValue) ->
                if (productValue.id == product.productId) {
                    updatedAskingProduct = productValue.askingProducts.filterNot { it.key != asking.productId }
                }
            }
            user.productsOffering[product.productId]!!.askingProducts = updatedAskingProduct as HashMap<String, ProductAskingFirebase>
            // update user in firebase
            //Log.i("delete asking product", "will update user.")
            return CoroutineScope(Dispatchers.IO).async {
                saveUserFirebase(user)
            }.await()
        }
        return false
    }

    private suspend fun uploadAcceptBid(acceptBidFirebase: AcceptBidFirebase) =
        suspendCancellableCoroutine<Boolean> { cancellableContinuation ->

            Firebase.firestore
                .collection("acceptBids")
                .document()
                .set(acceptBidFirebase)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.i("upload accept bid", "success")
                        cancellableContinuation.resume(true) {}
                    } else {
                        Log.i("upload accept bid", "failed ${task.exception}")
                        cancellableContinuation.resume(false) {}
                    }
                }
        }
}
