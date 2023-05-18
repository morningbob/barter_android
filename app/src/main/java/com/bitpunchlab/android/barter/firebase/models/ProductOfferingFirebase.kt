package com.bitpunchlab.android.barter.firebase.models

class ProductOfferingFirebase {
    var id: String = ""
    var name: String = ""
    var category: String = ""
    var images = HashMap<String, String>()
    var currentBids = HashMap<String, String>()

    constructor()

    constructor(productId: String, productName: String, productCategory: String,
                productImages: HashMap<String, String> = HashMap<String, String>(),
                productCurrentBids: HashMap<String, String> = HashMap<String, String>()) {
        id = productId
        name = productName
        category = productCategory
        images = productImages
        currentBids = productCurrentBids
    }
}