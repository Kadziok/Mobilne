package com.example.gtamapirl.data

class Message {
    var text: String? = null
    var userid: String? = null

    constructor()

    constructor(text: String?, id: String?) {
        this.text = text
        this.userid = id
    }
}