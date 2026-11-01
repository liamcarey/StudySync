
package com.example.studysync

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat

@Composable
fun CreateGroupScreen(
    onCreateGroup: (StudyGroup) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current

    // Form fields
    var course by remember { mutableStateOf("") }
    var title by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("") }

    // GPS coordinates
    var latitude by remember { mutableStateOf<Double?>(null) }
    var longitude by remember { mutableStateOf<Double?>(null) }

    var locationLoading by remember { mutableStateOf(false) }
    var locationMessage by remember {
        mutableStateOf("Location not detected")
    }

    // Check location permissions
    fun hasLocationPermission(): Boolean {
        val finePermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val coarsePermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        return finePermission || coarsePermission
    }

    // Retrieve GPS coordinates
    fun fetchLocation() {
        if (!hasLocationPermission()) {
            locationMessage = "Location permission required"
            return
        }

        locationLoading = true
        locationMessage = "Detecting location..."

        LocationHelper.getCurrentLocation(context) { result ->
            locationLoading = false

            if (result != null) {
                latitude = result.latitude
                longitude = result.longitude
                locationMessage = "Location detected successfully"
            } else {
                latitude = null
                longitude = null
                locationMessage =
                    "Unable to detect location. Try again."
            }
        }
    }

    // Request permissions
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.values.any { it }) {
            fetchLocation()
        } else {
            locationMessage = "Location permission denied"
        }
    }

    val canCreateGroup =
        course.isNotBlank() &&
                title.isNotBlank() &&
                location.isNotBlank() &&
                time.isNotBlank() &&
                latitude != null &&
                longitude != null &&
                !locationLoading

    // Main screen with fixed bottom button
    Scaffold(
        bottomBar = {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 8.dp
            ) {
                Button(
                    onClick = {
                        val newGroup = StudyGroup(
                            course = course.trim(),
                            title = title.trim(),
                            location = location.trim(),
                            time = time.trim(),
                            members = 1,
                            latitude = latitude,
                            longitude = longitude
                        )

                        onCreateGroup(newGroup)
                    },
                    enabled = canCreateGroup,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = 24.dp,
                            vertical = 12.dp
                        )
                        .height(54.dp)
                ) {
                    Text("Create Study Group")
                }
            }
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Text(
                text = "Create Study Group",
                style = MaterialTheme.typography.headlineMedium
            )

            Text(
                text = "Organize a new study session",
                style = MaterialTheme.typography.bodyMedium
            )

            // Course code
            OutlinedTextField(
                value = course,
                onValueChange = { course = it },
                label = { Text("Course Code") },
                placeholder = { Text("e.g. SOFE 4640U") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Study group title
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Study Group Title") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Location name
            OutlinedTextField(
                value = location,
                onValueChange = { location = it },
                label = { Text("Location") },
                placeholder = {
                    Text("e.g. Ontario Tech Library")
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Date and time
            OutlinedTextField(
                value = time,
                onValueChange = { time = it },
                label = { Text("Date and Time") },
                placeholder = {
                    Text("e.g. Friday, 3:00 PM")
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            HorizontalDivider()

            Text(
                text = "Study Group Location",
                style = MaterialTheme.typography.titleMedium
            )

            // Detect GPS location
            Button(
                onClick = {
                    if (hasLocationPermission()) {
                        fetchLocation()
                    } else {
                        permissionLauncher.launch(
                            arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            )
                        )
                    }
                },
                enabled = !locationLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    if (locationLoading)
                        "Detecting Location..."
                    else
                        "Detect My Location"
                )
            }

            if (locationLoading) {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Location status
            Text(
                text = locationMessage,
                style = MaterialTheme.typography.bodyMedium,
                color = if (latitude != null)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Display detected coordinates
            if (latitude != null && longitude != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor =
                            MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement =
                            Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "GPS Location Detected",
                            style =
                                MaterialTheme.typography.titleSmall
                        )

                        Text("Latitude: $latitude")
                        Text("Longitude: $longitude")
                    }
                }
            }

            // Cancel button
            OutlinedButton(
                onClick = onBack,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Cancel")
            }
        }
    }
}
