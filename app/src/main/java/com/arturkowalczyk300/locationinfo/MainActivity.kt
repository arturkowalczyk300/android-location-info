package com.arturkowalczyk300.locationinfo

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Paint.Align
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.widget.ProgressBar
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
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

                    Box(contentAlignment = Alignment.TopStart, modifier = Modifier.padding(10.dp)) {
                        Row(horizontalArrangement = Arrangement.Center) {
                            Column(verticalArrangement = Arrangement.Center) {
                                Text(
                                    text = "Data update count: $updateCounter",
                                    fontSize = 23.sp,
                                    modifier = Modifier.padding(0.dp, 10.dp)
                                )
                                Text(
                                    text = "lat: %.10f".format(currentLocation.lat),
                                    fontSize = 23.sp
                                )
                                Text(
                                    text = "lng: %.10f".format(currentLocation.lng),
                                    fontSize = 23.sp
                                )
                                Text(
                                    text = "altitude: %.4f\"".format(currentLocation.altitude),
                                    fontSize = 23.sp
                                )
                                if (loading)
                                    Row(
                                        horizontalArrangement = Arrangement.Center,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        CircularProgressIndicator()
                                    }
                            }
                        }
                    }
                    Box(
                        contentAlignment = Alignment.BottomCenter,
                        modifier = Modifier.padding(10.dp)
                    ) {
                        Row() {
                            Button(
                                onClick = {
                                    if (!initDone) {
                                        locationProvider =
                                            LocationProvider.getInstance(
                                                applicationContext
                                            ) { lat, lng, altitude, updateCount ->
                                                currentLocation.lat = lat
                                                currentLocation.lng = lng
                                                currentLocation.altitude = altitude
                                                updateCounter = updateCount
                                            }
                                        initDone = true
                                    }
                                    if (!loading) {
                                        locationProvider!!.startGPSListening()
                                    }

                                    if (loading)
                                        locationProvider?.stopGPSListening()
                                    loading = !loading
                                },
                                colors = ButtonDefaults.buttonColors(backgroundColor = Color.Blue),
                                modifier = Modifier.padding(
                                    PaddingValues(
                                        3.dp,
                                        0.dp,
                                        3.dp,
                                        0.dp
                                    )
                                )
                            ) {
                                if (!loading)
                                    Text(
                                        text = "Start loading GPS data!",
                                        fontSize = 17.sp
                                    )
                                else
                                    Text(
                                        text = "Stop loading GPS data!",
                                        fontSize = 17.sp
                                    )
                            }
                            Button(
                                onClick = {
                                    shareCurrentLocation(currentLocation)
                                },
                                colors = ButtonDefaults.buttonColors(backgroundColor = Color.Blue),
                                modifier = Modifier.padding(
                                    PaddingValues(
                                        3.dp,
                                        0.dp,
                                        3.dp,
                                        0.dp
                                    )
                                )
                            ) {
                                Text(
                                    text = "Share GPS location!",
                                    fontSize = 17.sp
                                )
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

    private fun shareCurrentLocation(location: com.arturkowalczyk300.locationinfo.Location) {
        val text =
            "My current location: lat=%.10f, lng=%.10f, altitude=%.4f".format(
                location.lat,
                location.lng,
                location.altitude
            )
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.setType("text/plain")
        shareIntent.putExtra(Intent.EXTRA_TEXT, text)
        startActivity(Intent.createChooser(shareIntent, "Share via..."))
    }
}

