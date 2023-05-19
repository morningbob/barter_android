package com.bitpunchlab.android.barter.firebase.models

class ProductOfferingFirebase {
    var id: String = ""
    var userId: String = ""
    var name: String = ""
    var category: String = ""
    var images = HashMap<String, String>()
    var currentBids = HashMap<String, String>()

    constructor()

    constructor(productId: String, productUserId: String,
                productName: String, productCategory: String,
                productImages: HashMap<String, String> = HashMap<String, String>(),
                productCurrentBids: HashMap<String, String> = HashMap<String, String>()) {
        id = productId
        userId = productUserId
        name = productName
        category = productCategory
        images = productImages
        currentBids = productCurrentBids
    }
}