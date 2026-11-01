
package com.example.studysync

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import android.content.pm.PackageManager

@Composable
fun LocationTestScreen() {

    val context = LocalContext.current

    var locationText by remember {
        mutableStateOf("Location not retrieved yet")
    }

    var isLoading by remember {
        mutableStateOf(false)
    }

    fun retrieveLocation() {
        val permissionGranted =
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED

        if (!permissionGranted) {
            locationText = "Location permission not granted"
            return
        }

        isLoading = true

        LocationHelper.getCurrentLocation(context) { location ->
            isLoading = false

            locationText = if (location != null) {
                "Latitude: ${location.latitude}\n" +
                        "Longitude: ${location.longitude}"
            } else {
                "Unable to retrieve location. Check emulator GPS settings."
            }
        }
    }

    val permissionLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->

            if (permissions.values.any { it }) {
                retrieveLocation()
            } else {
                locationText = "Location permission denied"
            }
        }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            "StudySync GPS Test",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(Modifier.height(24.dp))

        Button(
            onClick = {
                permissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }
        ) {
            Text("Get My Location")
        }

        Spacer(Modifier.height(24.dp))

        if (isLoading) {
            CircularProgressIndicator()
        }

        Text(locationText)
    }
}
