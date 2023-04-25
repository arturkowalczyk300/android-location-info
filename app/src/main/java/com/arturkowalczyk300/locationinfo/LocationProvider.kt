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
        Log.e("myApp", "load gps data!")

        val locationManager =
            _context!!.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val locationListener: LocationListener =
            object : LocationListener {
                override fun onStatusChanged(
                    provider: String?,
                    status: Int,
                    extras: Bundle?,
                ) {
                    Log.e("myApp", "status=$status")
                    super.onStatusChanged(provider, status, extras)
                }

                override fun onLocationChanged(location: Location) {
                    _listenerLocationChanged.invoke(
                        location.latitude,
                        location.longitude,
                        location.altitude,
                        _updateCount++
                    )
                }
            }

        locationManager.requestLocationUpdates(
            LocationManager.GPS_PROVIDER,
            0,
            0f,
            locationListener
        )
    }
}