package com.arturkowalczyk300.locationinfo.view

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.startActivity
import com.arturkowalczyk300.locationinfo.R
import com.arturkowalczyk300.locationinfo.model.Location
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*


@Composable
fun LocationView(
    currentLocation: Location,
    loading: Boolean,
    context: Context,
    padding: Dp = 20.dp,
    spacedBy: Dp = 20.dp,
) {
    Box(contentAlignment = Alignment.TopStart, modifier = Modifier.padding(padding)) {
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(spacedBy)) {
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
                    Column(verticalArrangement = Arrangement.spacedBy(spacedBy)) {
                        InfoRow(
                            R.string.location_info_lat,
                            R.string.location_info_lat_value,
                            currentLocation.lat,
                            context
                        )
                        InfoRow(
                            R.string.location_info_lng,
                            R.string.location_info_lng_value,
                            currentLocation.lng,
                            context
                        )
                        InfoRow(
                            R.string.location_info_altitude,
                            R.string.location_info_altitude_value,
                            currentLocation.altitude,
                            context
                        )
                        InfoRow(
                            R.string.location_info_accuracy,
                            R.string.location_info_accuracy_value,
                            currentLocation.accuracy.toDouble(),
                            context
                        )

                        var currentPosition = remember {
                            LatLng(
                                currentLocation.lat,
                                currentLocation.lng
                            )
                        }

                        var cameraPositionState = rememberCameraPositionState {
                            position = CameraPosition.fromLatLngZoom(
                                currentPosition, 10.0f
                            )
                        }
                        Box() {
                            GoogleMap(
                                modifier = Modifier.fillMaxWidth(),
                                cameraPositionState = cameraPositionState,
                            ) {
                                Marker(
                                    state = MarkerState(
                                        position = LatLng(
                                            currentLocation.lat,
                                            currentLocation.lng
                                        )
                                    )
                                )
                            }
                            Row(
                                modifier = Modifier
                                    .align(Alignment.TopStart)
                                    .padding(end = 5.dp)
                            ) {
                                MapToolbar(
                                    buttonCurrentLocationOnClickListener = {
                                        cameraPositionState.position =
                                            CameraPosition.fromLatLngZoom(
                                                LatLng(
                                                    currentLocation.lat,
                                                    currentLocation.lng
                                                ), 10.0f
                                            )
                                    },
                                    buttonOpenInMapsOnClickListener = {
                                        val intentUri =
                                            Uri.parse("geo:${currentLocation.lat}.${currentLocation.lng}?z=10")
                                        val mapIntent = Intent(Intent.ACTION_VIEW, intentUri)
                                        mapIntent.setPackage("com.google.android.apps.maps")
                                        context.startActivity(mapIntent)
                                    })
                            }
                        }
                    }
                }

            }
        }
    }
}

@Composable
private fun InfoRow(labelStringId: Int, valueStringId: Int, value: Double, context: Context) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 10.dp, end = 10.dp)
    ) {
        Text(
            context.getString(labelStringId),
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = context.getString(valueStringId).format(
                value
            ),
            fontSize = 26.sp,
            maxLines = 1
        )
    }
}
