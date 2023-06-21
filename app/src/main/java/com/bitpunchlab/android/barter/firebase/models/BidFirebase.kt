package com.bitpunchlab.android.barter.firebase.models

class BidFirebase {

    var id = ""
    var userName = ""
    var userId = ""
    var bidProduct : ProductBiddingFirebase? = null
    var bidTime = ""

    constructor()

    constructor(bidId: String, bidUserId: String,
                name: String, biddingFirebase: ProductBiddingFirebase?,
        time: String) {
        id = bidId
        userName = name
        userId = bidUserId
        bidProduct = biddingFirebase
        bidTime = time
    }
}