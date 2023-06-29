package com.bitpunchlab.android.barter.firebase.models

class BidFirebase {

    var id = ""
    var userName = ""
    var userId = ""
    var bidProduct : ProductAskingFirebase? = null
    var bidProductId = ""
    var bidTime = ""
    var accepted = false

    constructor()

    constructor(bidId: String, bidUserId: String,
                name: String, biddingFirebase: ProductAskingFirebase?,
                biddingProductId: String,
        time: String, accept: Boolean) {
        id = bidId
        userName = name
        userId = bidUserId
        bidProduct = biddingFirebase
        bidProductId = biddingProductId
        bidTime = time
        accepted = accept
    }
}