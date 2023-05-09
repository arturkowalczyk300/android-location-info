package com.arturkowalczyk300.locationinfo.model

data class Location(
    var lat: Double =0.0,
    var lng: Double =0.0,
    var altitude: Double =0.0,
    var hasAccuracy: Boolean= false,
    var accuracy: Float =0.0f,
)
