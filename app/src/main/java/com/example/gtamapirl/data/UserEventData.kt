package com.example.gtamapirl.data

class UserEventData {
    var id: String? = null
    var state: String? = null

    constructor()

    constructor(id: String?, state: String?) {
        this.id = id
        this.state = state
    }
}