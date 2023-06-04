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
    var currentBids = HashMap<String, String>()
    var askingProducts = HashMap<String, ProductOfferingFirebase>()

    constructor()

    constructor(productId: String, productUserId: String,
                productName: String, productCategory: String,
                duration: Int,
                productImages: HashMap<String, String> = HashMap<String, String>(),
                productCurrentBids: HashMap<String, String> = HashMap<String, String>(),
                asking: HashMap<String, ProductOfferingFirebase> = HashMap())
    {
        id = productId
        userId = productUserId
        name = productName
        category = productCategory
        sellingDuration = duration
        images = productImages
        currentBids = productCurrentBids
        askingProducts = asking
    }
}