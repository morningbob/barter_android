package com.bitpunchlab.android.barter.productOfferingDetails

import android.util.Log
import androidx.lifecycle.ViewModel
import com.bitpunchlab.android.barter.util.ImageHandler
import com.bitpunchlab.android.barter.database.BarterRepository
import com.bitpunchlab.android.barter.firebase.FirebaseClient
import com.bitpunchlab.android.barter.models.Bid
import com.bitpunchlab.android.barter.models.ProductOffering
import com.bitpunchlab.android.barter.productsOfferingList.ProductInfo
import com.bitpunchlab.android.barter.util.LocalDatabaseManager
import com.bitpunchlab.android.barter.util.ProductImage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

// here we prepare the bids list by querying firestore, the corresponding product bidding
// we prepare it even before users click on view bids
class ProductOfferingDetailsViewModel() : ViewModel() {

    private val _product = MutableStateFlow<ProductOffering?>(null)
    val product : StateFlow<ProductOffering?> get() = _product.asStateFlow()

    private val _shouldDisplayImages = MutableStateFlow<Boolean>(false)
    val shouldDisplayImages : StateFlow<Boolean> get() = _shouldDisplayImages.asStateFlow()

    private val _shouldPopImages = MutableStateFlow<Boolean>(false)
    val shouldPopImages : StateFlow<Boolean> get() = _shouldPopImages.asStateFlow()

    private val _shouldDisplayAskingProducts = MutableStateFlow<Boolean>(false)
    val shouldDisplayAskingProducts : StateFlow<Boolean> get() = _shouldDisplayAskingProducts.asStateFlow()

    private val _shouldShowBidsListStatus = MutableStateFlow<Int>(0)
    val shouldShowBidsListStatus : StateFlow<Int> get() = _shouldShowBidsListStatus.asStateFlow()

    private val _shouldBid = MutableStateFlow<Boolean>(false)
    val shouldBid : StateFlow<Boolean> get() = _shouldBid.asStateFlow()

    // 1 is failed, 2 is succeeded, 3 is invalid info
    private val _biddingStatus = MutableStateFlow<Int>(0)
    val biddingStatus : StateFlow<Int> get() = _biddingStatus.asStateFlow()

    // when user clicks view product images, or view asking product images
    // we retrieve the images and put it here
    // so the images display screen can retrieve it here
    // that way, we can display both product images and asking product images
    // both in one images display screen
    private val _imagesDisplay = MutableStateFlow<MutableList<ProductImage>>(mutableListOf())
    val imagesDisplay : StateFlow<MutableList<ProductImage>> get() = _imagesDisplay.asStateFlow()

    private val _productImages = MutableStateFlow<MutableList<ProductImage>>(mutableListOf())
    val productImages : StateFlow<MutableList<ProductImage>> get() = _productImages.asStateFlow()

    private val _askingImages = MutableStateFlow<MutableList<ProductImage>>(mutableListOf())
    val askingImages : StateFlow<MutableList<ProductImage>> get() = _askingImages.asStateFlow()

    private val _loadingAlpha = MutableStateFlow<Float>(0f)
    val loadingAlpha : StateFlow<Float> get() = _loadingAlpha.asStateFlow()

    private val _shouldPopDetails = MutableStateFlow<Boolean>(false)
    val shouldPopDetails : StateFlow<Boolean> get() = _shouldPopDetails.asStateFlow()

    private val _deleteProductStatus = MutableStateFlow<Int>(0)
    val deleteProductStatus : StateFlow<Int> get() = _deleteProductStatus.asStateFlow()

    private val _deleteImageStatus = MutableStateFlow(0)
    val deleteImageStatus : StateFlow<Int> get() = _deleteImageStatus.asStateFlow()

    init {
        // this is to prepare the images to be shown in image display screen
        CoroutineScope(Dispatchers.IO).launch {
            LocalDatabaseManager.sellerProductImages.collect() {
                _imagesDisplay.value = it
            }
        }
        CoroutineScope(Dispatchers.IO).launch {
            //LocalDatabaseManager.
            LocalDatabaseManager.productOfferingWithProductsAsking.collect() {
                it?.let {
                    ProductInfo.updateAskingProducts(it.askingProducts)
                }
            }
        }
        CoroutineScope(Dispatchers.IO).launch {
            LocalDatabaseManager.askingProductImages.collect() {
                if (it.isNotEmpty()) {
                    ProductInfo.updateAskingImages(it)
                }
            }
        }

        CoroutineScope(Dispatchers.IO).launch {
            LocalDatabaseManager.productOfferingWithBids.collect() {
                //ProductInfo
            }
        }
    }

