package com.bitpunchlab.android.barter.firebase.models

class ProductAskingFirebase {
    var id: String = ""
    var userId: String = ""
    var name: String = ""
    var category: String = ""
    //var sellingDuration : Int = 0
    var images = HashMap<String, String>()
    var productOfferingId = ""
    //var currentBids = HashMap<String, String>()
    //var askingProducts = HashMap<String, ProductOfferingFirebase>()

    constructor()

    constructor(productId: String, productUserId: String,
                productName: String, productCategory: String,
                productImages: HashMap<String, String> = HashMap<String, String>(),
                productOffering: String,
                )
    {
        id = productId
        userId = productUserId
        name = productName
        category = productCategory
        images = productImages
        productOfferingId = productOffering
    }
}