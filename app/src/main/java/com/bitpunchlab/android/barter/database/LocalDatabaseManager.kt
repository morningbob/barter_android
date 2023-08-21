package com.bitpunchlab.android.barter.database

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import com.bitpunchlab.android.barter.firebase.FirebaseClient
import com.bitpunchlab.android.barter.models.AcceptBid
import com.bitpunchlab.android.barter.models.Bid
import com.bitpunchlab.android.barter.models.BidWithDetails
import com.bitpunchlab.android.barter.models.Message
import com.bitpunchlab.android.barter.models.ProductAsking
import com.bitpunchlab.android.barter.models.ProductImageToDisplay
import com.bitpunchlab.android.barter.models.ProductOffering
import com.bitpunchlab.android.barter.models.ProductOfferingAndBids
import com.bitpunchlab.android.barter.models.ProductOfferingAndProductsAsking
import com.bitpunchlab.android.barter.models.User
import com.bitpunchlab.android.barter.util.ImageHandler
import com.bitpunchlab.android.barter.util.parseDateTime
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DEBUG_PROPERTY_NAME
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.takeWhile
import kotlinx.coroutines.internal.synchronized
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.UUID

// the manager is responsible to retrieve various data from the local database
// I need to avoid to retrieve data from the various screens' view model.
// it duplicates the job when user navigate back and forth
// there is also exception when reading from various states.
// when it starts, it observes the userId from FirebaseClient
// when it got the id, it starts retrieving all the information the app needs
// to display in the screens
object LocalDatabaseManager {

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser : StateFlow<User?> get() = _currentUser.asStateFlow()

    private var _allProductsOffering = MutableStateFlow<List<ProductOffering>>(listOf())
    val allProductsOffering : StateFlow<List<ProductOffering>> get() = _allProductsOffering.asStateFlow()

    private var _userProductsOffering = MutableStateFlow<List<ProductOffering>>(listOf())
    val userProductsOffering : StateFlow<List<ProductOffering>> get() = _userProductsOffering.asStateFlow()

    private val _productChosen = MutableStateFlow<ProductOffering?>(null)
    val productChosen : StateFlow<ProductOffering?> get() = _productChosen.asStateFlow()

    private var _sellerProductImages = MutableStateFlow<SnapshotStateList<ProductImageToDisplay>>(mutableStateListOf())
    val sellerProductImages : StateFlow<SnapshotStateList<ProductImageToDisplay>> get() = _sellerProductImages.asStateFlow()

    private var _askingProductImages = MutableStateFlow<SnapshotStateList<MutableList<ProductImageToDisplay>>>(mutableStateListOf())
    val askingProductImages : StateFlow<MutableList<MutableList<ProductImageToDisplay>>> get() = _askingProductImages.asStateFlow()

    private val _productOfferingWithProductsAsking = MutableStateFlow<ProductOfferingAndProductsAsking?>(null)
    val productOfferingWithProductsAsking : StateFlow<ProductOfferingAndProductsAsking?>
        get() = _productOfferingWithProductsAsking.asStateFlow()

    private val _bids = MutableStateFlow<List<Bid>>(listOf())
    val bids : StateFlow<List<Bid>> get() = _bids.asStateFlow()

    private val _bidChosen = MutableStateFlow<Bid?>(null)
    val bidChosen : StateFlow<Bid?> get() = _bidChosen.asStateFlow()

    private var _bidProductImages = MutableStateFlow<SnapshotStateList<ProductImageToDisplay>>(mutableStateListOf())
    val bidProductImages : StateFlow<SnapshotStateList<ProductImageToDisplay>> get() = _bidProductImages.asStateFlow()

    private val _allAcceptBids = MutableStateFlow<List<AcceptBid>>(listOf())
    val allAcceptBids : StateFlow<List<AcceptBid>> get() = _allAcceptBids.asStateFlow()

