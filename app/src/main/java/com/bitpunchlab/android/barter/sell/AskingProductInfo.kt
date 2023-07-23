package com.bitpunchlab.android.barter.sell

import android.graphics.Bitmap
import com.bitpunchlab.android.barter.models.ProductAsking
import com.bitpunchlab.android.barter.models.ProductImageToDisplay
import com.bitpunchlab.android.barter.models.ProductOffering
import com.bitpunchlab.android.barter.productsOfferingList.ProductInfo
import com.bitpunchlab.android.barter.util.Category
import com.bitpunchlab.android.barter.util.SellingDuration
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

// the temporary asking products info are stored here.
// they are not in the local database yet
object AskingProductInfo {

    private val _askingProducts = MutableStateFlow<List<ProductAsking>>(listOf())
    val askingProducts : StateFlow<List<ProductAsking>> get() = _askingProducts.asStateFlow()

    private val _askingImages = MutableStateFlow<List<List<ProductImageToDisplay>>>(listOf())
    val askingImages : StateFlow<List<List<ProductImageToDisplay>>> get() = _askingImages.asStateFlow()

    fun updateAskingProducts(asking: List<ProductAsking>) {
        _askingProducts.value = asking
    }

    fun updateAskingImages(imagesList: List<List<ProductImageToDisplay>>) {
        _askingImages.value = imagesList
    }

    fun addAskingProduct(product: ProductAsking) {
        val newList = _askingProducts.value.toMutableList()
        newList.add(product)
        _askingProducts.value = newList
    }

    fun addAskingImages(images: List<ProductImageToDisplay>) {
        val newList = askingImages.value.toMutableList()
        newList.add(images)
        _askingImages.value = newList

    }

}