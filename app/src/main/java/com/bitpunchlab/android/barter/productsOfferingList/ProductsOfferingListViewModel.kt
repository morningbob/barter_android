package com.bitpunchlab.android.barter.productsOfferingList

import android.util.Log
import androidx.lifecycle.ViewModel
import com.bitpunchlab.android.barter.database.BarterDatabase
import com.bitpunchlab.android.barter.database.BarterRepository
import com.bitpunchlab.android.barter.firebase.FirebaseClient
import com.bitpunchlab.android.barter.models.ProductOffering
import com.bitpunchlab.android.barter.util.UserMode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class ProductsOfferingListViewModel : ViewModel() {

    var userMode : UserMode = UserMode.OWNER_MODE

    private var _productsOffering = MutableStateFlow<List<ProductOffering>>(listOf())
    val productsOffering : StateFlow<List<ProductOffering>> get() = _productsOffering.asStateFlow()

    private var _shouldDisplayDetails = MutableStateFlow<Boolean>(false)
    val shouldDisplayDetails : StateFlow<Boolean> get() = _shouldDisplayDetails.asStateFlow()

    // everytime we trigger this page, for example, from navigation bar,
    // we clear the productChosen in ProductInfo, so, the product details page wont' be shown

    init {
        ProductInfo.updateProductChosen(null)

    }



    fun updateShouldDisplayProductDetails(should: Boolean) {
        _shouldDisplayDetails.value = should
    }
}
/*
suspend fun getAllProductsOffering(database: BarterDatabase, id: String) {
        val userList = BarterRepository.getUserProductsOffering(id) ?: listOf()
        if (userList.isNotEmpty()) {
            _productsOffering.value = userList[0].productsOffering
        }
    }
 */
/*
        CoroutineScope(Dispatchers.IO).launch {
            ProductInfo.userMode.collect() {
                //CoroutineScope(Dispatchers.IO).launch {
                if (it == UserMode.OWNER_MODE) {
                    FirebaseClient.userId.collect() { id ->
                        getAllProductsOffering(FirebaseClient.localDatabase!!, id)
                    }
                } else {
                    BarterRepository.getAllProductOffering()?.collect() {
                        Log.i("product offering list vm", "no of products retrieved ${it.size}")
                        _productsOffering.value = it
                    }
                }
            }

        }

         */
