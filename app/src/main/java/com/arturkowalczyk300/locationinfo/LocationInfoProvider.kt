package com.arturkowalczyk300.locationinfo

import android.annotation.SuppressLint
import android.content.Context
import android.location.LocationListener
import android.location.LocationManager

class LocationInfoProvider {
    private var _locationManager: LocationManager? = null
    private var _locationListener: LocationListener? = null

    companion object {
        private var _INSTANCE: LocationInfoProvider? = null
        private var _context: Context? = null
        private lateinit var listenerLocationChanged: (
            lat: Double,
            lng: Double,
            altitude: Double,
            hasAccuracy: Boolean,
            accuracy: Float
        ) -> Unit

        fun getInstance(
            context: Context,
            listener: (
                lat: Double,
                lng: Double,
                altitude: Double,
                hasAccuracy: Boolean,
                accuracy: Float
            ) -> Unit,
        ): LocationInfoProvider {
            _INSTANCE = _INSTANCE ?: LocationInfoProvider()
            _context = context
            listenerLocationChanged = listener

            return _INSTANCE as LocationInfoProvider
        }
    }

    @SuppressLint("MissingPermission")
    fun startGPSListening() {
        _locationManager = _context!!.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        _locationListener = LocationListener { location ->
            listenerLocationChanged.invoke(
                location.latitude,
                location.longitude,
                location.altitude,
                location.hasAccuracy(),
                location.accuracy
            )
        }

        _locationManager!!.requestLocationUpdates(
            LocationManager.GPS_PROVIDER, 0, 0f, _locationListener as LocationListener
        )
    }

    fun stopGPSListening() {
        _locationManager!!.removeUpdates(_locationListener!!)
    }
}