package com.example.gtamapirl.data

class UserData {
    var name: String? = null
    var email: String? = null
    var icon: String? = null

    constructor()

    constructor(name: String, email: String, icon: String) {
        this.name = name
        this.email = email
        this.icon = icon
    }
}