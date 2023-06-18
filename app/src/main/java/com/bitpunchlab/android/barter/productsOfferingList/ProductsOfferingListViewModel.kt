package com.bitpunchlab.android.barter.productsOfferingList

import android.util.Log
import androidx.lifecycle.ViewModel
import com.bitpunchlab.android.barter.database.BarterDatabase
import com.bitpunchlab.android.barter.database.BarterRepository
import com.bitpunchlab.android.barter.firebase.FirebaseClient
import com.bitpunchlab.android.barter.models.ProductOffering
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class ProductsOfferingListViewModel : ViewModel() {

    private var _productsOffering = MutableStateFlow<List<ProductOffering>>(listOf())
    val productsOffering : StateFlow<List<ProductOffering>> get() = _productsOffering.asStateFlow()

    //private var _productChosen = MutableStateFlow<ProductOffering?>(null)
    //val productChosen : StateFlow<ProductOffering?> get() = _productChosen.asStateFlow()

    private var _shouldDisplayDetails = MutableStateFlow<Boolean>(false)
    val shouldDisplayDetails : StateFlow<Boolean> get() = _shouldDisplayDetails.asStateFlow()

    // everytime we trigger this page, for example, from navigation bar,
    // we clear the productChosen in ProductInfo, so, the product details page wont' be shown
    //ProductInfo.updateProductChosen(null)
    init {
        ProductInfo.updateProductChosen(null)
        CoroutineScope(Dispatchers.IO).launch {
            FirebaseClient.userId.collect() { id ->
                getAllProductsOffering(FirebaseClient.localDatabase!!, id)
            }
        }
    }

    suspend fun getAllProductsOffering(database: BarterDatabase, id: String) {
        val userList = BarterRepository.getUserProductsOffering(id) ?: listOf()
        if (userList.isNotEmpty()) {
            _productsOffering.value = userList[0].productsOffering
        }
    }

    fun updateShouldDisplayProductDetails(should: Boolean) {
        _shouldDisplayDetails.value = should
    }
}

/*
    private var _productsAskingMap =
        MutableStateFlow<HashMap<String, List<ProductOffering>>>(HashMap<String, List<ProductOffering>>())
    val productsAskingMap : StateFlow<HashMap<String, List<ProductOffering>>> get() =
        _productsAskingMap.asStateFlow()

    suspend fun getCorrespondingAskingProducts(database: BarterDatabase) {
        productsOffering.value.map { each ->
            Log.i("get asking products", "got product's id ${each.productId}")
            BarterRepository.getAskingProducts(database, each.productId).collect() { askingProducts ->
                Log.i("get asking products", "got list ${askingProducts.size}")
                _productsAskingMap.value.put(each.productId, askingProducts)
            }
        }
    }
*/