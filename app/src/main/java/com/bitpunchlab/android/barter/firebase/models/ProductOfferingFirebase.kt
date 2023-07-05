package com.bitpunchlab.android.barter.firebase.models

import com.bitpunchlab.android.barter.models.ProductOffering
import com.bitpunchlab.android.barter.util.SellingDuration

class ProductOfferingFirebase {
    var id: String = ""
    var userId: String = ""
    var userName: String = ""
    var name: String = ""
    var category: String = ""
    var sellingDuration : Int = 0
    var images = HashMap<String, String>()
    var currentBids = HashMap<String, BidFirebase>()
    var askingProducts = HashMap<String, ProductAskingFirebase>()
    var dateCreated = ""
    var status : Int = 0
    var acceptBidId : String = ""

    constructor()

    constructor(productId: String, productUserId: String,
                productUserName: String,
                productName: String, productCategory: String,
                duration: Int,
                productImages: HashMap<String, String> = HashMap<String, String>(),
                productCurrentBids: HashMap<String, BidFirebase> = HashMap<String, BidFirebase>(),
                asking: HashMap<String, ProductAskingFirebase> = HashMap(),
                date: String, productStatus: Int, acceptId : String)
    {
        id = productId
        userId = productUserId
        userName = productUserName
        name = productName
        category = productCategory
        sellingDuration = duration
        images = productImages
        currentBids = productCurrentBids
        askingProducts = asking
        dateCreated = date
        status = productStatus
        acceptBidId = acceptId
    }
}