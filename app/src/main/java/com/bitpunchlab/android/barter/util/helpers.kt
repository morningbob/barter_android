package com.bitpunchlab.android.barter.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.bitpunchlab.android.barter.R
import com.bitpunchlab.android.barter.models.ProductOffering
import java.io.ByteArrayOutputStream

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

data class ProductImage(
    var id : String,
    var image: Bitmap
)

data class SortProduct<T>(
    var key : Int,
    var value : T
)