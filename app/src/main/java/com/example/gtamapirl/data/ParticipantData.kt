package com.example.gtamapirl.data

class ParticipantData {
    var id: String? = null
    var name: String? = null
    var state: String? = null
    var icon: String? = null

    constructor()

    constructor(id: String, name: String, state: String, icon: String) {
        this.id = id
        this.name = name
        this.state = state
        this.icon = icon
    }
}