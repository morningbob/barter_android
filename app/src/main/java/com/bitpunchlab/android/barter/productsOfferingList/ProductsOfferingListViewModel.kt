package com.bitpunchlab.android.barter.productsOfferingList

import android.util.Log
import androidx.lifecycle.ViewModel
import com.bitpunchlab.android.barter.database.BarterDatabase
import com.bitpunchlab.android.barter.database.BarterRepository
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

    suspend fun getAllProductsOffering(database: BarterDatabase, id: String) {
        //_productsOffering.value =
        val userList = BarterRepository.getUserProductsOffering(database, id)
        if (userList.isNotEmpty()) {
            _productsOffering.value = userList[0].productsOffering
        }
    }
}