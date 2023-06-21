package com.bitpunchlab.android.barter.firebase.models

import com.bitpunchlab.android.barter.models.ProductOffering

class AcceptBidFirebase {

    var id : String = ""
    var productOffering : ProductOfferingFirebase? = null
    var bid : BidFirebase? = null

    constructor()

    constructor(acceptId: String, product: ProductOfferingFirebase, theBid: BidFirebase) {
        id = acceptId
        productOffering = product
        bid = theBid

    }
}