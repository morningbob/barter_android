package com.bitpunchlab.android.barter.util

import com.bitpunchlab.android.barter.firebase.models.AcceptBidFirebase
import com.bitpunchlab.android.barter.firebase.models.BidFirebase
import com.bitpunchlab.android.barter.firebase.models.ProductAskingFirebase
import com.bitpunchlab.android.barter.firebase.models.ProductBiddingFirebase
import com.bitpunchlab.android.barter.firebase.models.ProductOfferingFirebase
import com.bitpunchlab.android.barter.firebase.models.UserFirebase
import com.bitpunchlab.android.barter.models.AcceptBid
import com.bitpunchlab.android.barter.models.AskingHolder
import com.bitpunchlab.android.barter.models.AskingProductsHolder
import com.bitpunchlab.android.barter.models.Bid
import com.bitpunchlab.android.barter.models.BidsHolder
import com.bitpunchlab.android.barter.models.ProductAsking
import com.bitpunchlab.android.barter.models.ProductBidding
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
fun convertProductOfferingToFirebase(product: ProductOffering, askingProducts: List<ProductAsking>,
    bids: List<Bid>) : ProductOfferingFirebase {

    val imagesMap = HashMap<String, String>()
    for (i in 0..product.images.size - 1) {
        imagesMap.put(i.toString(), product.images[i])
    }
    val askingMap = HashMap<String, ProductAskingFirebase>()

    if (askingProducts.isNotEmpty()) {
        for (i in 0..askingProducts.size - 1) {
            askingMap.put(i.toString(), convertProductAskingToFirebase(askingProducts[i]))
        }
    }

    val bidsMap = HashMap<String, BidFirebase>()
    if (bids.isNotEmpty()) {
        for (i in 0..bids.size - 1) {
            bidsMap.put(i.toString(), convertBidToBidFirebase(bids[i]))
        }
    }

    return ProductOfferingFirebase(
        productId = product.productId, productName = product.name,
        productUserId = product.userId,
        productUserName = product.userName,
        productImages = imagesMap, productCategory = product.category,
        duration = product.duration,
        asking = askingMap,
        date = product.dateCreated,
        productCurrentBids = bidsMap,
        productStatus = product.status,
        acceptId = product.acceptBidId
    )
}

// here we sort the key of the products in the product firebase before
// we add to the list of the product offering object before saving to database
fun convertProductFirebaseToProduct(productFirebase: ProductOfferingFirebase) : ProductOffering {

    val imagesList = sortElements(productFirebase.images)
    //val askingProductsFirebase = sortElements(productFirebase.askingProducts)
    //val askingProducts = askingProductsFirebase.map { each ->
    //    convertProductFirebaseToProductAsking(each)
    //}

    return ProductOffering(
        productId = productFirebase.id, name = productFirebase.name,
        userId = productFirebase.userId, userName = productFirebase.userName,
        category = productFirebase.category,
        images = imagesList, duration = productFirebase.sellingDuration,
        dateCreated = productFirebase.dateCreated,
        status = productFirebase.status,
        acceptBidId = productFirebase.acceptBidId

    )
}

fun convertProductFirebaseToProductAsking(productFirebase: ProductAskingFirebase) : ProductAsking {

    val imagesList = sortElements(productFirebase.images)
    //val askingProductsFirebase = sortProductsOffering(productFirebase.images)
    //val askingProducts = askingProductsFirebase.map { each ->
    //    convertProductFirebaseToProduct(each)
    //}

    return ProductAsking(
        productId = productFirebase.id, name = productFirebase.name,
        productOfferingId = productFirebase.productOfferingId,
        category = productFirebase.category,
        images = imagesList
    )
}

fun convertProductAskingToFirebase(productAsking: ProductAsking, ) : ProductAskingFirebase {
    val imagesMap = HashMap<String, String>()

    for (i in 0..productAsking.images.size - 1) {
        imagesMap.put(i.toString(), productAsking.images[i])
    }

    return ProductAskingFirebase(
        productId = productAsking.productId,
        productName = productAsking.name,
        productCategory = productAsking.category,
        productImages = imagesMap,
        //productUserId = productAsking.userId,
        productOffering = productAsking.productOfferingId
    )
}

fun convertProductAskingFirebaseToProductAsking(productAskingFirebase: ProductAskingFirebase) : ProductAsking {

    val imagesList = sortElements(productAskingFirebase.images)

    return ProductAsking(
        productId = productAskingFirebase.id,
        //userId = productAskingFirebase.userId,
        name = productAskingFirebase.name,
        category = productAskingFirebase.category,
        images = imagesList,
        productOfferingId = productAskingFirebase.productOfferingId
    )
}

