package com.arturkowalczyk300.locationinfo

data class Location(
    var lat: Double,
    var lng: Double,
    var altitude: Double,
    var hasAccuracy: Boolean,
    var accuracy: Float,
)
