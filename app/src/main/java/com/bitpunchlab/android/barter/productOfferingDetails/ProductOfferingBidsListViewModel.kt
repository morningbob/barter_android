package com.bitpunchlab.android.barter.productOfferingDetails

import android.content.Context
import androidx.lifecycle.ViewModel
import com.bitpunchlab.android.barter.database.BarterRepository
import com.bitpunchlab.android.barter.models.Bid
import com.bitpunchlab.android.barter.util.ImageType
import com.bitpunchlab.android.barter.util.ProductImage
import com.bitpunchlab.android.barter.util.createPlaceholderImage
import com.bitpunchlab.android.barter.util.loadImage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class ProductOfferingBidsListViewModel : ViewModel() {



    private val _bid = MutableStateFlow<Bid?>(null)
    val bid : StateFlow<Bid?> get() = _bid.asStateFlow()

    private val _bidProductImages = MutableStateFlow<MutableList<ProductImage>>(mutableListOf())
    val bidProductImages : StateFlow<MutableList<ProductImage>> get() = _bidProductImages.asStateFlow()

    private val _shouldShowBid = MutableStateFlow<Boolean>(false)
    val shouldShowBid : StateFlow<Boolean> get() = _shouldShowBid.asStateFlow()

    fun updateBid(bid: Bid) {
        _bid.value = bid
    }

    fun updateShouldShowBid(should: Boolean) {
        _shouldShowBid.value = should
    }

    fun prepareImages(imagesUrl: List<String>, context: Context) {
        // retrieve images from cloud storage and store in view model
        // we need to do like this because Images Display Screen's setup
        // can't be customized to use Glide to load images as needed

        for (i in 0..imagesUrl.size - 1) {
            // so before we load the image, we show the placeholder image
            _bidProductImages.value.add(i, ProductImage(UUID.randomUUID().toString(), createPlaceholderImage(context)))
            CoroutineScope(Dispatchers.IO).launch {
                loadImage(imagesUrl[i], context)?.let {
                    _bidProductImages.value.set(i, ProductImage(i.toString(), it))
                }
            }
        }
        //updateShouldShowBid(true)

    }
}
/*
    init {
        CoroutineScope(Dispatchers.IO).launch {
            bid.collect() { theBid ->
                theBid?.let {
                    val images = mutableListOf<ProductImage>()
                    theBid.bidProduct?.images?.map { image ->
                        val bitmap = loadImage(image, )
                            images.add(ProductImage(id = UUID.randomUUID().toString(), ))
                    }
                }
            }
        }
    }

 */