    private val _bidsDetail = MutableStateFlow<MutableList<BidWithDetails>>(mutableListOf())
    val bidsDetail : StateFlow<MutableList<BidWithDetails>> get() = _bidsDetail.asStateFlow()

    private val _acceptedBidsDetail = MutableStateFlow<MutableList<BidWithDetails>>(mutableListOf())
    val acceptedBidsDetail : StateFlow<MutableList<BidWithDetails>> get() = _acceptedBidsDetail.asStateFlow()

    private val _bidsAcceptedDetail = MutableStateFlow<MutableList<BidWithDetails>>(mutableListOf())
    val bidsAcceptedDetail : StateFlow<MutableList<BidWithDetails>> get() = _bidsAcceptedDetail.asStateFlow()

    private val _currentBids = MutableStateFlow<SnapshotStateList<Bid>>(mutableStateListOf())
    val currentBids : StateFlow<SnapshotStateList<Bid>> get() = _currentBids.asStateFlow()

    private val _currentBidsDetails = MutableStateFlow<SnapshotStateList<BidWithDetails>>(mutableStateListOf())
    val currentBidsDetails : StateFlow<SnapshotStateList<BidWithDetails>> get() = _currentBidsDetails.asStateFlow()

    private var _bidProductOfferingImages = MutableStateFlow<SnapshotStateList<ProductImageToDisplay>>(mutableStateListOf())
    val bidProductOfferingImages : StateFlow<SnapshotStateList<ProductImageToDisplay>> get() = _bidProductOfferingImages.asStateFlow()

    private val _chosenCurrentBid = MutableStateFlow<BidWithDetails?>(null)
    val chosenCurrentBid : StateFlow<BidWithDetails?> get() = _chosenCurrentBid.asStateFlow()

    private val _allMessages = MutableStateFlow<SnapshotStateList<Message>>(mutableStateListOf<Message>())
    val allMessages : StateFlow<SnapshotStateList<Message>> get() = _allMessages.asStateFlow()

    private val _messagesReceived = MutableStateFlow<SnapshotStateList<Message>>(mutableStateListOf<Message>())
    val messagesReceived : StateFlow<SnapshotStateList<Message>> get() = _messagesReceived.asStateFlow()

    private val _messagesSent = MutableStateFlow<SnapshotStateList<Message>>(mutableStateListOf<Message>())
    val messagesSent : StateFlow<SnapshotStateList<Message>> get() = _messagesSent.asStateFlow()

    init {
        prepare()
        prepareBidDetails()
        //prepareMessages()
    }
    private fun prepare() {

        // get all products available for bidding
        CoroutineScope(Dispatchers.IO).launch {
            // we also sort them here
            BarterRepository.getAllProductOffering()?.collect() {
                _allProductsOffering.value = sortProductsOffering(it)
            }
        }

        // get list of products offered by the user
        CoroutineScope(Dispatchers.IO).launch {
            FirebaseClient.currentUserFirebase.collect() { userFirebase ->
                if (userFirebase != null) {
                    //Log.i("authLocal", "got id ${userFirebase.id}")
                    reloadCurrentUser()
                    reloadUserAndProductOffering()
                    reloadCurrentBids()
                    reloadUserAndAcceptBid()
                    reloadMessages()
                }
            }
        }
        observeProductChosenAndProcess()
        observeBidChosenAndProcess()
    } // end of prepare

    fun resetProduct() {
        _productChosen.value = null
        _productOfferingWithProductsAsking.value = null
        //_productOfferingWithBids.value = null
        _bids.value = listOf()
        //_allProductsOffering.value = listOf()
        //_userProductsOffering.value = listOf()
        _sellerProductImages.value = mutableStateListOf()
        _askingProductImages.value = mutableStateListOf()
        _bidChosen.value = null
        _bidProductImages.value = mutableStateListOf()
        //_allAcceptBids.value = listOf()
        //_acceptedBidsDetail.value = mutableStateListOf()
        //_bidsAcceptedDetail.value = mutableStateListOf()
    }

