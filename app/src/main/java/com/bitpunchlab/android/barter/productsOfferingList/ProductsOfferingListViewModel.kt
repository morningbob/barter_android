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

    private var _shouldDisplayDetails = MutableStateFlow<Boolean>(false)
    val shouldDisplayDetails : StateFlow<Boolean> get() = _shouldDisplayDetails.asStateFlow()


    fun updateShouldDisplayProductDetails(should: Boolean) {
        _shouldDisplayDetails.value = should
    }
}
