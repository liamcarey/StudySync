
package com.example.studysync

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem
import com.google.maps.android.compose.*
import com.google.maps.android.compose.clustering.Clustering

// One map item can represent multiple groups at one location

data class StudyLocationItem(
    val groups: List<StudyGroup>,
    val coordinates: LatLng
) : ClusterItem {

    override val position: LatLng
        get() = coordinates

    override val title: String?
        get() = if (groups.size == 1) {
            groups.first().title
        } else {
            "${groups.size} Study Groups"
        }

    override val snippet: String?
        get() = if (groups.size == 1) {
            groups.first().course
        } else {
            "Tap to view all study groups"
        }

    override val zIndex: Float?
        get() = 0f
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudyMapScreen(
    studyGroups: List<StudyGroup>,
    userLatitude: Double? = null,
    userLongitude: Double? = null,
    onBack: () -> Unit
) {
    val context = LocalContext.current

    // Check location permission
    fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
    }

    var locationAllowed by remember {
        mutableStateOf(hasLocationPermission())
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) {
        locationAllowed = hasLocationPermission()
    }

    LaunchedEffect(Unit) {
        if (!locationAllowed) {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    // Ontario Tech University default position
    val campus = LatLng(43.9455, -78.8967)

    val initialPosition =
        if (userLatitude != null && userLongitude != null) {
            LatLng(userLatitude, userLongitude)
        } else {
            campus
        }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            initialPosition,
            15f
        )
    }

    // Combine groups at exactly identical coordinates
    val mapItems = remember(studyGroups) {

        studyGroups
            .filter {
                it.latitude != null &&
                        it.longitude != null &&
                        it.latitude in -90.0..90.0 &&
                        it.longitude in -180.0..180.0
            }
            .groupBy {
                Pair(it.latitude!!, it.longitude!!)
            }
            .map { (coordinates, groups) ->

                StudyLocationItem(
                    groups = groups,
                    coordinates = LatLng(
                        coordinates.first,
                        coordinates.second
                    )
                )
            }
    }

    var selectedGroups by remember {
        mutableStateOf<List<StudyGroup>>(emptyList())
    }

    var showGroupDialog by remember {
        mutableStateOf(false)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("StudySync Map")
                },
                navigationIcon = {
                    TextButton(onClick = onBack) {
                        Text("Back")
                    }
                }
            )
        }
    ) { paddingValues ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {

            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,

                // Native Google Maps blue dot
                properties = MapProperties(
                    isMyLocationEnabled = locationAllowed
                ),

                uiSettings = MapUiSettings(
                    myLocationButtonEnabled = locationAllowed,
                    zoomControlsEnabled = true,
                    compassEnabled = true,
                    mapToolbarEnabled = false
                )
            ) {

                Clustering(
                    items = mapItems,

                    onClusterClick = { cluster ->
                        // Default clustering behavior zooms in
                        false
                    },

                    onClusterItemClick = { item ->
                        selectedGroups = item.groups
                        showGroupDialog = true
                        true
                    }
                )
            }
        }
    }

    // Show groups when a location marker is tapped
    if (showGroupDialog) {

        AlertDialog(
            onDismissRequest = {
                showGroupDialog = false
            },

            title = {
                Text(
                    if (selectedGroups.size == 1) {
                        "Study Group Details"
                    } else {
                        "${selectedGroups.size} Study Groups Here"
                    }
                )
            },

            text = {

                LazyColumn(
                    verticalArrangement =
                        Arrangement.spacedBy(12.dp)
                ) {

                    items(selectedGroups) { group ->

                        Column(
                            verticalArrangement =
                                Arrangement.spacedBy(4.dp)
                        ) {

                            Text(
                                group.course,
                                style =
                                    MaterialTheme.typography.labelLarge
                            )

                            Text(
                                group.title,
                                style =
                                    MaterialTheme.typography.titleMedium
                            )

                            Text(
                                "Location: ${group.location}"
                            )

                            Text(
                                "Time: ${group.time}"
                            )

                            Text(
                                "Members: ${group.members}"
                            )

                            HorizontalDivider()
                        }
                    }
                }
            },

            confirmButton = {
                TextButton(
                    onClick = {
                        showGroupDialog = false
                    }
                ) {
                    Text("Close")
                }
            }
        )
    }
}
