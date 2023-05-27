package com.bitpunchlab.android.barter.firebase.models

import com.bitpunchlab.android.barter.models.ProductOffering

class ProductOfferingFirebase {
    var id: String = ""
    var userId: String = ""
    var name: String = ""
    var category: String = ""
    var images = HashMap<String, String>()
    var currentBids = HashMap<String, String>()
    var askingProducts = HashMap<String, String>()

    constructor()

    constructor(productId: String, productUserId: String,
                productName: String, productCategory: String,
                productImages: HashMap<String, String> = HashMap<String, String>(),
                productCurrentBids: HashMap<String, String> = HashMap<String, String>(),
                asking: HashMap<String, String> = HashMap())
    {
        id = productId
        userId = productUserId
        name = productName
        category = productCategory
        images = productImages
        currentBids = productCurrentBids
        askingProducts = asking
    }
}