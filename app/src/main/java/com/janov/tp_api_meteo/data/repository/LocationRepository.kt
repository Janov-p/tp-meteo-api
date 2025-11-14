package com.janov.tp_api_meteo.data.repository

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.janov.tp_api_meteo.util.NetworkError
import com.janov.tp_api_meteo.util.Result
import kotlinx.coroutines.tasks.await

class LocationRepository(context: Context) {
    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)
    
    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocation(): Result<Pair<Double, Double>> {
        return try {
            val cancellationTokenSource = CancellationTokenSource()
            val location: Location = fusedLocationClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                cancellationTokenSource.token
            ).await()
            
            Result.Success(Pair(location.latitude, location.longitude))
        } catch (e: SecurityException) {
            Result.Error(
                NetworkError.Unknown("Permission refusée"),
                "Permission de localisation refusée"
            )
        } catch (e: Exception) {
            Result.Error(
                NetworkError.Unknown(e.message),
                "Erreur lors de la récupération de la position: ${e.message}"
            )
        }
    }
}
