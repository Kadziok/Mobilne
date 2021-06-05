package com.example.gtamapirl.data

class MessageData {
    var text: String? = null
    var userid: String? = null
    var timeStamp: Long? = null

    constructor()

    constructor(text: String?, id: String?, timeStamp: Long?) {
        this.text = text
        this.userid = id
        this.timeStamp = timeStamp
    }
}