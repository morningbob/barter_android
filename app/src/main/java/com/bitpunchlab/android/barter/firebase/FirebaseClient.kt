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
import com.bitpunchlab.android.barter.models.ProductOffering
import com.bitpunchlab.android.barter.models.ProductOfferingAndBids
import com.bitpunchlab.android.barter.models.ProductOfferingAndProductsAsking
import com.bitpunchlab.android.barter.models.User
import com.bitpunchlab.android.barter.util.ProductImage
import com.bitpunchlab.android.barter.util.convertBidFirebaseToBid
import com.bitpunchlab.android.barter.util.convertBidToBidFirebase
import com.bitpunchlab.android.barter.util.convertBitmapToBytes
import com.bitpunchlab.android.barter.util.convertProductAskingFirebaseToProductAsking
import com.bitpunchlab.android.barter.util.convertProductAskingToFirebase
import com.bitpunchlab.android.barter.util.convertProductBiddingFirebaseToProductBidding
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
                        //prepareProductsBiddingForLocalDatabase()
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

    private suspend fun prepareProductsOffering() {
        val productsFirebase = CoroutineScope(Dispatchers.IO).async {
           retrieveProductsOfferingFirebase()
        }.await()

        val (products, askingProducts, bids) = decomposeProductOfferingFirebase(productsFirebase)

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
        val products = userFirebase.productsOffering.map { (key, product) ->
            product
        }

        val tripleResult = decomposeProductOfferingFirebase(products)
        val (productsOffering, askingProducts, bids) = tripleResult

        Log.i("prepare product offering", "no of products asking ${askingProducts.size}")
        Log.i("prepare product offering", "no of bids ${bids.size}")
        BarterRepository.insertProductsOffering(productsOffering)
        BarterRepository.insertProductsAsking(askingProducts)
        BarterRepository.insertBids(bids)
    }

    private fun decomposeProductOfferingFirebase(productsOfferingFirebase: List<ProductOfferingFirebase>) :
        Triple<List<ProductOffering>, List<ProductAsking>, List<Bid>> {
        val askingProducts = mutableListOf<ProductAsking>()
        val bids = mutableListOf<Bid>()
        val productsOffering = productsOfferingFirebase.map {
                product ->
            product.askingProducts.map { (askingKey, asking) ->
                askingProducts.add(convertProductAskingFirebaseToProductAsking(asking))
            }
            product.currentBids.map { (bidKey, bid) ->
                bids.add(convertBidFirebaseToBid(bid))
            }
            convertProductFirebaseToProduct(product)
        }
        return Triple(productsOffering, askingProducts, bids)
    }

    // the bids that users bidding, the bids that users accepted
    private fun prepareOpenTransactions(userFirebase: UserFirebase) {
        // save the product and bid from userFirebase, accepted bids and bidAccepted
        val acceptedBids = mutableListOf<AcceptBid>()
        //val bidsAccepted = mutableListOf<AcceptBid>()
        val products = mutableListOf<ProductOffering>()
        val bids = mutableListOf<Bid>()

        val allBids = mutableListOf<AcceptBid>()

        userFirebase.userAcceptedBids.map { (bidKey, acceptBid) ->
            Log.i("prepare open transactions", "processing one accepted bid")
            allBids.add(AcceptBid(acceptId = acceptBid.id, isSeller = true, userId = acceptBid.product!!.userId))
            products.add(convertProductFirebaseToProduct(acceptBid.product!!))
            bids.add(convertBidFirebaseToBid(acceptBid.bid!!))
        }

        userFirebase.userBidsAccepted.map { (bidKey, acceptBid) ->
            Log.i("prepare open transactions", "processing one bid accepted")
            allBids.add(AcceptBid(acceptId = acceptBid.id, isSeller = false, userId = acceptBid.bid!!.userId))
            products.add(convertProductFirebaseToProduct(acceptBid.product!!))
            bids.add(convertBidFirebaseToBid(acceptBid.bid!!))
        }

        CoroutineScope(Dispatchers.IO).launch {
            localDatabase!!.barterDao.insertAcceptBids(*acceptedBids.toTypedArray())
            localDatabase!!.barterDao.insertBids(*bids.toTypedArray())
            localDatabase!!.barterDao.insertProductsOffering(*products.toTypedArray())
        }
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
                               productImages: List<ProductImage>,
        askingProducts: List<ProductAsking>, askingProductImages: List<List<ProductImage>>) : Boolean {

        val pairResult =
            uploadImagesAndGetDownloadUrl(productOffering, askingProducts, askingProductImages, productImages)
        val semiUpdatedProductOffering = pairResult.first
        val askingProductsList = pairResult.second
        // query the asking products associated with the product offering
        //localDatabase!!.barterDao.getProductOfferingAndProductsAsking(pro)

        //val askingProductsUpdated = askingProductsList.map { product ->
        //    product.copy(productOfferingId = semiUpdatedProductOffering.productId)
        //}

        //semiUpdatedProductOffering.askingProducts = AskingProductsHolder(askingProductsList)
        // here, we need to wait for the processEachProduct to upload the images to cloud storage
        // and get back download url, then record in the product offering and asking product
        // so the product offering will be updated with both its own images and the asking products
        // objects. They can then be saved in user object in firestore.
        val productFirebase =
            convertProductOfferingToFirebase(semiUpdatedProductOffering, askingProductsList, listOf())

        val askingProductsFirebase = askingProductsList.map { each ->
            convertProductAskingToFirebase(each)
        }
        //val resultAskingDeferred = CoroutineScope(Dispatchers.IO).async {
        //    saveAllAskingProductFirebase(askingProductsFirebase)
        //}

        //val productOfferingFirebase = convertProductOfferingToFirebase(productOffering)
        val askingProductsFirebaseMap = HashMap<String, ProductAskingFirebase>()
        for (i in 0..askingProductsFirebase.size - 1) {
            askingProductsFirebaseMap.put(i.toString(), askingProductsFirebase[i])
        }

        productFirebase.askingProducts = askingProductsFirebaseMap

        val resultProductDeferred = CoroutineScope(Dispatchers.IO).async {
            saveProductOfferingFirebase(productFirebase)
        }

        //val resultAsking = resultAskingDeferred.await()

        val resultUpdateUser = updateProductSellingInUserFirebase(
            currentUserFirebase.value!!,
            productFirebase,
        )

        val resultProduct = resultProductDeferred.await()

        return resultProduct && resultUpdateUser//resultProduct && resultAsking && resultUpdateUser
    }

    private suspend fun uploadImagesAndGetDownloadUrl(productOffering: ProductOffering,
                                                      listOfAskingProducts: List<ProductAsking>,
                                                      askingProductImages: List<List<ProductImage>>,
                                                      productImages: List<ProductImage>) =
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
/*
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
*/
    private suspend fun processEachProduct(productId: String,
        productImages: List<ProductImage>) : Pair<List<String>, List<String>> {
        Log.i("process each product", "started")
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

    //suspend fun processAcceptBid(product: ProductOffering, bid: Bid) : Boolean {
        // upload pic and get url
        // write to Accept Bid collection

    //}

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

    private suspend fun uploadBidAccepted(bid: BidFirebase) =
        suspendCancellableCoroutine<Boolean> { cancellableContinuation ->
            Firebase.firestore
                .collection("acceptBids")
                .document(bid.id)
                .set(bid)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.i("upload accept bid", "success")
                        cancellableContinuation.resume(true) {}

                    } else {
                        Log.i("upload accept bid", "failed")
                        cancellableContinuation.resume(false) {}
                }
        }

    }

