package com.bitpunchlab.android.barter.firebase.models

class ProductBiddingFirebase {
    var id: String = ""
    var productOfferingId : String = ""
    var name: String = ""
    var ownerName: String = ""
    var category: String = ""
    var dateCreated : String = ""
    var duration: Int = 0
    var askingProducts = HashMap<String, ProductAskingFirebase>()
    var images = HashMap<String, String>()
    var bids = HashMap<String, BidFirebase>()

    constructor()

    constructor(productId: String, offeringId: String, productName: String,
                ownerN: String,
                productCategory: String, date: String, dur: Int,
                asking: HashMap<String, ProductAskingFirebase> = HashMap(),
                productImages: HashMap<String, String> = HashMap<String, String>(),
                bid: HashMap<String, BidFirebase>
                ) {
        id = productId
        productOfferingId = offeringId
        name = productName
        ownerName = ownerN
        category = productCategory
        dateCreated = date
        duration = dur
        askingProducts = asking
        images = productImages
        bids = bid
    }
}
