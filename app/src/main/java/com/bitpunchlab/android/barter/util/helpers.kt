package com.bitpunchlab.android.barter.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.util.Log
import com.bitpunchlab.android.barter.R
import com.bitpunchlab.android.barter.models.ProductOffering
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.ByteArrayOutputStream
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

fun validateEmail(email: String) : String {
    if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
        return "Email is invalid."
    }
    return ""
}

// I separate the test of password because I want to present different error messages.
fun validatePassword(password: String) : String {
    // test length
    val regexLength = "\\w{8,20}".toRegex()
    if (!regexLength.matches(password)) {
        return "Password must have length within 8 and 20 characters.  It must contains only alphabets."
    }
    val regexLetter = "[a-zA-Z]+".toRegex()
    if (!regexLetter.containsMatchIn(password)) {
        return "Password must contains at least one letter."
    }

    return ""
}

fun validateConfirmPassword(password: String, confirmPass: String) : String {
    if (password != confirmPass) {
        return "Password and confirm password must be the same."
    }
    return ""
}

fun convertBitmapToBytes(bitmap: Bitmap) : ByteArray {
    val baos = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
    return baos.toByteArray()
}

fun createPlaceholderImage(context: Context) : Bitmap {
    return BitmapFactory.decodeResource(context.resources, R.mipmap.imageplaceholder)
}

@OptIn(ExperimentalCoroutinesApi::class)
suspend fun loadImage(url: String, context: Context) =
    suspendCancellableCoroutine<Bitmap?> { cancellableContinuation ->
        Glide.with(context)
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
                    //super.onLoadFailed(errorDrawable)
                    //val bitmap = BitmapFactory.decodeResource(context.resources, R.mipmap.imageplaceholder)
                    cancellableContinuation.resume(createPlaceholderImage(context)) {}
                }
            })
    }

fun getCurrentDateTime() : String {
    //val dateTime = ZonedDateTime.now()
    //val dateTime = OffsetDateTime.now(ZoneOffset.UTC)
    val dateTime = LocalDateTime.now().atZone(ZoneId.of("America/Toronto"))

    val formatter = DateTimeFormatter.ISO_DATE_TIME
    val newFormat = LocalDateTime.parse(dateTime.toString(), formatter)
    // 2023-06-28T13:14:45.399015
    Log.i("Time", "new format: $newFormat")
    return newFormat.toString()
}

fun parseDateTime(dateTimeString: String) : LocalDateTime? {
    val formatter = DateTimeFormatter.ISO_DATE_TIME
    var dateTime : LocalDateTime? = null
    try {
        dateTime = LocalDateTime.parse(dateTimeString, formatter)
    } catch (e: DateTimeParseException) {
        Log.i("parse date time", "parsing error: $e")
    }

    return dateTime
}

val acceptBidStatusMap = mapOf<Int, BidStatus>(
    0 to BidStatus.NORMAL,
    1 to BidStatus.REQUESTED_CLOSE,
    2 to BidStatus.TO_CONFIRM_CLOSE,
    3 to BidStatus.CLOSED
)

data class SortHelpObject<T>(
    var key : Int,
    var value : T
)
/*
Log.i("Time", "dateTime ${dateTime}")
val year = dateTime.year
Log.i("Time", "year $year")
val month = dateTime.month
Log.i("Time", "month $month")
val day = dateTime.dayOfMonth
Log.i("Time", "day $day")
val hour = dateTime.hour
Log.i("Time", "hour $hour")
val minute = dateTime.minute
Log.i("Time", "minute $minute")

val stringFormatTime = "$month $day, $year $hour:$minute"
Log.i("Time", "string format: $stringFormatTime")
*/