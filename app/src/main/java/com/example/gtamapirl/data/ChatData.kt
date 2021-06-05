package com.example.gtamapirl.data

class ChatData {
    var id: String? = null
    var lastTime: Long? = null
    var user1: String? = null
    var user2: String? = null

    constructor()

    constructor(id: String, lastTime: Long, user1:String, user2:String){
        this.id = id
        this.lastTime = lastTime
        this.user1 = user1
        this.user2 = user2
    }
}