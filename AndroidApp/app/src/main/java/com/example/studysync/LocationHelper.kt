
package com.example.studysync

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource

object LocationHelper {

    @SuppressLint("MissingPermission")
    fun getCurrentLocation(
        context: Context,
        onResult: (Location?) -> Unit
    ) {
        val fusedLocationClient =
            LocationServices.getFusedLocationProviderClient(context)

        try {
            fusedLocationClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                CancellationTokenSource().token
            )
                .addOnSuccessListener { location ->
                    onResult(location)
                }
                .addOnFailureListener { error ->
                    error.printStackTrace()
                    onResult(null)
                }

        } catch (e: SecurityException) {
            onResult(null)
        }
    }
}
