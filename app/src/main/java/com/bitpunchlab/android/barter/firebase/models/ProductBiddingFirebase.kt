package com.bitpunchlab.android.barter.firebase.models

class ProductBiddingFirebase {
    var id: String = ""
    var name: String = ""
    var ownerId : String = ""
    var category: String = ""
    var images = HashMap<String, String>()
    //var currentBids = HashMap<String, String>()

    constructor()

    constructor(productId: String, productName: String, productOwnerId: String,
                productCategory: String,
                productImages: HashMap<String, String> = HashMap<String, String>(),
                ) {
        id = productId
        name = productName
        ownerId = productOwnerId
        category = productCategory
        images = productImages
        //currentBids = productCurrentBids
    }
}