/*
    // to accept a bid, we modify the bid's accept flag
    //
    suspend fun processAcceptBid(product: ProductOffering, bid: Bid) : Boolean {

        //val productFirebase = convertProductBiddingToProductBiddingFirebase(productBidding)

        // save the bid in local database
        val updatedBid = bid.copy(bidAccepted = true)
        val bidFirebase = convertBidToBidFirebase(updatedBid)
        var productOfferingAskingProducts : ProductOfferingAndProductAsking? = null
         CoroutineScope(Dispatchers.IO).async {
            localDatabase!!.barterDao.getProductOfferingAndProductsAsking(product.productId)
        }.await().collect() {
             productOfferingAskingProducts = it[0]
        }
        /*
        val askingProductsList = productOfferingAskingProducts[0].askingProducts.map { product ->
            convertProductAskingToFirebase(product)
        }
        val askingProductsMap = HashMap<String, ProductAskingFirebase>()

        for (i in 0..productOfferingAskingProducts[0].askingProducts.size - 1) {
            askingProductsMap.put(i.toString(),
                convertProductAskingToFirebase(productOfferingAskingProducts[0].askingProducts[i]))
        }


         */
        if (productOfferingAskingProducts != null) {
            val productFirebase = convertProductOfferingToFirebase(
                product,
                productOfferingAskingProducts!!.askingProducts
            )

            val acceptBidFirebase = AcceptBidFirebase(
                acceptId = UUID.randomUUID().toString(),
                product = productFirebase,
                theBid = bidFirebase
            )
            return CoroutineScope(Dispatchers.IO).async {
                uploadAcceptBid(acceptBidFirebase)
            }.await()
        } else {
            return false
        }
    }

 */

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



/*
    private suspend fun saveProductBiddingFirebase(bidFirebase: BidFirebase) : Boolean =
        suspendCancellableCoroutine { cancellableContinuation ->
            cancellableContinuation.resume(false) {}
        }

*/
    /*
        val askingProducts = mutableListOf<ProductAsking>()
        val bids = mutableListOf<Bid>()
        val productsOffering = userFirebase.productsOffering.map {
                (productKey, product) ->
            product.askingProducts.map { (askingKey, asking) ->
                askingProducts.add(convertProductAskingFirebaseToProductAsking(asking))
            }
            product.currentBids.map { (bidKey, bid) ->
                bids.add(convertBidFirebaseToBid(bid))
            }
            convertProductFirebaseToProduct(product)
        }
*/
}
