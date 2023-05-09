package com.arturkowalczyk300.locationinfo

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
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
            var currentLocation by remember {
                mutableStateOf(
                    Location(
                        0.0,
                        0.0,
                        0.0,
                        false,
                        0.0f
                    )
                )
            }
            var updateCounter by remember { mutableStateOf(0) }
            var isLoading by remember { mutableStateOf(false) }
            var initDone by remember { mutableStateOf(false) }

            LocationInfoTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background,
                ) {
                    InfoPresentation(currentLocation, isLoading, updateCounter)
                    Buttons(
                        currentLocation = currentLocation,
                        initDone = initDone,
                        isLoading = isLoading,
                        onInitDoneListener = { initDone = it },
                        onDataUpdateListener = { updateCounter = it },
                        onLoadingStateChange = { isLoading = !isLoading })
                }

            }
        }

        requireRuntimePermissions()
    }

    @Composable
    private fun InfoPresentation(
        currentLocation: Location,
        loading: Boolean,
        updateCounter: Int,
    ) {
        Box(contentAlignment = Alignment.TopStart, modifier = Modifier.padding(20.dp)) { //section A
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            getString(R.string.count_data_updates),
                            fontSize = 23.sp
                        )
                        Text(
                            text = getString(R.string.count_data_updates_values).format(
                                updateCounter
                            ),
                            fontSize = 23.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                    if (!currentLocation.hasAccuracy && loading) {
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            CircularProgressIndicator(modifier = Modifier.size(100.dp))
                        }
                    }
                    AnimatedVisibility(
                        visible = currentLocation.hasAccuracy && loading,
                        enter = slideInVertically(
                            initialOffsetY = { -800 },
                            animationSpec = tween(durationMillis = 1000)
                        ),
                        exit = slideOutVertically(
                            targetOffsetY = { -8000 },
                            animationSpec = tween(durationMillis = 1000)
                        )
                    ) {
                        Column() {
                            Spacer(modifier = Modifier.height(50.dp))
                            Row(
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 10.dp, end = 10.dp)
                            ) {
                                Text(
                                    getString(R.string.location_info_lat),
                                    fontSize = 29.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = getString(R.string.location_info_lat_value).format(
                                        currentLocation.lat
                                    ),
                                    fontSize = 29.sp
                                )
                            }
                            Row(
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 10.dp, end = 10.dp)
                            ) {
                                Text(
                                    getString(R.string.location_info_lng),
                                    fontSize = 29.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = getString(R.string.location_info_lng_value).format(
                                        currentLocation.lng
                                    ),
                                    fontSize = 29.sp
                                )
                            }
                            Row(
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 10.dp, end = 10.dp)
                            ) {
                                Text(
                                    getString(R.string.location_info_altitude),
                                    fontSize = 29.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = getString(R.string.location_info_altitude_value).format(
                                        currentLocation.altitude
                                    ),
                                    fontSize = 29.sp
                                )
                            }
                            if (currentLocation.hasAccuracy) {
                                Row(
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(start = 10.dp, end = 10.dp)
                                ) {
                                    Text(
                                        getString(R.string.location_info_accuracy),
                                        fontSize = 29.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = getString(R.string.location_info_accuracy_value).format(
                                            currentLocation.accuracy
                                        ),
                                        fontSize = 29.sp
                                    )
                                }
                            }
                        }
                    }

                }
            }
        }
    }

    @Composable
    private fun Buttons(
        currentLocation: Location,
        initDone: Boolean,
        isLoading: Boolean,
        onInitDoneListener: (initDone: Boolean) -> Unit,
        onDataUpdateListener: (updateCount: Int) -> Unit,
        onLoadingStateChange: () -> Unit,
    ) {
        var locationProvider by remember { mutableStateOf(LocationProvider()) }

        Box( //section B - buttons
            contentAlignment = Alignment.BottomCenter,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 0.dp, top = 0.dp, end = 0.dp, bottom = 10.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    verticalArrangement = Arrangement.Bottom,
                    modifier = Modifier.padding(horizontal = 0.dp, vertical = 10.dp)
                ) {
                    Button(
                        onClick = {
                            if (!initDone) {
                                locationProvider =
                                    LocationProvider.getInstance(
                                        applicationContext
                                    ) { lat, lng, altitude, hasAccuracy, accuracy, updateCount ->
                                        currentLocation.lat = lat
                                        currentLocation.lng = lng
                                        currentLocation.altitude = altitude
                                        currentLocation.hasAccuracy = hasAccuracy
                                        currentLocation.accuracy = accuracy
                                        onDataUpdateListener(updateCount)
                                    }
                                onInitDoneListener(true)
                            }
                            if (!isLoading) {
                                locationProvider.startGPSListening()
                            }

                            if (isLoading)
                                locationProvider?.stopGPSListening()
                            onLoadingStateChange()
                        },
                        shape = RoundedCornerShape(20),
                        colors = if (!isLoading) ButtonDefaults.buttonColors(
                            backgroundColor = Color(
                                42,
                                95,
                                44,
                                255
                            )
                        ) else ButtonDefaults.buttonColors(
                            backgroundColor = Color(
                                104, 34, 29, 255
                            )
                        ),
                        modifier = Modifier
                            .width(350.dp)
                            .padding(vertical = 15.dp)
                    ) {
                        if (!isLoading)
                            Text(
                                text = getString(R.string.loading_gps_start),
                                fontSize = 22.sp,
                                modifier = Modifier.padding(vertical = 15.dp)
                            )
                        else
                            Text(
                                text = getString(R.string.loading_gps_stop),
                                fontSize = 22.sp,
                                modifier = Modifier.padding(vertical = 15.dp)
                            )
                    }
                    Button(
                        onClick = {
                            shareCurrentLocation(currentLocation)
                        },
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = Color(51, 66, 148, 255)
                        ),
                        shape = RoundedCornerShape(20),
                        modifier = Modifier
                            .width(350.dp)
                            .padding(top = 15.dp, bottom = 10.dp)
                    ) {
                        Text(
                            text = getString(R.string.share_gps_location),
                            fontSize = 22.sp,
                            modifier = Modifier.padding(vertical = 15.dp)
                        )
                    }
                }
            }
        }
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

    private fun shareCurrentLocation(location: Location) {
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
