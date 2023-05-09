package com.arturkowalczyk300.locationinfo.model

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.arturkowalczyk300.locationinfo.LocationInfoProvider

class MainRepository {
    private lateinit var locationProvider: LocationInfoProvider

    private var _currentLocation = MutableLiveData<Location>()
    val currentLocation: LiveData<Location> = _currentLocation

    fun initLocationProvider(
        context: Context
    ) {
        locationProvider = LocationInfoProvider.getInstance(context) { lat, lng, altitude, hasAccuracy, accuracy ->
            _currentLocation.value = Location(lat,lng,altitude,hasAccuracy, accuracy)
        }
    }

    fun startListeningLocationChange() {
        locationProvider.startGPSListening()
    }

    fun stopListeningLocationChange() {
        locationProvider.stopGPSListening()
    }

}