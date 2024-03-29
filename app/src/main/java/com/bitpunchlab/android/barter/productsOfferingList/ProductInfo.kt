package com.bitpunchlab.android.barter.productsOfferingList

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import com.bitpunchlab.android.barter.models.ProductAsking
import com.bitpunchlab.android.barter.models.ProductImageToDisplay
import com.bitpunchlab.android.barter.models.ProductOffering
import com.bitpunchlab.android.barter.models.ProductOfferingAndBids
import com.bitpunchlab.android.barter.models.ProductOfferingAndProductsAsking
import com.bitpunchlab.android.barter.util.UserMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object ProductInfo {

    // it is used to distinguish between user's products they offered
    // or user view the other user's products offered
    // the options are different,
    // like the owner's list will have edit option and view bids option
    private val _userMode = MutableStateFlow<UserMode>(UserMode.OWNER_MODE)
    val userMode : StateFlow<UserMode> get() = _userMode.asStateFlow()

    private val _productChosen = MutableStateFlow<ProductOffering?>(null)
    val productChosen : StateFlow<ProductOffering?> get() = _productChosen.asStateFlow()

    private val _productOfferingWithProductsAsking = MutableStateFlow<ProductOfferingAndProductsAsking?>(null)
    val productOfferingWithProductsAsking : StateFlow<ProductOfferingAndProductsAsking?>
        get() = _productOfferingWithProductsAsking.asStateFlow()

    private val _productOfferingWithBids = MutableStateFlow<ProductOfferingAndBids?>(null)
    val productOfferingWithBids : StateFlow<ProductOfferingAndBids?>
        get() = _productOfferingWithBids.asStateFlow()

    // asking product are Product Asking objects
    // it can be from the product the user offers, or the product the user bids
    private val _askingProducts = MutableStateFlow<SnapshotStateList<ProductAsking>>(
        mutableStateListOf()
    )
    val askingProducts : StateFlow<SnapshotStateList<ProductAsking>> get() = _askingProducts.asStateFlow()

    private val _askingImages = MutableStateFlow<List<List<ProductImageToDisplay>>>(listOf())
    val askingImages : StateFlow<List<List<ProductImageToDisplay>>> get() = _askingImages.asStateFlow()

    fun updateProductChosen(product: ProductOffering?) {
        _productChosen.value = product
    }

    fun updateAskingProducts(products: List<ProductAsking>) {
        _askingProducts.value = products.toMutableStateList()
    }

    fun updateAskingImages(images: List<List<ProductImageToDisplay>>) {
        _askingImages.value = images
    }

    fun updateUserMode(mode: UserMode) {
        _userMode.value = mode
    }

    fun deleteAskingProduct(product: ProductAsking) {
        askingProducts.value.remove(product)
    }

    fun resetProduct() {
        _userMode.value = UserMode.OWNER_MODE
        _productChosen.value = null
        _productOfferingWithProductsAsking.value = null
        _productOfferingWithBids.value = null
    }
}