package com.bitpunchlab.android.barter.util

import com.bitpunchlab.android.barter.firebase.models.ProductOfferingFirebase
import com.bitpunchlab.android.barter.firebase.models.UserFirebase
import com.bitpunchlab.android.barter.models.ProductOffering
import com.bitpunchlab.android.barter.models.User

fun convertUserFirebaseToUser(userFirebase: UserFirebase) : User {
    return User(userFirebase.id, userFirebase.name, userFirebase.email, userFirebase.dataCreated)
}

fun convertUserToUserFirebase(user: User) : UserFirebase {

    return UserFirebase(user.id, user.name, user.email, user.dataCreated, HashMap<String, ProductOfferingFirebase>())
}

// I like to maintain the order of the images, that is better for the user
fun convertProductOfferingToFirebase(product: ProductOffering, asking: List<ProductOffering> = listOf()) : ProductOfferingFirebase {

    val imagesMap = HashMap<String, String>()
    for (i in 0..product.images.size - 1) {
        imagesMap.put(i.toString(), product.images[i])
    }
    val askingMap = HashMap<String, String>()
    // this is a product offering, not a asking product
    if (asking.isNotEmpty()) {
        for (i in 0..asking.size - 1) {
            askingMap.put(asking[i].productId, asking[i].productId)
        }
    }

    return ProductOfferingFirebase(
        productId = product.productId, productName = product.name,
        productUserId = product.userId,
        productImages = imagesMap, productCategory = product.category,
        productCurrentBids = HashMap<String, String>(),
        asking = askingMap
    )
}

fun convertProductFirebaseToProduct(productFirebase: ProductOfferingFirebase) : ProductOffering {
    //var imagesList = mutableListOf<String>()
    //val listOfKeys = mutableListOf<String>()
    //val listOfValues = mutableListOf<String>()
    val listOfSortProduct = mutableListOf<SortProduct>()
    // convert the keys in the map to integers and rank
    for ((key, value) in productFirebase.images) {
        //listOfKeys.add(key)
        //listOfValues.add(value)
        listOfSortProduct.add(SortProduct(key.toInt(), value))
    }

    listOfSortProduct.sortBy { it.key }

    val imagesList = listOfSortProduct.map { it.value }

    //imagesList.addAll( listOfSortProduct.)

    return ProductOffering(
        productId = productFirebase.id, name = productFirebase.name,
        userId = productFirebase.userId, category = productFirebase.category,
        images = imagesList
    )
}
