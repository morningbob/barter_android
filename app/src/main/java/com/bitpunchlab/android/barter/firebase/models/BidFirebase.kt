package com.bitpunchlab.android.barter.firebase.models

class BidFirebase {

    var id = ""
    var userName = ""
    var askingProduct : ProductAskingFirebase? = null
    var bidTime = ""

    constructor()

    constructor(bidId: String, name: String, askingFirebase: ProductAskingFirebase?,
        time: String) {
        id = bidId
        userName = name
        askingProduct = askingFirebase
        bidTime = time
    }
}