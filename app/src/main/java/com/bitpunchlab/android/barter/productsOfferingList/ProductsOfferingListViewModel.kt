package com.bitpunchlab.android.barter.productsOfferingList

import androidx.lifecycle.ViewModel
import com.bitpunchlab.android.barter.util.UserMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ProductsOfferingListViewModel : ViewModel() {

    var userMode : UserMode = UserMode.OWNER_MODE

    private var _shouldDisplayDetails = MutableStateFlow<Boolean>(false)
    val shouldDisplayDetails : StateFlow<Boolean> get() = _shouldDisplayDetails.asStateFlow()


    fun updateShouldDisplayProductDetails(should: Boolean) {
        _shouldDisplayDetails.value = should
    }
}