    fun updateProductChosen(product: ProductOffering?) {
        _productChosen.value = product
    }

    fun updateBidChosen(bid: Bid) {
        _bidChosen.value = bid
    }

    fun deleteProductAskingLocalDatabase(productAsking: ProductAsking) {
        BarterRepository.deleteProductsAsking(listOf(productAsking))
    }

    private fun sortProductsOffering(products: List<ProductOffering>) : List<ProductOffering> {
        //val result = products.sortedByDescending { parseDateTime(it.dateCreated) }
        //Log.i("sorting products", "no ${result.size}")
        //return result
        return products.sortedByDescending { parseDateTime(it.dateCreated) }
    }

    private fun sortBids(bids: List<Bid>) : List<Bid> {
        return bids.sortedByDescending { parseDateTime(it.bidTime) }
    }

    private fun sortAcceptBids(bids: List<AcceptBid>) : List<AcceptBid> {
        return bids.sortedByDescending { parseDateTime(it.acceptTime) }
    }

    private fun sortBidWithDetails(bids: List<BidWithDetails>) : List<BidWithDetails> {
        return bids.sortedByDescending { parseDateTime(it.bid.bidTime) }
    }

    private fun sortMessages(messages: List<Message>) : List<Message> {
        return messages.sortedByDescending { parseDateTime(it.date) }
    }

    fun updateChosenCurrentBid(bid: BidWithDetails) {
        _chosenCurrentBid.value = bid
    }

    fun reloadCurrentUser() {
        CoroutineScope(Dispatchers.IO).launch {
            BarterRepository.getCurrentUser(FirebaseClient.currentUserFirebase.value!!.id)
                ?.collect() { currentUserList ->
                    if (currentUserList.isNotEmpty()) {
                        Log.i(
                            "authLocal",
                            "got current user ${currentUserList[0].name}"
                        )
                        _currentUser.value = currentUserList[0]
                    } else {
                        Log.i("authLocal", "got empty list of user")
                    }
                }
        }
    }

    fun reloadUserAndProductOffering() {
        CoroutineScope(Dispatchers.IO).launch {
            val userAndProductOffering = CoroutineScope(Dispatchers.IO).async {
                BarterRepository.getUserProductsOffering(FirebaseClient.currentUserFirebase.value!!.id)
            }.await()
            if (!userAndProductOffering.isNullOrEmpty()) {
                // here we sort the products offering,
                _userProductsOffering.value =
                    sortProductsOffering(userAndProductOffering.get(0).productsOffering)
                Log.i(
                    "local database manager",
                    "got user's products ${userAndProductOffering.get(0).productsOffering.size}"
                )
            } else {
                Log.i("local database manager", "user's product is null or 0")
            }
        }
    }


    fun reloadCurrentBids() {
        CoroutineScope(Dispatchers.IO).launch {
            BarterRepository.getCurrentBidsById(FirebaseClient.currentUserFirebase.value!!.id)?.collect() {
                userAndCurrentBids ->
                if (userAndCurrentBids.isNotEmpty()) {
                    Log.i("reload current bids", "loaded current bids")
                    _currentBids.value = userAndCurrentBids[0].currentBids.toMutableStateList()
                    // retrieve product from local only, if it is not available, show not available
                    // the product is supposed to be in the product offering available list
                    // so it should be in the database
                    retrieveProductOfferingAndBids()
                }
            }
        }
    }

    fun reloadUserAndAcceptBid() {
        CoroutineScope(Dispatchers.IO).launch {
            Log.i("local database mgr", "preparing user and accept bid, got user id")
            val userAcceptBidsFlow = CoroutineScope(Dispatchers.IO).async {
                BarterRepository.getUserAndAcceptBids(FirebaseClient.currentUserFirebase.value!!.id)
            }.await()
            userAcceptBidsFlow?.collect() { userAcceptBids ->
                if (userAcceptBids.isNotEmpty()) {
                    Log.i(
                        "local database mgr",
                        "userAcceptedBids ${userAcceptBids.size}"
                    )
                    Log.i(
                        "local database mgr",
                        "got no accept bids: ${userAcceptBids[0].acceptBids.size}"
                    )
                    // we sort the accept bids here too
                    _allAcceptBids.value = sortAcceptBids(userAcceptBids[0].acceptBids)
                }
            }
        }
    }

