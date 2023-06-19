package com.bitpunchlab.android.barter.productBiddingList

import androidx.lifecycle.ViewModel
import com.bitpunchlab.android.barter.database.BarterRepository
import com.bitpunchlab.android.barter.models.ProductBidding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class ProductBiddingListViewModel : ViewModel() {

    val _productsBidding = MutableStateFlow<List<ProductBidding>>(listOf())
    val productsBidding : StateFlow<List<ProductBidding>> get() = _productsBidding.asStateFlow()

    val _shouldShowProduct = MutableStateFlow<Boolean>(false)
    val shouldShowProduct : StateFlow<Boolean> get() = _shouldShowProduct.asStateFlow()

    val _shouldDisplayImages = MutableStateFlow<Boolean>(false)
    val shouldDisplayImages : StateFlow<Boolean> get() = _shouldDisplayImages.asStateFlow()

    init {
        CoroutineScope(Dispatchers.IO).launch {
            BarterRepository.getAllProductsBidding()?.collect() { products ->
                _productsBidding.value = products
            }
        }
    }

    fun updateShouldShowProduct(should: Boolean) {
        _shouldShowProduct.value = should
    }

    fun updateShouldDisplayImages(should: Boolean) {
        _shouldShowProduct.value = should
    }

    fun prepareForProduct(product: ProductBidding) {
        ProductBiddingInfo.updateProduct(product)
        _shouldShowProduct.value = true
    }
}