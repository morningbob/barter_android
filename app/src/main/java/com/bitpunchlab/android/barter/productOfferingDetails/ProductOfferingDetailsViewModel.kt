package com.bitpunchlab.android.barter.productOfferingDetails

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import com.bitpunchlab.android.barter.firebase.FirebaseClient
import com.bitpunchlab.android.barter.models.Bid
import com.bitpunchlab.android.barter.models.ProductImageToDisplay
import com.bitpunchlab.android.barter.models.ProductOffering
import com.bitpunchlab.android.barter.productsOfferingList.ProductInfo
import com.bitpunchlab.android.barter.util.BiddingStatus
import com.bitpunchlab.android.barter.util.DeleteProductStatus
import com.bitpunchlab.android.barter.database.LocalDatabaseManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// here we prepare the bids list by querying firestore, the corresponding product bidding
// we prepare it even before users click on view bids
class ProductOfferingDetailsViewModel() : ViewModel() {

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
    private val _biddingStatus = MutableStateFlow<BiddingStatus>(BiddingStatus.NORMAL)
    val biddingStatus : StateFlow<BiddingStatus> get() = _biddingStatus.asStateFlow()

    // when user clicks view product images, or view asking product images
    // we retrieve the images and put it here
    // so the images display screen can retrieve it here
    // that way, we can display both product images and asking product images
    // both in one images display screen
    private val _imagesDisplay = MutableStateFlow<SnapshotStateList<ProductImageToDisplay>>(mutableStateListOf())
    val imagesDisplay : StateFlow<SnapshotStateList<ProductImageToDisplay>> get() = _imagesDisplay.asStateFlow()

    private val _loadingAlpha = MutableStateFlow<Float>(0f)
    val loadingAlpha : StateFlow<Float> get() = _loadingAlpha.asStateFlow()

    private val _shouldPopDetails = MutableStateFlow<Boolean>(false)
    val shouldPopDetails : StateFlow<Boolean> get() = _shouldPopDetails.asStateFlow()

    private val _deleteProductStatus = MutableStateFlow<DeleteProductStatus>(DeleteProductStatus.NORMAL)
    val deleteProductStatus : StateFlow<DeleteProductStatus> get() = _deleteProductStatus.asStateFlow()

    private val _deleteImageStatus = MutableStateFlow(0)
    val deleteImageStatus : StateFlow<Int> get() = _deleteImageStatus.asStateFlow()

    private val _triggerImageUpdate = MutableStateFlow(false)
    val triggerImageUpdate : StateFlow<Boolean> get() = _triggerImageUpdate.asStateFlow()


    init {
        // this is to prepare the images to be shown in image display screen
        CoroutineScope(Dispatchers.IO).launch {
            LocalDatabaseManager.sellerProductImages.collect() {
                _imagesDisplay.value = it
            }
        }
        CoroutineScope(Dispatchers.IO).launch {
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

    }

    fun updateTriggerImageUpdate(trigger: Boolean) {
        _triggerImageUpdate.value = trigger
    }

    fun updateBiddingStatus(status: BiddingStatus) {
        _biddingStatus.value = status
    }

    fun updateShouldDisplayImages(should: Boolean) {
        _shouldDisplayImages.value = should
    }

    fun updateShouldPopImages(should: Boolean) {
        _shouldPopImages.value = should
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

    fun updateDeleteProductStatus(status: DeleteProductStatus) {
        _deleteProductStatus.value = status
    }

    fun confirmDelete() {
        _deleteProductStatus.value = DeleteProductStatus.CONFIRM
    }

    fun deleteProduct(product: ProductOffering) {
        CoroutineScope(Dispatchers.IO).launch {
            if (FirebaseClient.processDeleteProduct(product)) {
                _deleteProductStatus.value = DeleteProductStatus.SUCCESS
            } else {
                _deleteProductStatus.value = DeleteProductStatus.FAILURE
            }
        }
    }

    fun deleteProductImage(image: ProductImageToDisplay) {
        _imagesDisplay.value.remove(image)
    }

    fun updateDeleteImageStatus(status: Int) {
        _deleteImageStatus.value = status
    }

    // show images as soon as it is loaded
    fun prepareAskingProducts() {
        ProductInfo.productChosen.value?.let {
            //Log.i("product details vM", "product is not null")
            ProductInfo.productOfferingWithProductsAsking.value?.let {
                ProductInfo.updateAskingProducts(it.askingProducts)
            }
        }
    }

    fun processBidding(product: ProductOffering, bid: Bid, images: List<ProductImageToDisplay>)  {
        _loadingAlpha.value = 100f

        val imagesBitmap = images.map { image ->
            image.image!!
        }

        CoroutineScope(Dispatchers.IO).launch {
            if (FirebaseClient.processBidding(product, bid, imagesBitmap)) {
                _biddingStatus.value = BiddingStatus.SUCCESS
                _loadingAlpha.value = 0f
            } else {
                _biddingStatus.value = BiddingStatus.FAILURE
                _loadingAlpha.value = 0f
            }
        }
    }


}

