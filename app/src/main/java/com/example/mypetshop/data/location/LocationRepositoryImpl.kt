package com.example.mypetshop.data.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import com.example.mypetshop.domain.model.GeoLocation
import com.example.mypetshop.domain.repository.LocationRepository
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

@Singleton
class LocationRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
) : LocationRepository {

    private val fusedClient = LocationServices.getFusedLocationProviderClient(context)

    @SuppressLint("MissingPermission")
    override suspend fun getCurrentLocation(): Result<GeoLocation> = runCatching {
        val location = suspendCancellableCoroutine { continuation ->
            val tokenSource = CancellationTokenSource()
            fusedClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, tokenSource.token)
                .addOnSuccessListener { loc ->
                    if (loc != null) continuation.resume(loc)
                    else continuation.cancel(IllegalStateException("location_null"))
                }
                .addOnFailureListener { continuation.cancel(it) }
            continuation.invokeOnCancellation { tokenSource.cancel() }
        }
        val address = resolveAddress(location.latitude, location.longitude)
        GeoLocation(
            latitude = location.latitude,
            longitude = location.longitude,
            formattedAddress = address,
        )
    }

    @Suppress("DEPRECATION")
    private suspend fun resolveAddress(latitude: Double, longitude: Double): String =
        withContext(Dispatchers.IO) {
            if (!Geocoder.isPresent()) return@withContext formatCoords(latitude, longitude)
            try {
                val geocoder = Geocoder(context, Locale("es", "CO"))
                val addresses = geocoder.getFromLocation(latitude, longitude, 1)
                addresses?.firstOrNull()?.let { a ->
                    listOfNotNull(a.thoroughfare, a.subLocality, a.locality, a.adminArea)
                        .filter { it.isNotBlank() }
                        .joinToString(", ")
                } ?: formatCoords(latitude, longitude)
            } catch (_: Exception) {
                formatCoords(latitude, longitude)
            }
        }

    private fun formatCoords(latitude: Double, longitude: Double) =
        "Lat: %.5f, Lng: %.5f".format(latitude, longitude)
}
