package com.bitpunchlab.android.barter.firebase.models

class UserFirebase {
    var id: String = ""
    var name: String = ""
    var email: String = ""
    var dataCreated: String = ""

    constructor()

    constructor(userId: String, userName: String, userEmail: String, userDateCreated: String) {
        id = userId
        name = userName
        email = userEmail
        dataCreated = userDateCreated
    }
}