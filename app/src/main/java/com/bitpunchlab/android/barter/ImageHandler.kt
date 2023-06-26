package com.bitpunchlab.android.barter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import com.bitpunchlab.android.barter.util.ProductImage
import com.bitpunchlab.android.barter.util.createPlaceholderImage
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.ByteArrayOutputStream
import java.util.UUID

// the image loader is so important for the app to display images
// keeping a context in it while the app is active
@SuppressLint("StaticFieldLeak")
object ImageHandler {

    @SuppressLint("StaticFieldLeak")
    var currentContext : Context? = null

    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun loadImage(url: String) =
        suspendCancellableCoroutine<Bitmap?> { cancellableContinuation ->
            Glide.with(currentContext!!)
                .asBitmap()
                .placeholder(R.mipmap.imageplaceholder)
                .load(url)
                .into(object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                        cancellableContinuation.resume(resource) {}
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {
                    }

                    override fun onLoadFailed(erroryDrawable: Drawable?) {
                        cancellableContinuation.resume(createPlaceholderImage(currentContext!!)) {}
                    }
                })
        }

    
    suspend fun prepareImages(imagesUrl: List<String>) : List<ProductImage> {
        // retrieve images from cloud storage and store in view model
        // we need to do like this because Images Display Screen's setup
        // can't be customized to use Glide to load images as needed

        val images = mutableListOf<ProductImage>()

        for (i in 0..imagesUrl.size - 1) {
            // so before we load the image, we show the placeholder image
            //_imagesDisplay.value.add(i, ProductImage(UUID.randomUUID().toString(), com.bitpunchlab.android.barter.util.createPlaceholderImage(context)))
            images.add(i, ProductImage(UUID.randomUUID().toString(), createPlaceholderImage()))
            CoroutineScope(Dispatchers.IO).launch {
                loadImage(imagesUrl[i])?.let {
                    //_imagesDisplay.value.set(i, ProductImage(i.toString(), it))
                    images.set(i, ProductImage(i.toString(), it))
                }
            }.join()
        }
        return images

    }

    fun convertBitmapToBytes(bitmap: Bitmap) : ByteArray {
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        return baos.toByteArray()
    }

    fun createPlaceholderImage() : Bitmap {
        return BitmapFactory.decodeResource(currentContext!!.resources, R.mipmap.imageplaceholder)
    }
}