package com.arturkowalczyk300.locationinfo

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import androidx.core.content.ContextCompat.getSystemService

class LocationProvider {
    private var _locationManager: LocationManager? = null
    private var _locationListener: LocationListener? = null

    companion object {
        private var _INSTANCE: LocationProvider? = null
        private var _context: Context? = null
        private var _updateCount = 0
        private lateinit var _listenerLocationChanged: (lat: Double, lng: Double, altitude: Double, updateCount: Int) -> Unit

        fun getInstance(
            context: Context,
            listener: (lat: Double, lng: Double, altitude: Double, updateCount: Int) -> Unit,
        ): LocationProvider {
            _INSTANCE = _INSTANCE ?: LocationProvider()
            _context = context
            _listenerLocationChanged = listener

            return _INSTANCE as LocationProvider
        }
    }

    @SuppressLint("MissingPermission")
    fun startGPSListening() {
        _locationManager =
            _context!!.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        _locationListener =
            LocationListener { location ->
                _listenerLocationChanged.invoke(
                    location.latitude,
                    location.longitude,
                    location.altitude,
                    _updateCount++
                )
            }

        _locationManager!!.requestLocationUpdates(
            LocationManager.GPS_PROVIDER,
            0,
            0f,
            _locationListener as LocationListener
        )
    }

    fun stopGPSListening() {
        _locationManager!!.removeUpdates(_locationListener!!)
    }
}