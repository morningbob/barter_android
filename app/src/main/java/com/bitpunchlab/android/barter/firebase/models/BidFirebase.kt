package com.bitpunchlab.android.barter.firebase.models

class BidFirebase {

    var id = ""
    var userName = ""
    var bidProduct : ProductBiddingFirebase? = null
    var bidTime = ""

    constructor()

    constructor(bidId: String, name: String, biddingFirebase: ProductBiddingFirebase?,
        time: String) {
        id = bidId
        userName = name
        bidProduct = biddingFirebase
        bidTime = time
    }
}