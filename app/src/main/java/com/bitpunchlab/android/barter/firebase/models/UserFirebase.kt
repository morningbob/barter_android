package com.bitpunchlab.android.barter.firebase.models

class UserFirebase {
    var id: String = ""
    var name: String = ""
    var email: String = ""
    var dataCreated: String = ""
    var productsOffering = HashMap<String, ProductOfferingFirebase>()
    // The bids that user accepted
    var userAcceptedBids = HashMap<String, AcceptBidFirebase>()
    // The bids that user offered and got accepted
    var userBidsAccepted = HashMap<String, AcceptBidFirebase>()
    var userCurrentBids = HashMap<String, BidFirebase>()

    constructor()

    constructor(userId: String, userName: String, userEmail: String, userDateCreated: String,
        offering: HashMap<String, ProductOfferingFirebase>,
        acceptedBids: HashMap<String, AcceptBidFirebase> =
                    HashMap(),
        bidsAccepted: HashMap<String, AcceptBidFirebase> =
                    HashMap(),
        currentBids: HashMap<String, BidFirebase> = hashMapOf()
        ) {
        id = userId
        name = userName
        email = userEmail
        dataCreated = userDateCreated
        productsOffering = offering
        userAcceptedBids = acceptedBids
        userBidsAccepted = bidsAccepted
        userCurrentBids = currentBids
    }
}