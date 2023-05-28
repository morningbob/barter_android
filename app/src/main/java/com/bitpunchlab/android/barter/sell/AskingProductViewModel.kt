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

    private val _status = MutableStateFlow(0)
    val status : StateFlow<Int> get() = _status.asStateFlow()

    private val _askingProductsList = MutableStateFlow<List<ProductOffering>>(mutableListOf())
    val askingProductsList : StateFlow<List<ProductOffering>> get() = _askingProductsList.asStateFlow()

    private val _askingProductsImages = MutableStateFlow<List<List<Bitmap>>>(mutableListOf())
    val askingProductsImages : StateFlow<List<List<Bitmap>>> get() = _askingProductsImages.asStateFlow()

    private val _update = MutableStateFlow(false)
    val update : StateFlow<Boolean> get() = _update.asStateFlow()

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

            AskingProductInfo.askingProducts.add(newProduct)
            AskingProductInfo.askingProductsImages.add(askingProductImages.value)
            //updateAskingProductsList(newProduct)
            //updateAskingProductsImages(askingProductImages.value)
            // clean up
            clearForm()
            _status.value = 2
            _update.value = true
        }
    }

    fun cancelUpdate() {
        _update.value = false
    }

    fun updateAskingProductsList(product: ProductOffering) {
        val newList = askingProductsList.value.toMutableList()
        newList.add(product)
        _askingProductsList.value = newList
    }

    fun updateAskingProductsImages(images: List<Bitmap>) {
        val newList = askingProductsImages.value.toMutableList()
        newList.add(images)
        _askingProductsImages.value = newList
    }

    fun updateStatus(status: Int) {
        _status.value = status
    }

    fun validateInputs() : Boolean {
        return !(productName.value == "" || productCategory.value == Category.NOT_SET)
    }

    private fun clearForm() {
        _productName.value = ""
        _productCategory.value = Category.NOT_SET
        _askingProductImages.value = listOf()
        _shouldExpandCategory.value = false
    }
}