fun convertProductBiddingFirebaseToProductBidding(productFirebase: ProductBiddingFirebase) : ProductBidding {

    val imagesList = sortElements(productFirebase.images)
    val bidsFirebaseList = sortElements(productFirebase.bids)
    val bidsList = bidsFirebaseList.map { bidFirebase ->
        convertBidFirebaseToBid(bidFirebase)
    }
    val askingProducts = sortElements(productFirebase.askingProducts)
    val askingList = askingProducts.map { asking ->
        convertProductAskingFirebaseToProductAsking(asking)
    }

    return ProductBidding(
        productBidId = productFirebase.id,
        productOfferingForBid = productFirebase.productOfferingId,
        productName = productFirebase.name,
        ownerName = productFirebase.ownerName,
        productCategory = productFirebase.category,
        biddingDateCreated = productFirebase.dateCreated,
        biddingDuration = productFirebase.duration,
        productImages = imagesList,
        bidsHolder = BidsHolder(bidsList),
        biddingAskingProducts = AskingHolder(askingList)
    )
}

fun convertProductBiddingToProductBiddingFirebase(productBidding: ProductBidding)
    : ProductBiddingFirebase {

    val imagesMap = HashMap<String, String>()
    for (i in 0..productBidding.productImages.size - 1) {
        imagesMap.put(i.toString(), productBidding.productImages[i])
    }

    val bidsMap = HashMap<String, BidFirebase>()
    for (i in 0..productBidding.bidsHolder.bids.size - 1) {
        bidsMap.put(i.toString(), convertBidToBidFirebase(productBidding.bidsHolder.bids[i], ))
    }
    productBidding.bidsHolder.bids

    val askingMap = HashMap<String, ProductAskingFirebase>()
    for (i in 0..productBidding.biddingAskingProducts.askingList.size - 1) {
        askingMap.put(i.toString(), convertProductAskingToFirebase(productBidding.biddingAskingProducts.askingList[i]))
    }

    return ProductBiddingFirebase(
        productId = productBidding.productBidId,
        offeringId = productBidding.productOfferingForBid,
        productName = productBidding.productName,
        ownerN = productBidding.ownerName,
        productCategory = productBidding.productCategory,
        date = productBidding.biddingDateCreated,
        dur = productBidding.biddingDuration,
        productImages = imagesMap,
        bid = bidsMap,
        asking = askingMap
    )
}

fun convertBidFirebaseToBid(bidFirebase: BidFirebase) : Bid {

    var bidProduct : ProductAsking? = null
    bidFirebase.bidProduct?.let {
        bidProduct = convertProductAskingFirebaseToProductAsking(it)
    }

    return Bid(
        bidId = bidFirebase.id,
        bidUserName = bidFirebase.userName,
        bidUserId = bidFirebase.userId,
        bidTime = bidFirebase.bidTime,
        bidProduct = bidProduct!!,
        bidProductId = bidFirebase.bidProductId,
        acceptBidId = bidFirebase.acceptBidId
    )
}

fun convertBidToBidFirebase(bid: Bid) : BidFirebase {

    return BidFirebase(
        bidId = bid.bidId,
        name = bid.bidUserName,
        bidUserId = bid.bidUserId,
        biddingFirebase = convertProductAskingToFirebase(bid.bidProduct), //(bid.bidProduct!!),
        time = bid.bidTime,
        acceptId = bid.acceptBidId,
        biddingProductId = bid.bidProductId
    )
}


/*
fun convertAcceptBidFirebaseToAcceptBid(acceptBidFirebase: AcceptBidFirebase) : AcceptBid {

    val productImages = sortElements(acceptBidFirebase.product!!.images)
    val productBidImages = sortElements(acceptBidFirebase.bid!!.bidProduct!!.images)

    return AcceptBid(
        acceptId = acceptBidFirebase.id,
        productId = acceptBidFirebase.product!!.id,
        productName = acceptBidFirebase.product!!.name,
        productCategory = acceptBidFirebase.product!!.category,
        productImages = productImages,
        productSellerName = acceptBidFirebase.product!!.userName,
        productBidName = acceptBidFirebase.bid!!.bidProduct!!.name,
        productBidCategory = acceptBidFirebase.bid!!.bidProduct!!.category,
        productBidImages = productBidImages,
        productBidderName = acceptBidFirebase.bid!!.userName,
        bidDate = acceptBidFirebase.bid!!.bidTime
    )
}

 */

fun <T> sortElements(productsMap: HashMap<String, T>) : List<T>{
    val listOfSortProduct = mutableListOf<SortHelpObject<T>>()
    for ((key, value) in productsMap) {
        listOfSortProduct.add(SortHelpObject(key.toInt(), value))
    }

    listOfSortProduct.sortBy { it.key }

    val list = listOfSortProduct.map { it.value }
    return list
}
