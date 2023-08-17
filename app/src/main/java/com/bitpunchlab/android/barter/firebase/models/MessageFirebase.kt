package com.bitpunchlab.android.barter.firebase.models

class MessageFirebase {

    var id : String = ""
    var messageText : String = ""
    var ownerUserId : String = ""
    var otherUserId : String = ""
    var sender : Boolean = true
    var ownerName : String = ""
    var otherName : String = ""
    var date : String = ""

    constructor()

    constructor(messageId: String, message: String, ownerId: String, otherId: String, senderFlag: Boolean,
        ownerUser: String, otherUser: String, time: String) {

        id = messageId
        messageText = message
        ownerUserId = ownerId
        otherUserId = otherId
        sender = senderFlag
        ownerName = ownerUser
        otherName = otherUser
        date = time
    }
}