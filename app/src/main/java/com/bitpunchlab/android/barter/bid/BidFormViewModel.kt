package com.bitpunchlab.android.barter.bid

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import com.bitpunchlab.android.barter.firebase.FirebaseClient
import com.bitpunchlab.android.barter.models.AskingHolder
import com.bitpunchlab.android.barter.models.Bid
import com.bitpunchlab.android.barter.models.BidsHolder
import com.bitpunchlab.android.barter.models.ProductAsking
import com.bitpunchlab.android.barter.models.ProductImageToDisplay
import com.bitpunchlab.android.barter.util.Category
import com.bitpunchlab.android.barter.util.LocalDatabaseManager
import com.bitpunchlab.android.barter.util.getCurrentDateTime
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Date
import java.util.UUID

class BidFormViewModel : ViewModel() {

    private val _bidTime = MutableStateFlow("")
    val bidTime : StateFlow<String> get() = _bidTime.asStateFlow()

    private val _bidProductName = MutableStateFlow("")
    val bidProductName : StateFlow<String> get() = _bidProductName.asStateFlow()

    private val _bidProductCategory = MutableStateFlow(Category.NOT_SET)
    val bidProductCategory : StateFlow<Category> get() = _bidProductCategory.asStateFlow()

    private val _imagesDisplay = MutableStateFlow<SnapshotStateList<ProductImageToDisplay>>(mutableStateListOf())
    val imagesDisplay : StateFlow<SnapshotStateList<ProductImageToDisplay>> get() = _imagesDisplay.asStateFlow()

    private val _shouldExpandCategoryDropdown = MutableStateFlow<Boolean>(false)
    val shouldExpandCategoryDropdown : StateFlow<Boolean>
        get() = _shouldExpandCategoryDropdown.asStateFlow()

    private val _shouldDisplayImages = MutableStateFlow<Boolean>(false)
    val shouldDisplayImages : StateFlow<Boolean> get() = _shouldDisplayImages.asStateFlow()

    private val _shouldPopImages = MutableStateFlow<Boolean>(false)
    val shouldPopImages : StateFlow<Boolean> get() = _shouldPopImages.asStateFlow()

    private val _deleteImageStatus = MutableStateFlow(0)
    val deleteImageStatus : StateFlow<Int> get() = _deleteImageStatus.asStateFlow()

    fun updateBidProductName(name: String) {
        _bidProductName.value = name
    }

    fun updateBidProductCategory(cat: Category) {
        _bidProductCategory.value = cat
    }

    fun updateShouldExpandCategoryDropdown(should: Boolean) {
        _shouldExpandCategoryDropdown.value = should
    }

    fun updateImagesDisplay(bitmap: Bitmap) {
        val productImage = ProductImageToDisplay(UUID.randomUUID().toString(), bitmap, "")
        //val newList = imagesDisplay.value.toMutableList()
        //newList.add(productImage)

        _imagesDisplay.value.add(productImage)
    }

    fun updateShouldDisplayImages(should: Boolean) {
        _shouldDisplayImages.value = should
    }

    fun updateShouldPopImages(should: Boolean) {
        _shouldPopImages.value = should
    }

    // check if all fields are valid
    // create the bid
    fun createBid() : Bid? {
        if (bidProductName.value != "" && bidProductCategory.value != Category.NOT_SET) {
            val bidProduct = ProductAsking(
                productId = UUID.randomUUID().toString(),
                productOfferingId = LocalDatabaseManager.productChosen.value?.productId!!,
                name = bidProductName.value,
                category = bidProductCategory.value.label,
                images = listOf() // add the images later, need to upload then get url
            )
            //Log.i("bid formVM", "created bid")
            return Bid(bidId = UUID.randomUUID().toString(),
                bidUserName = FirebaseClient.currentUserFirebase.value!!.name,
                bidUserId = FirebaseClient.userId.value,
                bidProduct = bidProduct,
                bidTime = getCurrentDateTime(),
                bidProductId = LocalDatabaseManager.productChosen.value!!.productId,
            )
        }
        return null
    }

    fun deleteImage(image: ProductImageToDisplay) {
        _imagesDisplay.value.remove(image)
    }

    fun updateDeleteImageStatus(status: Int) {
        _deleteImageStatus.value = status
    }

    fun clearForm() {
        _bidProductName.value = ""
        _bidProductCategory.value = Category.NOT_SET
    }


}
/*
            val bidProduct1 = ProductBidding(
                productBidId = UUID.randomUUID().toString(),
                productName = bidProductName.value,
                productCategory = bidProductCategory.value.label,
                ownerName = FirebaseClient.currentUserFirebase.value!!.name,
                biddingDateCreated = getCurrentDateTime(),
                productOfferingForBid = ProductBiddingInfo.product.value?.productOfferingForBid ?: "",
                bidsHolder = BidsHolder(listOf<Bid>()),
                biddingDuration = 0,
                productImages = listOf(),
                biddingAskingProducts = AskingHolder(listOf())
            )

             */