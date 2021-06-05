package com.example.gtamapirl.data

class UserData {
    var name: String? = null
    var email: String? = null

    constructor()

    constructor(name: String, email: String) {
        this.name = name
        this.email = email
    }
}