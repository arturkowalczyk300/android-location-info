package com.arturkowalczyk300.locationinfo.view

import com.arturkowalczyk300.locationinfo.R
import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun MapToolbar(
    buttonCurrentLocationOnClickListener: ()->Unit,
    buttonOpenInMapsOnClickListener: ()->Unit,
) {
    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 5.dp, end = 5.dp)
    ) {
        Button(
            shape = RoundedCornerShape(30),
            colors = ButtonDefaults.buttonColors(backgroundColor = Color.Gray),
            onClick = buttonCurrentLocationOnClickListener
        ) {
            Row(modifier = Modifier, verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(R.drawable.ic_current_location),
                    contentDescription = "Current location button"
                )
                    Text(text = "Current location")
            }
        }

        Button(
            shape = RoundedCornerShape(30),
            colors = ButtonDefaults.buttonColors(backgroundColor = Color.Gray),
            onClick = buttonOpenInMapsOnClickListener
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(R.drawable.ic_maps_app),
                    contentDescription = "Open in Maps button"
                )
                    Text(text = "Open in Maps")
            }
        }

    }
}

@Preview
@Composable
fun MapToolbarPreview() {
    MapToolbar({},{})
}