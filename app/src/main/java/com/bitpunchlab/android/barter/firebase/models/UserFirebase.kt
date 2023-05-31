package com.bitpunchlab.android.barter.firebase.models

class UserFirebase {
    var id: String = ""
    var name: String = ""
    var email: String = ""
    var dataCreated: String = ""
    var productsOffering = HashMap<String, ProductOfferingFirebase>()

    constructor()

    constructor(userId: String, userName: String, userEmail: String, userDateCreated: String,
        offering: HashMap<String, ProductOfferingFirebase>) {
        id = userId
        name = userName
        email = userEmail
        dataCreated = userDateCreated
        productsOffering = offering
    }
}