    fun observeProductChosenAndProcess() {
        CoroutineScope(Dispatchers.IO).launch {
            productChosen.collect() { productOffering ->
                Log.i("local database mgr", "product was chosen detected")
                productOffering?.let {
                    reloadProductImages(it)
                    reloadProductAskingProduct(it)
                    reloadChosenProductBids(it)
                }
            }
        }
    }

    fun reloadProductImages(productOffering: ProductOffering) {
        _sellerProductImages.value = mutableStateListOf()
        // preloaded with placeholder images,
        // also, for mutable list to set the result at particular index
        for (i in 0..productOffering.images.size - 1) {
            _sellerProductImages.value.add(
                ProductImageToDisplay(
                    imageId = UUID.randomUUID().toString(),
                    image = ImageHandler.createPlaceholderImage(),
                    imageUrlCloud = "placeholder"
                )
            )

            CoroutineScope(Dispatchers.IO).launch {
                val (productImage, shouldSave) = CoroutineScope(Dispatchers.IO).async {
                    Log.i("loadingImagesLocal", "dealing with url ${productOffering.images[i]}")
                    loadOrRetrieveProductImage(productOffering.images[i])
                }.await()

                productImage?.let {
                    Log.i(
                        "database mgr",
                        "have asking product image setting to list"
                    )
                    _sellerProductImages.value.set(i, it)
                    if (shouldSave) {
                        BarterRepository.insertImages(listOf(it))
                    }
                }
            }
        }// end of first for loop
    }

    fun reloadProductAskingProduct(productOffering: ProductOffering) {
        CoroutineScope(Dispatchers.IO).launch {
            BarterRepository.getProductOfferingWithProductsAsking(
                productOffering.productId
            )
                ?.collect() { productOfferingWithProductAskingList ->

                    if (productOfferingWithProductAskingList.isNotEmpty()) {
                        _productOfferingWithProductsAsking.value =
                            productOfferingWithProductAskingList[0]

                        for (i in 0..productOfferingWithProductAskingList[0].askingProducts.size - 1) {
                            _askingProductImages.value.add(mutableListOf())
                            for (j in 0..productOfferingWithProductAskingList[0].askingProducts[i].images.size - 1) {
                                val (productImage, shouldSave) = CoroutineScope(
                                    Dispatchers.IO
                                ).async {
                                    loadOrRetrieveProductImage(
                                        productOfferingWithProductAskingList[0].askingProducts[i].images[j]
                                    )
                                }.await()

                                productImage?.let {
                                    Log.i(
                                        "database mgr",
                                        "have product image setting to list"
                                    )
                                    _askingProductImages.value[i].add(it)
                                    if (shouldSave) {
                                        BarterRepository.insertImages(listOf(it))
                                    }
                                }
                            }
                        }
                    }
                }
        }
    }

    fun reloadChosenProductBids(productOffering: ProductOffering) {
        // prepare bids within the product offering
        CoroutineScope(Dispatchers.IO).launch {
            BarterRepository.getProductOfferingWithBids(productOffering.productId)
                ?.collect() {
                    if (it.isNotEmpty()) {
                        // we sort the bids here
                        _bids.value = sortBids(it[0].bids)
                    }
                }
        }
    }

