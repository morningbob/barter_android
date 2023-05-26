package com.bitpunchlab.android.barter.util

import com.bitpunchlab.android.barter.firebase.models.ProductOfferingFirebase
import com.bitpunchlab.android.barter.firebase.models.UserFirebase
import com.bitpunchlab.android.barter.models.ProductOffering
import com.bitpunchlab.android.barter.models.User

fun convertUserFirebaseToUser(userFirebase: UserFirebase) : User {
    return User(userFirebase.id, userFirebase.name, userFirebase.email, userFirebase.dataCreated)
}

fun convertUserToUserFirebase(user: User) : UserFirebase {
    return UserFirebase(user.id, user.name, user.email, user.dataCreated)
}

fun convertProductOfferingToFirebase(product: ProductOffering, imageFilenames: List<String>) : ProductOfferingFirebase {

    val imagesMap = HashMap<String, String>()
    for (i in 0..product.images.size - 1) {
        imagesMap.put(imageFilenames[i], product.images[i])
    }

    return ProductOfferingFirebase(productId = product.productId, productName = product.name,
        productUserId = product.userId,
        productImages = imagesMap, productCategory = product.category,
        productCurrentBids = HashMap<String, String>()
    )
}