package com.bitpunchlab.android.barter.productsOfferingList

import com.bitpunchlab.android.barter.models.ProductAsking
import com.bitpunchlab.android.barter.models.ProductOffering
import com.bitpunchlab.android.barter.models.ProductOfferingAndBids
import com.bitpunchlab.android.barter.models.ProductOfferingAndProductsAsking
import com.bitpunchlab.android.barter.util.ProductImage
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
    private val _askingProducts = MutableStateFlow<List<ProductAsking>>(listOf())
    val askingProducts : StateFlow<List<ProductAsking>> get() = _askingProducts.asStateFlow()

    private val _askingImages = MutableStateFlow<List<List<ProductImage>>>(listOf())
    val askingImages : StateFlow<List<List<ProductImage>>> get() = _askingImages.asStateFlow()

    fun updateProductChosen(product: ProductOffering?) {
        _productChosen.value = product
    }

    fun updateAskingProducts(products: List<ProductAsking>) {
        _askingProducts.value = products
    }

    fun updateAskingImages(images: List<List<ProductImage>>) {
        _askingImages.value = images
    }

    fun updateUserMode(mode: UserMode) {
        _userMode.value = mode
    }

    fun updateProductOfferingWithProductsAsking(product: ProductOfferingAndProductsAsking) {
        //Log.i("Product info", "products asking were set")
        _productOfferingWithProductsAsking.value = product
    }

    fun updateProductOfferingWithBids(product: ProductOfferingAndBids) {
        //Log.i("Product info", "bids were set")
        _productOfferingWithBids.value = product
    }

    fun resetProduct() {
        _userMode.value = UserMode.OWNER_MODE
        _productChosen.value = null
        _productOfferingWithProductsAsking.value = null
        _productOfferingWithBids.value = null
    }
}