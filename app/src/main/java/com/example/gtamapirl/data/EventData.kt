package com.example.gtamapirl.data

class EventData {
    var id: String? = null
    var name: String? = null
    var creator: String? = null
    var date: String? = null
    var time: String? = null
    var description: String? = null
    var latitude: Double? = null
    var longitude: Double? = null

    constructor()

    constructor(id: String, name: String, creator: String, date: String, time: String, latitude: Double, longitude: Double, description: String) {
        this.id = id
        this.name = name
        this.creator = creator
        this.date = date
        this.time = time
        this.latitude = latitude
        this.longitude = longitude
        this.description = description
    }
}