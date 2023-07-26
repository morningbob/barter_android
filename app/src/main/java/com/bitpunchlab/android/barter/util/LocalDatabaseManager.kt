package com.bitpunchlab.android.barter.util

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.bitpunchlab.android.barter.BarterNavigation
import com.bitpunchlab.android.barter.database.BarterRepository
import com.bitpunchlab.android.barter.firebase.FirebaseClient
import com.bitpunchlab.android.barter.models.Bid
import com.bitpunchlab.android.barter.models.ProductAsking
import com.bitpunchlab.android.barter.models.ProductImageToDisplay
import com.bitpunchlab.android.barter.models.ProductOffering
import com.bitpunchlab.android.barter.models.ProductOfferingAndBids
import com.bitpunchlab.android.barter.models.ProductOfferingAndProductsAsking
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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

    private var _allProductsOffering = MutableStateFlow<List<ProductOffering>>(listOf())
    val allProductsOffering : StateFlow<List<ProductOffering>> get() = _allProductsOffering.asStateFlow()

    private var _userProductsOffering = MutableStateFlow<List<ProductOffering>>(listOf())
    val userProductsOffering : StateFlow<List<ProductOffering>> get() = _userProductsOffering.asStateFlow()

    private val _productChosen = MutableStateFlow<ProductOffering?>(null)
    val productChosen : StateFlow<ProductOffering?> get() = _productChosen.asStateFlow()

    private var _sellerProductImages = MutableStateFlow<SnapshotStateList<ProductImageToDisplay>>(mutableStateListOf())
    val sellerProductImages : StateFlow<SnapshotStateList<ProductImageToDisplay>> get() = _sellerProductImages.asStateFlow()

    private var _askingProductImages = MutableStateFlow<MutableList<MutableList<ProductImageToDisplay>>>(mutableListOf())
    val askingProductImages : StateFlow<MutableList<MutableList<ProductImageToDisplay>>> get() = _askingProductImages.asStateFlow()

    private val _productOfferingWithProductsAsking = MutableStateFlow<ProductOfferingAndProductsAsking?>(null)
    val productOfferingWithProductsAsking : StateFlow<ProductOfferingAndProductsAsking?>
        get() = _productOfferingWithProductsAsking.asStateFlow()

    private val _productOfferingWithBids = MutableStateFlow<ProductOfferingAndBids?>(null)
    val productOfferingWithBids : StateFlow<ProductOfferingAndBids?>
        get() = _productOfferingWithBids.asStateFlow()

    private val _bidChosen = MutableStateFlow<Bid?>(null)
    val bidChosen : StateFlow<Bid?> get() = _bidChosen.asStateFlow()

    private var _bidProductImages = MutableStateFlow<MutableList<ProductImageToDisplay>>(mutableListOf())
    val bidProductImages : StateFlow<MutableList<ProductImageToDisplay>> get() = _bidProductImages.asStateFlow()

    init {
        prepare()
    }
    private fun prepare() {
        // get all products available for bidding
        CoroutineScope(Dispatchers.IO).launch {
            CoroutineScope(Dispatchers.IO).launch {
                BarterRepository.getAllProductOffering()?.collect() {
                    _allProductsOffering.value = it
                }
            }
            // get list of products offered by the user
            CoroutineScope(Dispatchers.IO).launch {
                FirebaseClient.userId.collect() { id ->
                    if (id != "") {
                        _userProductsOffering.value =
                            BarterRepository.getUserProductsOffering(id)?.get(0)?.productsOffering
                                ?: listOf()
                    }

                }
            }
        }
        // get the bitmap of the images associated with the product
        // we first try to retrieve the product image locally by the imageUrlCloud
        // if we got null, we send a request to Cloud Storage
        CoroutineScope(Dispatchers.IO).launch {
            productChosen.collect() { productOffering ->
                productOffering?.let {
                    _sellerProductImages.value = mutableStateListOf()
                    // preloaded with placeholder images,
                    // also, for mutable list to set the result at particular index
                    for (i in 0..productOffering.images.size - 1) {
                        _sellerProductImages.value.add(
                            ProductImageToDisplay(
                                UUID.randomUUID().toString(),
                                ImageHandler.createPlaceholderImage(),
                                // the placeholder phrase indicates that it is a placeholder
                                // later if the image comes back is null, we'll change it to "download"
                                "placeholder"
                            )
                        )
                        // we try to retrieve the image from local database
                        // and get the imageUrlLocal as uri
                        // retrieve the image from uri
                        // put it in the image field of the product image object
                        // then replace the placeholder with it
                        // coroutine scope here , so we can wait for if the image comes back
                        CoroutineScope(Dispatchers.IO).launch {
                            var productImage : ProductImageToDisplay? = null
                            var imageLoaded : Bitmap? = null
                            val imageList = BarterRepository.getImage(productOffering.images[i])
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
                                            imageLoaded
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
                    //CoroutineScope(Dispatchers.IO).launch {
                    //    ImageHandler.loadedImagesFlow(productOffering.images).collect() { pairResult ->
                    //        _sellerProductImages.value.set(pairResult.first, pairResult.second)
                    //    }
                    //}

                    // prepare bids and asking products associated with the product offering
                    CoroutineScope(Dispatchers.IO).launch {
                        BarterRepository.getProductOfferingWithProductsAsking(productOffering.productId)
                            ?.collect() {
                                //ProductInfo.updateProductOfferingWithProductsAsking(it[0])
                                _productOfferingWithProductsAsking.value = it[0]
                                // prepare asking product's images
                                for (i in 0..it[0].askingProducts.size - 1) {
                                    _askingProductImages.value.add(mutableListOf())
                                    for (j in 0..it[0].askingProducts[i].images.size - 1) {
                                        _askingProductImages.value[i].add(
                                            ProductImageToDisplay(
                                                UUID.randomUUID().toString(),
                                                ImageHandler.createPlaceholderImage(),
                                                ""
                                            )
                                        )
                                    }
                                }
                                CoroutineScope(Dispatchers.IO).launch {
                                    for (i in 0..it[0].askingProducts.size - 1) {
                                        ImageHandler.loadedImagesFlow(it[0].askingProducts[i].images).collect() { pairResult ->
                                            _askingProductImages.value[i].set(pairResult.first, pairResult.second)
                                        }
                                    }
                                }
                            }
                    }
                    CoroutineScope(Dispatchers.IO).launch {
                        BarterRepository.getProductOfferingWithBids(productOffering.productId)
                            ?.collect() {
                                //Log.i("product details vm", "getting bids ${it[0].bids.size}")
                                _productOfferingWithBids.value = it[0]
                            }
                    }
                }
            }
        }
        // prepare chosen bid's images
        CoroutineScope(Dispatchers.IO).launch {
            bidChosen.collect() {
                it?.let {
                    for (i in 0..it.bidProduct.images.size - 1) {
                        _bidProductImages.value.add(
                            ProductImageToDisplay(
                                UUID.randomUUID().toString(),
                                ImageHandler.createPlaceholderImage(),
                                ""
                            )
                        )
                    }

                    CoroutineScope(Dispatchers.IO).launch {
                            ImageHandler.loadedImagesFlow(it.bidProduct.images)
                                .collect() { pairResult ->
                                    _bidProductImages.value.set(
                                        pairResult.first,
                                        pairResult.second
                                    )
                                }
                        }
                    }
                }
            }
        }


    fun resetProduct() {
        _productChosen.value = null
        _productOfferingWithProductsAsking.value = null
        _productOfferingWithBids.value = null
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



}