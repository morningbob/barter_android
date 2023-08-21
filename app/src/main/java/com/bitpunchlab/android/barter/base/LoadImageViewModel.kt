package com.bitpunchlab.android.barter.base

import android.annotation.SuppressLint
import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import com.bitpunchlab.android.barter.database.BarterRepository
import com.bitpunchlab.android.barter.models.ProductImageToDisplay
import com.bitpunchlab.android.barter.util.ImageHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async

class LoadImageViewModel : ViewModel() {

    @SuppressLint("SuspiciousIndentation")
    suspend fun loadImageDatabase(imageUrl: String) : Bitmap? {
        var productImage : ProductImageToDisplay? = null
        var image : Bitmap? = null
        val productImageList = CoroutineScope(Dispatchers.IO).async {
            BarterRepository.getImage(imageUrl)
        }.await()
        if (productImageList != null && productImageList.isNotEmpty()) {
            productImage = productImageList.get(0)
                if (productImage.imageUrlLocal != null)  {
                    image = CoroutineScope(Dispatchers.IO).async {
                        ImageHandler.loadImageFromLocal(productImage.imageUrlLocal!!)
                    }.await()
                }
        }
        return image
    }
}