    fun updateBiddingStatus(status: Int) {
        _biddingStatus.value = status
    }

    fun updateShouldDisplayImages(should: Boolean) {
        _shouldDisplayImages.value = should
    }

    fun updateShouldPopImages(should: Boolean) {
        _shouldPopImages.value = should
    }

    fun updateImages(images: MutableList<ProductImage>) {
        _imagesDisplay.value = images
    }

    fun updateShouldDisplayAskingProducts(should: Boolean) {
        _shouldDisplayAskingProducts.value = should
    }

    // before showing the bids list, we need to retrieve the product bidding from firestore
    fun updateShouldShowBidsListStatus(status: Int) {
        _shouldShowBidsListStatus.value = status
    }

    fun updateShouldPopDetails(should: Boolean) {
        _shouldPopDetails.value = should
    }

    fun updateShouldBid(should: Boolean) {
        _shouldBid.value = should
    }

    fun updateDeleteProductStatus(status: Int) {
        _deleteProductStatus.value = status
    }

    fun confirmDelete() {
        _deleteProductStatus.value = 1
    }

    fun deleteProduct(product: ProductOffering) {
        CoroutineScope(Dispatchers.IO).launch {
            if (FirebaseClient.processDeleteProduct(product)) {
                _deleteProductStatus.value = 2
            } else {
                _deleteProductStatus.value = 3
            }
        }
    }


    fun deleteImage(image: ProductImage) {
        Log.i("askingVM", "got image")
        //val newList = imagesDisplay.value.toMutableList()
        //newList.remove(image)
        //_imagesDisplay.value = newList
    }

    fun updateDeleteImageStatus(status: Int) {
        _deleteImageStatus.value = status
    }

    // show images as soon as it is loaded
    fun prepareAskingProducts() {
        ProductInfo.productChosen.value?.let {
            Log.i("product details vM", "product is not null")
            ProductInfo.productOfferingWithProductsAsking.value?.let {
                ProductInfo.updateAskingProducts(it.askingProducts)
            }
        }
    }

    fun processBidding(product: ProductOffering, bid: Bid, images: List<ProductImage>)  {
        _loadingAlpha.value = 100f

        val imagesBitmap = images.map { image ->
            image.image
        }

        //Log.i("bid vm", "processing bid")

        CoroutineScope(Dispatchers.IO).launch {
            Log.i("bidVM", "process bidding")
            if (FirebaseClient.processBidding(product, bid, imagesBitmap)) {
                _biddingStatus.value = 2
                _loadingAlpha.value = 0f
            } else {
                _biddingStatus.value = 1
                _loadingAlpha.value = 0f
            }
        }
    }
}
/*
        CoroutineScope(Dispatchers.IO).launch {
            ProductInfo.productChosen.collect() { productOffering ->
                productOffering?.let {
                    // preloaded with placeholder images,
                    // also, for mutable list to set the result at particular index
                    for (i in 0..productOffering.images.size - 1) {
                        _imagesDisplay.value.add(
                            ProductImage(
                                UUID.randomUUID().toString(),
                                ImageHandler.createPlaceholderImage()
                            )
                        )
                    }

                    CoroutineScope(Dispatchers.IO).launch {
                        //CoroutineScope(Dispatchers.IO).launch {
                        ImageHandler.loadedImagesFlow(productOffering.images).collect() { pairResult ->
                            _imagesDisplay.value.set(pairResult.first, pairResult.second)
                        }
                    }

                    // prepare bids and asking products associated with the product offering
                    CoroutineScope(Dispatchers.IO).launch {
                        BarterRepository.getProductOfferingWithProductsAsking(productOffering.productId)
                            ?.collect() {
                                ProductInfo.updateProductOfferingWithProductsAsking(it[0])
                            }
                    }
                    CoroutineScope(Dispatchers.IO).launch {
                        BarterRepository.getProductOfferingWithBids(productOffering.productId)
                            ?.collect() {
                                Log.i("product details vm", "getting bids ${it[0].bids.size}")
                                ProductInfo.updateProductOfferingWithBids(it[0])
                            }
                    }
                }
            }
        }

         */
