package com.bitpunchlab.android.barter.firebase.models

import com.bitpunchlab.android.barter.models.ProductOffering
import com.bitpunchlab.android.barter.util.SellingDuration

class ProductOfferingFirebase {
    var id: String = ""
    var userId: String = ""
    var name: String = ""
    var category: String = ""
    var sellingDuration : Int = 0
    var images = HashMap<String, String>()
    var currentBids = HashMap<String, BidFirebase>()
    var askingProducts = HashMap<String, ProductAskingFirebase>()
    var dateCreated = ""

    constructor()

    constructor(productId: String, productUserId: String,
                productName: String, productCategory: String,
                duration: Int,
                productImages: HashMap<String, String> = HashMap<String, String>(),
                productCurrentBids: HashMap<String, BidFirebase> = HashMap<String, BidFirebase>(),
                asking: HashMap<String, ProductAskingFirebase> = HashMap(),
                date: String)
    {
        id = productId
        userId = productUserId
        name = productName
        category = productCategory
        sellingDuration = duration
        images = productImages
        currentBids = productCurrentBids
        askingProducts = asking
        dateCreated = date
    }
}