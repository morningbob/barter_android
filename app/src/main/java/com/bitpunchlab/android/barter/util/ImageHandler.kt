package com.bitpunchlab.android.barter.util

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.core.content.ContextCompat
import com.bitpunchlab.android.barter.R
import com.bitpunchlab.android.barter.models.ProductImageToDisplay
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
import java.io.File
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import kotlinx.coroutines.async
import java.io.IOException
import java.lang.IllegalStateException

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
    suspend fun loadedImagesFlow(imagesUrl: List<String>) : Flow<Pair<Int, ProductImageToDisplay>> = channelFlow {
        for (i in 0..imagesUrl.size - 1) {
            // so before we load the image, we show the placeholder image

            send(Pair(i, ProductImageToDisplay(
                imageId = i.toString(),
                image = loadImageFromCloud(imagesUrl[i]),
                imageUrlCloud = "")))
        }
        // this is required to keep the channel opened
        awaitClose()
    }

    suspend fun loadImageFromLocal(imageUrl: String) : Bitmap? {
        currentContext?.let {
            val source = ImageDecoder.createSource(currentContext!!.contentResolver, imageUrl.toUri())
            return ImageDecoder.decodeBitmap(source)
        }
        return null
    }

    suspend fun loadImageFromCloud(url: String) : Bitmap? {
        return CoroutineScope(Dispatchers.IO).async {
            loadImage(url)
        }.await()
    }

    suspend fun saveImageExternalStorage(displayName: String, bitmap: Bitmap) : Uri? {

        val imageCollection = sdk29AndUp {
            // this location allows all apps to access the image
            MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        } ?: MediaStore.Images.Media.EXTERNAL_CONTENT_URI

        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "${displayName.substring(38, displayName.length)}.jpg")
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            put(MediaStore.Images.Media.WIDTH, bitmap.width)
            put(MediaStore.Images.Media.HEIGHT, bitmap.height)
            put(MediaStore.Images.Media.IS_PENDING, 1)
        }

         try {
             val uri = currentContext!!.contentResolver.insert(imageCollection, contentValues)
             uri?.let {
                 currentContext!!.contentResolver.openOutputStream(it, "wt").use { outputStream ->
                     //outputStream.flush()
                     if (!bitmap.compress(Bitmap.CompressFormat.JPEG, 95, outputStream)) {
                         throw IOException("couldn't save image")
                     }
                 } ?: throw IOException("failed to create media store entry")

                 Log.i("image handler", "saved image successfully.")

                 contentValues.clear()
                 contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
                 //currentContext!!.contentResolver.insert(uri, contentValues)
                 currentContext!!.contentResolver.update(uri, contentValues, null, null)
                 return it
             }
             return null
         } catch (e: IllegalStateException) {
             Log.i("saving image", "same filename found, not saving it")
             e.printStackTrace()
             return null
        } catch (e: IOException) {
             e.printStackTrace()
             return null
        }
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