    private fun reloadMessages() {
        CoroutineScope(Dispatchers.IO).launch {
            //prepareMessages()
            BarterRepository.getUserAndMessageById(FirebaseClient.currentUserFirebase.value!!.id)?.collect() {
                    userAndMessageList ->
                if (userAndMessageList.isNotEmpty()) {
                    _allMessages.value = sortMessages(userAndMessageList[0].messages).toMutableStateList()
                    Log.i("reload messages", "got messages")
                } else {
                    Log.i("reload messages", "got no messages")
                }

            }
        }
    }

    private fun observeBidChosenAndProcess() {
        // this second big coroutine is to observe the bid chosen
        // whenever a bid is chosen, I load the corresponding bid product images in it
        // prepare chosen bid's images
        CoroutineScope(Dispatchers.IO).launch {
            bidChosen.collect() { bid ->
                bid?.let { theBid ->
                    for (i in 0..theBid.bidProduct.images.size - 1) {
                        _bidProductImages.value.add(
                            ProductImageToDisplay(
                                imageId = UUID.randomUUID().toString(),
                                image = ImageHandler.createPlaceholderImage(),
                                imageUrlCloud = ""
                            )
                        )

                        CoroutineScope(Dispatchers.IO).launch {
                            val (productImage, shouldSave) = CoroutineScope(Dispatchers.IO).async {
                                loadOrRetrieveProductImage(theBid.bidProduct.images[i])
                            }.await()

                            productImage?.let {
                                Log.i("database mgr", "have bid product image setting to list")
                                _bidProductImages.value.set(i, it)
                                if (shouldSave) {
                                    BarterRepository.insertImages(listOf(it))
                                }
                            }
                        }
                    } // end of for loop
                } // end of if bid exist
            }
        } // end of bid coroutine
    }

    // as I get each new bid details, I store them in a temp list
    // after I collect all bid details, I sort them
    // it is then I update the currentBidsDetails
    private suspend fun retrieveProductOfferingAndBids() {
        // for each of the current bids, retrieve the product from database
        // create a bid detail to hold the bid and the product
        val tempBidDetails = mutableListOf<BidWithDetails>()
        val deferredList = mutableListOf<Deferred<Unit?>>()
        CoroutineScope(Dispatchers.IO).launch {
            for (bid in currentBids.value) {
                Log.i("retrieve", "processing 1 bid")
                deferredList.add(CoroutineScope(Dispatchers.IO).async {
                    var product: ProductOffering?
                    var bids: List<Bid>
                    var gotResult = false
                    BarterRepository.getProductOfferingWithBids(bid.bidProductId)
                        ?.take(1)?.collect() { productWithBidsList ->
                            if (productWithBidsList.isNotEmpty()) {
                                product = productWithBidsList[0].productOffering
                                bids = sortBids(productWithBidsList[0].bids)

                                val bidDetails = BidWithDetails(
                                    acceptBid = null, product = product!!,
                                    bid = bid, currentBids = bids
                                )
                                Log.i("retrieve and create bid", "added a bid")
                                tempBidDetails.add(bidDetails)
                                //_currentBidsDetails.value.add(bidDetails)
                                //addAndSortCurrentBidsDetails(bidDetails)
                                gotResult = true
                            }

                        }
                })
            }
            //Log.i("retrieve ", "finished for loop")
            //Log.i("retrieve and create bid", "started all cor, waiting for result")
            deferredList.awaitAll()
            Log.i("retrieve", "got all bid result, assign to current bids")
            // after the coroutine finish, we sort and update
            //Log.i("retrieve", "no of temp bid ${tempBidDetails.size}")
            _currentBidsDetails.value = sortBidWithDetails(tempBidDetails).toMutableStateList()
        }
    }

