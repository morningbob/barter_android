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
import com.bitpunchlab.android.barter.models.ProductAsking
import com.bitpunchlab.android.barter.models.ProductImageToDisplay
import com.bitpunchlab.android.barter.models.ProductOffering
import com.bitpunchlab.android.barter.models.ProductOfferingAndBids
import com.bitpunchlab.android.barter.models.ProductOfferingAndProductsAsking
import com.bitpunchlab.android.barter.models.User
import com.bitpunchlab.android.barter.util.ImageHandler
import com.bitpunchlab.android.barter.util.parseDateTime
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
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

    //private val _productOfferingWithBids = MutableStateFlow<ProductOfferingAndBids?>(null)
    //val productOfferingWithBids : StateFlow<ProductOfferingAndBids?>
    //    get() = _productOfferingWithBids.asStateFlow()

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

    //private val _chosenCurrentBids = MutableStateFlow<SnapshotStateList<Bid>>(mutableStateListOf())
    //val currentBids : StateFlow<SnapshotStateList<Bid>> get() = _currentBids.asStateFlow()

    init {
        prepare()
        prepareBidDetails()
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
                //FirebaseClient.userId.collect() { id ->
                FirebaseClient.currentUserFirebase.collect() { userFirebase ->
                    if (userFirebase != null) {
                        //Log.i("authLocal", "got id ${userFirebase.id}")
                        CoroutineScope(Dispatchers.IO).launch {
                            BarterRepository.getCurrentUser(userFirebase.id)
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
                        reloadUserAndProductOffering()
                        reloadCurrentBids()

                        CoroutineScope(Dispatchers.IO).launch {
                            Log.i("local database mgr", "preparing user and accept bid, got user id")
                            val userAcceptBidsFlow = CoroutineScope(Dispatchers.IO).async {
                                BarterRepository.getUserAndAcceptBids(userFirebase.id)
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
                }
            }
        // get the bitmap of the images associated with the product
        // we first try to retrieve the product image locally by the imageUrlCloud
        // if we got null, we send a request to Cloud Storage

                // this coroutine is used to observe the product offering chosen
                // when I got the product offering, I load the product's images
                // and the asking product images
                // I also load the bids associated with the product
        CoroutineScope(Dispatchers.IO).launch {
            productChosen.collect() { productOffering ->
                Log.i("local database mgr", "product was chosen detected")
                productOffering?.let {
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

                    // prepare bids and asking products associated with the product offering
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
                } // end of if product exist
            }

        } // end of the product chosen collect() coroutine

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

    private fun addAndSortCurrentBidsDetails(bidDetail: BidWithDetails) {
        if (currentBidsDetails.value.firstOrNull { it.bid.bidId == bidDetail.bid.bidId } == null) {
            _currentBidsDetails.value.add(bidDetail)
            _currentBidsDetails.value = sortBidWithDetails(currentBidsDetails.value.toList()).toMutableStateList()
        }
    }

    fun updateChosenCurrentBid(bid: BidWithDetails) {
        _chosenCurrentBid.value = bid
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
                    _currentBids.value = userAndCurrentBids[0].currentBids.toMutableStateList()
                    // retrieve product from local only, if it is not available, show not available
                    // the product is supposed to be in the product offering available list
                    // so it should be in the database
                    retrieveProductOfferingAndBids()
                }
            }
        }
    }

    private fun retrieveProductOfferingAndBids() {
        // for each of the current bids, retrieve the product from database
        // create a bid detail to hold the bid and the product
        for (bid in currentBids.value) {
            _currentBidsDetails.value = mutableStateListOf()
            CoroutineScope(Dispatchers.IO).launch {
                var product : ProductOffering?
                var bids : List<Bid>
                BarterRepository.getProductOfferingWithBids(bid.bidProductId)?.collect() { productWithBidsList ->
                    if (productWithBidsList.isNotEmpty()) {
                        product = productWithBidsList[0].productOffering
                        bids = sortBids(productWithBidsList[0].bids)

                        val bidDetails = BidWithDetails(acceptBid = null, product = product!!,
                            bid = bid, currentBids = bids)
                        //_currentBidsDetails.value.add(bidDetails)
                        addAndSortCurrentBidsDetails(bidDetails)
                    }
                }
            }
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
                                //_bidsDetail.value.add(bidDetails)
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

}
/*
        CoroutineScope(Dispatchers.IO).launch {
            val imageList = CoroutineScope(Dispatchers.IO).async {
                BarterRepository.getImage("https://firebasestorage.googleapis.com/v0/b/barter-a84a2.appspot.com/o/images%2F1aa2c01c-9f90-494b-9f9f-24f7985c5cce_0.jpg?alt=media&token=5be717b0-b9cd-4647-b30d-4e6148764ee0")
            }.await()
            if (imageList.isNullOrEmpty()) {
                Log.i("loadOrSaveImage", "couldn't retrieve the first image")
            } else {
                Log.i("loadOrSaveImage", "got first image")
            }
        }

         */
/*
//CoroutineScope(Dispatchers.IO).launch {
        // this outer coroutine will wait for the below coroutine to finish
            //val imagesToBeSaved = mutableListOf<ProductImageToDisplay>()
            // this outer coroutine will wait for the three parts to finish before
            // the outmost coroutine saves the new product images to local database
            // I use this one big coroutine to enclose the other 3 small coroutine is
            // because I need the 3 small coroutines runs at the same time
            // i can't just use .join() in the 3 small coroutines
            // because then, they won't run at the same time
CoroutineScope(Dispatchers.IO).launch {
                            var productImage: ProductImageToDisplay? = null
                            var imageLoaded: Bitmap? = null

                            val imageList = CoroutineScope(Dispatchers.IO).async {
                                BarterRepository.getImage(productOffering.images[i])
                            }.await()
                            // load it from cloud
                            // and save it locally
                            if (imageList.isNullOrEmpty()) {
                                Log.i("database mgr", "can't retrieve image from local database")
                                //_sellerProductImages.value[i] = imageList[0]
                                imageLoaded = CoroutineScope(Dispatchers.IO).async {
                                    ImageHandler.loadImageFromCloud(productOffering.images[i])
                                }.await()

                                imageLoaded?.let { image ->
                                    Log.i("database mgr", "loaded image from cloud")
                                    val url = CoroutineScope(Dispatchers.IO).async {
                                        ImageHandler.saveImageExternalStorage(
                                            productOffering.images[i],
                                            image
                                        )
                                    }.await()
                                    url?.let {
                                        Log.i("database mgr", "saved image to local")
                                        productImage = ProductImageToDisplay(
                                            image = image,
                                            imageId = UUID.randomUUID().toString(),
                                            imageUrlCloud = productOffering.images[i],
                                            imageUrlLocal = it.toString()
                                        )
                                        //BarterRepository.insertImages(images = listOf(productImage!!))
                                        imagesToBeSaved.add(productImage!!)
                                    }
                                }

                            } else {
                                Log.i("database mgr", "got product image object from local")
                                productImage = imageList[0]
                                val localUrl = imageList[0].imageUrlLocal
                                localUrl?.let {
                                    Log.i("database mgr", "loading image from local")
                                    productImage!!.image = ImageHandler.loadImageFromLocal(it)
                                }

                                //productImage = productImageLoaded.copy()
                            }
                            productImage?.let {
                                Log.i("database mgr", "have product image setting to list")
                                _sellerProductImages.value.set(i, it)
                            }
                        }
                    }
                    CoroutineScope(Dispatchers.IO).launch {
                            BarterRepository.getProductOfferingWithProductsAsking(productOffering.productId)
                                ?.collect() { productOfferingWithProductAskingList ->
                                    //ProductInfo.updateProductOfferingWithProductsAsking(it[0])
                                    //val imagesToBeSaved = mutableListOf<ProductImageToDisplay>()
                                    _productOfferingWithProductsAsking.value =
                                        productOfferingWithProductAskingList[0]
                                    // prepare asking product's images
                                    for (i in 0..productOfferingWithProductAskingList[0].askingProducts.size - 1) {
                                        _askingProductImages.value.add(mutableListOf())
                                        for (j in 0..productOfferingWithProductAskingList[0].askingProducts[i].images.size - 1) {
                                            _askingProductImages.value[i].add(
                                                ProductImageToDisplay(
                                                    UUID.randomUUID().toString(),
                                                    ImageHandler.createPlaceholderImage(),
                                                    ""
                                                )
                                            )

                                            CoroutineScope(Dispatchers.IO).launch {
                                                var productImage: ProductImageToDisplay? = null
                                                var imageLoaded: Bitmap? = null
                                                val imageList =
                                                    CoroutineScope(Dispatchers.IO).async {
                                                        BarterRepository.getImage(
                                                            productOfferingWithProductAskingList[0].askingProducts[i].images[j]
                                                        )
                                                    }.await()
                                                // load it from cloud
                                                // and save it locally
                                                if (imageList.isNullOrEmpty()) {
                                                    Log.i(
                                                        "database mgr",
                                                        "can't retrieve image from local database"
                                                    )
                                                    //_sellerProductImages.value[i] = imageList[0]
                                                    imageLoaded =
                                                        CoroutineScope(Dispatchers.IO).async {
                                                            ImageHandler.loadImageFromCloud(
                                                                productOfferingWithProductAskingList[0].askingProducts[i].images[j]
                                                            )
                                                        }.await()

                                                    imageLoaded?.let { image ->
                                                        Log.i(
                                                            "database mgr",
                                                            "loaded image from cloud"
                                                        )
                                                        val url =
                                                            CoroutineScope(Dispatchers.IO).async {
                                                                ImageHandler.saveImageExternalStorage(
                                                                    productOfferingWithProductAskingList[0].askingProducts[i].images[j],
                                                                    image
                                                                )
                                                            }.await()
                                                        url?.let {
                                                            Log.i(
                                                                "database mgr",
                                                                "saved image to local"
                                                            )
                                                            productImage = ProductImageToDisplay(
                                                                image = image,
                                                                imageId = UUID.randomUUID()
                                                                    .toString(),
                                                                imageUrlCloud = productOfferingWithProductAskingList[0].askingProducts[i].images[j],
                                                                imageUrlLocal = it.toString()
                                                            )
                                                            imagesToBeSaved.add(productImage!!)
                                                        }
                                                    }
                                                } else {
                                                    Log.i(
                                                        "database mgr",
                                                        "got product image object from local"
                                                    )
                                                    productImage = imageList[0]
                                                    val localUrl = imageList[0].imageUrlLocal
                                                    localUrl?.let {
                                                        Log.i(
                                                            "database mgr",
                                                            "loading image from local"
                                                        )
                                                        productImage!!.image =
                                                            ImageHandler.loadImageFromLocal(it)
                                                    }
                                                }
                                                productImage?.let {
                                                    Log.i(
                                                        "database mgr",
                                                        "have product image setting to list"
                                                    )
                                                    _askingProductImages.value[i].set(j, it)
                                                }
                                            }
                                        }


                                        //CoroutineScope(Dispatchers.IO).launch {
                                        //    for (i in 0..it[0].askingProducts.size - 1) {
                                        //        ImageHandler.loadedImagesFlow(it[0].askingProducts[i].images).collect() { pairResult ->
                                        //            _askingProductImages.value[i].set(pairResult.first, pairResult.second)
                                        //        }
                                        //    }
                                        //}
                                    }
                                }
                        } // end of coroutine scope

                        CoroutineScope(Dispatchers.IO).launch {
                                    var productImage: ProductImageToDisplay? = null
                                    var imageLoaded: Bitmap? = null
                                    val imageList = CoroutineScope(Dispatchers.IO).async {
                                        BarterRepository.getImage(it.bidProduct.images[i])
                                    }.await()
                                    // load it from cloud
                                    // and save it locally
                                    if (imageList.isNullOrEmpty()) {
                                        Log.i(
                                            "database mgr",
                                            "can't retrieve image from local database"
                                        )
                                        //_sellerProductImages.value[i] = imageList[0]
                                        imageLoaded = CoroutineScope(Dispatchers.IO).async {
                                            ImageHandler.loadImageFromCloud(it.bidProduct.images[i])
                                        }.await()

                                        imageLoaded?.let { image ->
                                            Log.i("database mgr", "loaded image from cloud")
                                            val url = CoroutineScope(Dispatchers.IO).async {
                                                ImageHandler.saveImageExternalStorage(
                                                    it.bidProduct.images[i],
                                                    image
                                                )
                                            }.await()

                                            url?.let { urlBack ->
                                                Log.i("database mgr", "saved image to local")
                                                productImage = ProductImageToDisplay(
                                                    image = image,
                                                    imageId = UUID.randomUUID().toString(),
                                                    imageUrlCloud = it.bidProduct.images[i],
                                                    imageUrlLocal = urlBack.toString()
                                                )
                                            }
                                        }
                                    } else {
                                        Log.i("database mgr", "got product image object from local")
                                        productImage = imageList[0]
                                        val localUrl = imageList[0].imageUrlLocal
                                        localUrl?.let {
                                            Log.i("database mgr", "loading image from local")
                                            productImage!!.image =
                                                ImageHandler.loadImageFromLocal(it)
                                            imagesToBeSaved.add(productImage!!)
                                        }
                                    }
                                    productImage?.let {
                                        Log.i("database mgr", "have product image setting to list")
                                        _bidProductImages.value.set(i, it)
                                    }

                                }.join()
 */
//CoroutineScope(Dispatchers.IO).launch {
//    ImageHandler.loadedImagesFlow(productOffering.images).collect() { pairResult ->
//        _sellerProductImages.value.set(pairResult.first, pairResult.second)
//    }
//}