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
// here, we set the order of the products and asking products according to the place they are in the list
fun convertProductOfferingToFirebase(product: ProductOffering, asking: List<ProductOffering> = listOf()) : ProductOfferingFirebase {

    val imagesMap = HashMap<String, String>()
    for (i in 0..product.images.size - 1) {
        imagesMap.put(i.toString(), product.images[i])
    }
    val askingMap = HashMap<String, ProductOfferingFirebase>()
    // this is a product offering, not a asking product
    if (asking.isNotEmpty()) {
        for (i in 0..asking.size - 1) {
            askingMap.put(i.toString(), convertProductOfferingToFirebase(asking[i]))
        }
    }

    return ProductOfferingFirebase(
        productId = product.productId, productName = product.name,
        productUserId = product.userId,
        productImages = imagesMap, productCategory = product.category,
        duration = product.duration,
        productCurrentBids = HashMap<String, String>(),
        asking = askingMap
    )
}

// here we sort the key of the products in the product firebase before
// we add to the list of the product offering object before saving to database
fun convertProductFirebaseToProduct(productFirebase: ProductOfferingFirebase) : ProductOffering {

    val listOfSortProduct = mutableListOf<SortProduct>()
    // convert the keys in the map to integers and rank
    for ((key, value) in productFirebase.images) {

        listOfSortProduct.add(SortProduct(key.toInt(), value))
    }

    listOfSortProduct.sortBy { it.key }

    val imagesList = listOfSortProduct.map { it.value }


    return ProductOffering(
        productId = productFirebase.id, name = productFirebase.name,
        userId = productFirebase.userId, category = productFirebase.category,
        images = imagesList, duration = productFirebase.sellingDuration
    )
}