    // we try to retrieve the image from local database
    // and get the imageUrlLocal as uri
    // retrieve the image from uri
    // put it in the image field of the product image object
    // then replace the placeholder with it
    // coroutine scope here , so we can wait for if the image comes back
    private suspend fun loadOrRetrieveProductImage(imageConcerned: String) : Pair<ProductImageToDisplay?, Boolean> {
        Log.i("loadOrRetrieve", "called")
        var productImage: ProductImageToDisplay? = null
        var imageLoaded: Bitmap? = null
        var shouldSaveImage = false
        val imageList = CoroutineScope(Dispatchers.IO).async {
            BarterRepository.getImage(imageConcerned)
        }.await()
        // load it from cloud
        // and save it locally
        if (imageList.isNullOrEmpty()) {
            Log.i("database mgr", "can't retrieve image from local database")
            imageLoaded = CoroutineScope(Dispatchers.IO).async {
                ImageHandler.loadImageFromCloud(imageConcerned)
            }.await()

            imageLoaded?.let { image ->
                Log.i("database mgr", "loaded image from cloud")
                val url = CoroutineScope(Dispatchers.IO).async {
                    ImageHandler.saveImageExternalStorage(
                        imageConcerned,
                        image
                    )
                }.await()

                url?.let { urlBack ->
                    Log.i("database mgr", "saved image to local")
                    productImage = ProductImageToDisplay(
                        image = image,
                        imageId = UUID.randomUUID().toString(),
                        imageUrlCloud = imageConcerned,
                        imageUrlLocal = urlBack.toString()
                    )
                    shouldSaveImage = true
                }
            }
        } else {
            Log.i("loadOrSaveImage", "got product image object from local")
            productImage = imageList[0]
            val localUrl = imageList[0].imageUrlLocal
            localUrl?.let {
                Log.i("loadOrSaveImage", "loading image from local")
                productImage!!.image = CoroutineScope(Dispatchers.IO).async {
                    ImageHandler.loadImageFromLocal(it)
                }.await()
                //Log.i("loadOrSaveImage", "product image got from local ${productImage!!.image?.width.toString()}")
            }
        }
        return Pair(productImage, shouldSaveImage)
    }

    // retrieve the bid's details from AcceptBidAndProduct, and AcceptBidAndBid
    private fun prepareBidDetails() {
        CoroutineScope(Dispatchers.IO).launch {
            allAcceptBids.collect() { theBids ->
                _acceptedBidsDetail.value = mutableStateListOf()
                _bidsAcceptedDetail.value = mutableStateListOf()
                for (theBid in theBids) {
                    Log.i("local database mgr", "processing the bid ${theBid.acceptId}")
                    CoroutineScope(Dispatchers.IO).launch {
                        val bidProductDeferred = CoroutineScope(Dispatchers.IO).async {
                            BarterRepository.getAcceptBidAndProductById(theBid.acceptId)
                        }

                        val bidBidDeferred = CoroutineScope(Dispatchers.IO).async {
                            BarterRepository.getAcceptBidAndBidById(theBid.acceptId)
                        }
                        val product = bidProductDeferred.await()?.get(0)?.product
                        Log.i("local database, prepare bid details", "got product ${product?.name}")
                        //Log.i("Local database mgr", "product ${product?.name}")
                        val bid = bidBidDeferred.await()?.get(0)?.bid
                        Log.i("Local database mgr", "bid ${bid?.bidId}")
                        if (product != null && bid != null) {
                            val bidDetails = BidWithDetails(
                                acceptBid = theBid,
                                product = product,
                                bid = bid
                            )
                            if (theBid.isSeller) {
                                _acceptedBidsDetail.value.add(bidDetails)
                                Log.i("Local database mgr", "update accepted bids")
                            } else {
                                _bidsAcceptedDetail.value.add(bidDetails)
                                Log.i("Local database mgr", "update bids accepted")
                            }
                        }
                    }
                }
            }
        }
    }

    private fun prepareMessages() {
        CoroutineScope(Dispatchers.IO).launch {
            allMessages.collect() { messages ->
                if (messages.isNotEmpty()) {
                    _messagesReceived.value = messages.filter { !it.sender }.toMutableStateList()
                    _messagesSent.value = messages.filter { it.sender }.toMutableStateList()
                }
            }
        }
    }

}
