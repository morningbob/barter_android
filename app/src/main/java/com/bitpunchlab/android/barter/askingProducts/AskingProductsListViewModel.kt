package com.bitpunchlab.android.barter.askingProducts

import androidx.lifecycle.ViewModel
import com.bitpunchlab.android.barter.firebase.FirebaseClient
import com.bitpunchlab.android.barter.models.ProductAsking
import com.bitpunchlab.android.barter.models.ProductOffering
import com.bitpunchlab.android.barter.productsOfferingList.ProductInfo
import com.bitpunchlab.android.barter.sell.AskingProductInfo
import com.bitpunchlab.android.barter.util.DeleteProductStatus
import com.bitpunchlab.android.barter.util.LocalDatabaseManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AskingProductsListViewModel : ViewModel() {

    private val _shouldDismiss = MutableStateFlow<Boolean>(false)
    val shouldDismiss : StateFlow<Boolean> get() = _shouldDismiss.asStateFlow()

    private val _deleteProductStatus = MutableStateFlow<DeleteProductStatus>(DeleteProductStatus.NORMAL)
    val deleteProductStatus : StateFlow<DeleteProductStatus> get() = _deleteProductStatus.asStateFlow()

    private val _loadingAlpha = MutableStateFlow<Float>(0f)
    val loadingAlpha : StateFlow<Float> get() = _loadingAlpha.asStateFlow()

    fun updateShouldDismiss(should: Boolean) {
        _shouldDismiss.value = should
    }
    fun updateDeleteProductStatus(status: DeleteProductStatus) {
        _deleteProductStatus.value = status
    }

    fun deleteAskingProduct(product: ProductOffering, asking: ProductAsking) {
        _loadingAlpha.value = 100f
        ProductInfo.deleteAskingProduct(asking)
        LocalDatabaseManager.deleteProductAskingLocalDatabase(asking)
        CoroutineScope(Dispatchers.IO).launch {
            if (FirebaseClient.processDeleteAskingProduct(product, asking)) {
                _deleteProductStatus.value = DeleteProductStatus.SUCCESS
                _loadingAlpha.value = 0f
            } else {
                _deleteProductStatus.value = DeleteProductStatus.FAILURE
                _loadingAlpha.value = 0f
            }
        }
    }
}