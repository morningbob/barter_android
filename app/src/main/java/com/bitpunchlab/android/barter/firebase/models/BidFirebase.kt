package com.bitpunchlab.android.barter.firebase.models

class BidFirebase {

    var id = ""
    var userName = ""
    var userId = ""
    var bidProduct : ProductAskingFirebase? = null
    var bidProductId = ""
    var bidTime = ""
    var acceptBidId = ""
    //var acceptBidId = false

    constructor()

    constructor(bidId: String, bidUserId: String,
                name: String, biddingFirebase: ProductAskingFirebase?,
                biddingProductId: String,
        time: String, acceptId: String) {//, accept: Boolean) {
        id = bidId
        userName = name
        userId = bidUserId
        bidProduct = biddingFirebase
        bidProductId = biddingProductId
        bidTime = time
        acceptBidId = acceptId
        //accepted = accept
    }
}