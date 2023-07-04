package com.bitpunchlab.android.barter.productsOfferingList

import android.util.Log
import androidx.compose.runtime.collectAsState
import com.bitpunchlab.android.barter.models.ProductAsking
import com.bitpunchlab.android.barter.models.ProductOffering
import com.bitpunchlab.android.barter.models.ProductOfferingAndBid
import com.bitpunchlab.android.barter.models.ProductOfferingAndProductAsking
import com.bitpunchlab.android.barter.util.UserMode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

object ProductInfo {

    // it is used to distinguish between user's products they offered
    // or user view the other user's products offered
    // the options are different,
    // like the owner's list will have edit option and view bids option
    val _userMode = MutableStateFlow<UserMode>(UserMode.OWNER_MODE)
    val userMode : StateFlow<UserMode> get() = _userMode.asStateFlow()

    val _productChosen = MutableStateFlow<ProductOffering?>(null)
    val productChosen : StateFlow<ProductOffering?> get() = _productChosen.asStateFlow()

    private val _productOfferingWithProductsAsking = MutableStateFlow<ProductOfferingAndProductAsking?>(null)
    val productOfferingWithProductsAsking : StateFlow<ProductOfferingAndProductAsking?>
        get() = _productOfferingWithProductsAsking.asStateFlow()

    private val _productOfferingWithBids = MutableStateFlow<ProductOfferingAndBid?>(null)
    val productOfferingWithBids : StateFlow<ProductOfferingAndBid?>
        get() = _productOfferingWithBids.asStateFlow()

    // asking product are Product Asking objects
    // it can be from the product the user offers, or the product the user bids
    val _askingProducts = MutableStateFlow<List<ProductAsking>>(listOf())
    val askingProducts : StateFlow<List<ProductAsking>> get() = _askingProducts.asStateFlow()

    fun updateProductChosen(product: ProductOffering?) {
        _productChosen.value = product
    }

    fun updateAskingProducts(products: List<ProductAsking>) {
        _askingProducts.value = products
    }

    fun updateUserMode(mode: UserMode) {
        _userMode.value = mode
    }

    fun updateProductOfferingWithProductsAsking(product: ProductOfferingAndProductAsking) {
        Log.i("Product info", "products asking were set")
        _productOfferingWithProductsAsking.value = product
    }

    fun updateProductOfferingWithBids(product: ProductOfferingAndBid) {
        Log.i("Product info", "bids were set")
        _productOfferingWithBids.value = product
    }

    fun resetProduct() {
        _userMode.value = UserMode.OWNER_MODE
        _productChosen.value = null
        _productOfferingWithProductsAsking.value = null
        _productOfferingWithBids.value = null
    }
}