package com.arturkowalczyk300.locationinfo

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.arturkowalczyk300.locationinfo.ui.theme.LocationInfoTheme

const val REQUEST_PERMISSION_LOCATION = 1

class MainActivity : ComponentActivity() {
    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var currentLocation by remember { mutableStateOf(Location(0.0, 0.0, 0.0)) }
            var updateCounter by remember { mutableStateOf(0) }
            var loading by remember { mutableStateOf(false) }
            var initDone by remember { mutableStateOf(false) }
            var locationProvider: LocationProvider? = null
            LocationInfoTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background,
                ) {
                    Row(horizontalArrangement = Arrangement.Center) {
                        Column(verticalArrangement = Arrangement.Center) {
                            Text(text = "Data update count: $updateCounter", fontSize = 20.sp)
                            Text(text = "lat: ${currentLocation.lat}", fontSize = 20.sp)
                            Text(
                                text = "lng: ${currentLocation.lng}",
                                fontSize = 20.sp
                            )
                            Text(text = "alt: ${currentLocation.altitude}", fontSize = 20.sp)
                            Row() {
                                Button(
                                    onClick = { //todo: start

                                        if (!initDone) {
                                            locationProvider =
                                                LocationProvider.getInstance(applicationContext) { lat, lng, altitude, updateCount ->
                                                    currentLocation.lat = lat
                                                    currentLocation.lng = lng
                                                    currentLocation.altitude = altitude
                                                    updateCounter = updateCount
                                                }

                                            locationProvider!!.startGPSListening()
                                            initDone = true
                                        }
                                    },
                                    modifier = Modifier.padding(
                                        PaddingValues(
                                            5.dp,
                                            0.dp,
                                            5.dp,
                                            0.dp
                                        )
                                    )
                                ) {
                                    Text("Get GPS data!")
                                }
                                Button(
                                    onClick = {

                                    },
                                    modifier = Modifier.padding(
                                        PaddingValues(
                                            5.dp,
                                            0.dp,
                                            5.dp,
                                            0.dp
                                        )
                                    )
                                ) {
                                    Text("Share GPS location!")
                                }
                            }
                        }
                    }
                }
            }
        }

        requireRuntimePermissions()
    }

    private fun requireRuntimePermissions() {
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_PERMISSION_LOCATION
            )
        }
    }

    @Composable
    fun Greeting(name: String) {
        Text(text = "Hello $name!")
    }

    @Preview(showBackground = true)
    @Composable
    fun DefaultPreview() {
        LocationInfoTheme {
            Greeting("Android")
        }
    }

    @Composable
    fun gpsData(lat: Double, lng: Double, alt: Double) {

    }


}

