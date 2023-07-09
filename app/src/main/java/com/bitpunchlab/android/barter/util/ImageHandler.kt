package com.bitpunchlab.android.barter.util

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import com.bitpunchlab.android.barter.R
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.ByteArrayOutputStream

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

    // since we need to get responses from the other coroutines,
    // we use channel flow instead of flow
    suspend fun loadedImagesFlow(imagesUrl: List<String>) : Flow<Pair<Int, ProductImage>> = channelFlow {
        for (i in 0..imagesUrl.size - 1) {
            // so before we load the image, we show the placeholder image
            //emit(Pair(i, ProductImage(UUID.randomUUID().toString(), createPlaceholderImage())))
            CoroutineScope(Dispatchers.IO).launch {
                loadImage(imagesUrl[i])?.let {
                    send(Pair(i, ProductImage(i.toString(), it)))
                }
            }
        }
        // this is required to keep the channel opened
        awaitClose()
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
/*
    // this method has the problem that it return the whole list at one point of time
    // if this method is in the view model, it can make images available as soon as
    // they are set.
    suspend fun prepareImages(imagesUrl: List<String>) : List<ProductImage>  {
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


 */