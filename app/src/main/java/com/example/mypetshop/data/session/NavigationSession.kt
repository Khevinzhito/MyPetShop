package com.example.mypetshop.data.session

import android.content.Context
import com.example.mypetshop.domain.model.AuthUser
import com.example.mypetshop.domain.model.DriverProfile
import com.example.mypetshop.domain.model.PetProfile
import dagger.hilt.android.qualifiers.ApplicationContext
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NavigationSession @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val prefs = context.getSharedPreferences("mypetshop_session_prefs", Context.MODE_PRIVATE)

    var currentUser: AuthUser? = null
        set(value) {
            field = value
            loadDriverForCurrentUser()
        }

    var currentProfile: PetProfile? = null

    var currentDriver: DriverProfile? = null
        set(value) {
            field = value
            saveDriverForCurrentUser(value)
        }

    fun updateDriverLocation(location: String, latitude: Double = 0.0, longitude: Double = 0.0) {
        currentDriver = currentDriver?.copy(
            assignedLocation = location,
            latitude = latitude,
            longitude = longitude,
        )
    }

    fun clearSession() {
        currentUser = null
        currentProfile = null
        currentDriver = null
    }

    private fun saveDriverForCurrentUser(profile: DriverProfile?) {
        val email = currentUser?.email?.trim()?.lowercase() ?: return
        val editor = prefs.edit()
        if (profile == null) {
            editor.remove("driver_$email")
        } else {
            val json = JSONObject().apply {
                put("fullName", profile.fullName)
                put("cedula", profile.cedula)
                put("phone", profile.phone)
                put("vehicleType", profile.vehicleType)
                put("vehiclePlate", profile.vehiclePlate)
                put("vehicleBrand", profile.vehicleBrand)
                put("vehicleColor", profile.vehicleColor)
                put("profilePhotoUri", profile.profilePhotoUri ?: "")
                put("assignedLocation", profile.assignedLocation)
                put("latitude", profile.latitude)
                put("longitude", profile.longitude)
                put("biometricEnabled", profile.biometricEnabled)
            }
            editor.putString("driver_$email", json.toString())
        }
        editor.apply()
    }

    private fun loadDriverForCurrentUser() {
        val email = currentUser?.email?.trim()?.lowercase() ?: run {
            currentDriver = null
            return
        }
        val jsonStr = prefs.getString("driver_$email", null)
        if (jsonStr.isNullOrBlank()) {
            currentDriver = null
            return
        }
        try {
            val obj = JSONObject(jsonStr)
            currentDriver = DriverProfile(
                fullName = obj.optString("fullName"),
                cedula = obj.optString("cedula"),
                phone = obj.optString("phone"),
                vehicleType = obj.optString("vehicleType"),
                vehiclePlate = obj.optString("vehiclePlate"),
                vehicleBrand = obj.optString("vehicleBrand"),
                vehicleColor = obj.optString("vehicleColor"),
                profilePhotoUri = obj.optString("profilePhotoUri").ifBlank { null },
                assignedLocation = obj.optString("assignedLocation"),
                latitude = obj.optDouble("latitude", 0.0),
                longitude = obj.optDouble("longitude", 0.0),
                biometricEnabled = obj.optBoolean("biometricEnabled", false),
            )
        } catch (_: Exception) {
            currentDriver = null
        }
    }
}
