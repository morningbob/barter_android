package com.bitpunchlab.android.barter.firebase.models

class ProductBiddingFirebase {
    var id: String = ""
    var productOfferingId : String = ""
    var name: String = ""
    var ownerName: String = ""
    var category: String = ""
    var dateCreated : String = ""
    var durationLeft: Int = 0
    var images = HashMap<String, String>()
    var bids = HashMap<String, BidFirebase>()

    constructor()

    constructor(productId: String, offeringId: String, productName: String,
                ownerN: String,
                productCategory: String, date: String, duration: Int,
                productImages: HashMap<String, String> = HashMap<String, String>(),
                bid: HashMap<String, BidFirebase>
                ) {
        id = productId
        productOfferingId = offeringId
        name = productName
        ownerName = ownerN
        productOfferingId = productId
        category = productCategory
        dateCreated = date
        durationLeft = duration
        images = productImages
        bids = bid
    }
}
