package com.arturkowalczyk300.locationinfo.view

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.arturkowalczyk300.locationinfo.R
import com.arturkowalczyk300.locationinfo.model.Location
import com.arturkowalczyk300.locationinfo.ui.theme.LocationInfoTheme
import com.arturkowalczyk300.locationinfo.viewmodels.MainViewModel
import com.arturkowalczyk300.locationinfo.viewmodels.MainViewModelFactory
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.maps.android.compose.GoogleMap

const val REQUEST_PERMISSION_LOCATION = 1

class MainActivity : AppCompatActivity() {
    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModel =
            ViewModelProvider(this, MainViewModelFactory()).get(MainViewModel::class.java)

        viewModel.initLocationProvider(this)
        setContent {
            var isLoading by remember { mutableStateOf(false) }
            var initDone by remember { mutableStateOf(false) }
            val currentLocation by viewModel.currentLocation.observeAsState(initial = Location())

            LocationInfoTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background,
                ) {
                    val context = this
                    Column() {
                        Row(modifier = Modifier.weight(0.75f)) {
                            LocationView(
                                currentLocation,
                                isLoading,
                                context
                            )
                        }

                        Row(modifier = Modifier.weight(0.25f)) {
                            Buttons(
                                currentLocation = currentLocation,
                                initDone = initDone,
                                isLoading = isLoading,
                                onInitDoneListener = { initDone = it },
                                onLoadingStateChange = {
                                    isLoading = !isLoading
                                    if (isLoading) viewModel.startListeningLocationChange()
                                    else viewModel.stopListeningLocationChange()
                                })
                        }
                    }
                }
            }
        }

        requireRuntimePermissions()
    }


    @Composable
    private fun Buttons(
        currentLocation: Location,
        initDone: Boolean,
        isLoading: Boolean,
        onInitDoneListener: (initDone: Boolean) -> Unit,
        onLoadingStateChange: () -> Unit,
    ) {
        Box(
            contentAlignment = Alignment.BottomCenter,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
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
                                onInitDoneListener(true)
                            }
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

    @Composable
    fun MapView(
        modifier: Modifier,
        onMapReadyCallback: OnMapReadyCallback,
    ) { //TODO: move to another file
        AndroidView(
            //modifier = Modifier.fillMaxSize(),
            factory = { context ->
                val view = layoutInflater.inflate(R.layout.map_fragment, null, false)
                view
            },
            update = { view ->
                (supportFragmentManager.findFragmentById(R.id.map_fragment) as SupportMapFragment).apply {
                    getMapAsync(onMapReadyCallback)
                }
            },
            modifier = modifier
        )
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
