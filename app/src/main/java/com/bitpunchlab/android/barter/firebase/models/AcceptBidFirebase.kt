package com.bitpunchlab.android.barter.firebase.models

import com.bitpunchlab.android.barter.models.ProductOffering

class AcceptBidFirebase {

    var id : String = ""
    var product : ProductOfferingFirebase? = null
    var bid : BidFirebase? = null
    var acceptTime : String = ""
    var status = 0

    constructor()

    constructor(acceptId: String, productOffering: ProductOfferingFirebase, theBid: BidFirebase,
        time: String, stat: Int) {
        id = acceptId
        product = productOffering
        bid = theBid
        acceptTime = time
        status = stat
    }
}