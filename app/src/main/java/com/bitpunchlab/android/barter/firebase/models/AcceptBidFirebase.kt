package com.bitpunchlab.android.barter.firebase.models

import com.bitpunchlab.android.barter.models.ProductOffering

class AcceptBidFirebase {

    var id : String = ""
    var product : ProductOfferingFirebase? = null
    var bid : BidFirebase? = null

    constructor()

    constructor(acceptId: String, productOffering: ProductOfferingFirebase, theBid: BidFirebase) {
        id = acceptId
        product = productOffering
        bid = theBid
    }
}