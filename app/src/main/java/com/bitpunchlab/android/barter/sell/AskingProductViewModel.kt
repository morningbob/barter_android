package com.bitpunchlab.android.barter.sell

import android.graphics.Bitmap
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import com.bitpunchlab.android.barter.firebase.FirebaseClient
import com.bitpunchlab.android.barter.models.ProductOffering
import com.bitpunchlab.android.barter.util.Category
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.*
import kotlin.collections.HashMap

class AskingProductViewModel : ViewModel() {

    private val _productName = MutableStateFlow("")
    val productName : StateFlow<String> get() = _productName.asStateFlow()

    private val _shouldExpandCategory = MutableStateFlow(false)
    val shouldExpandCategory : StateFlow<Boolean> get() = _shouldExpandCategory.asStateFlow()

    private val _productCategory = MutableStateFlow(Category.NOT_SET)
    val productCategory : StateFlow<Category> get() = _productCategory.asStateFlow()

    private val _askingProductImages = MutableStateFlow<List<Bitmap>>(listOf())
    val askingProductImages : StateFlow<List<Bitmap>> get() = _askingProductImages.asStateFlow()

    fun updateShouldExpandCategory(should: Boolean) {
        _shouldExpandCategory.value = should
    }

    fun updateCategory(cat: Category) {
        _productCategory.value = cat
    }

    fun updateName(name: String) {
        _productName.value = name
    }

    fun updateAskingImages(bitmap: Bitmap) {
        val newList = askingProductImages.value.toMutableList()
        newList.add(bitmap)
        _askingProductImages.value = newList
    }

    fun processAskingProduct() {
        // validate inputs
        if (validateInputs()) {
            val newProduct = ProductOffering(productId = UUID.randomUUID().toString(),
            category = productCategory.value.name,
            userId = FirebaseClient.currentUserFirebase.value!!.id,
            name = productName.value)
            //AskingProductInfo.askingProductList.add(newProduct)
            //AskingProductInfo.askingProductImages.addAll(askingProductImages.value)
            val productMap = HashMap<ProductOffering, List<Bitmap>>()
            productMap.put(newProduct, askingProductImages.value)
            AskingProductInfo.askingProductsMap.put(newProduct.productId,
                productMap
            )
            // clean up

        }
    }

    fun validateInputs() : Boolean {
        return !(productName.value == "" || productCategory.value == Category.NOT_SET)
    }

    private fun clearForm() {
        
    }
}