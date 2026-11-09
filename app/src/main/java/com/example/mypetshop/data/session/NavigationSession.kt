package com.example.mypetshop.data.session

import com.example.mypetshop.domain.model.AuthUser
import com.example.mypetshop.domain.model.DriverProfile
import com.example.mypetshop.domain.model.PetProfile
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NavigationSession @Inject constructor() {
    var currentUser: AuthUser? = null
    var currentProfile: PetProfile? = null
    var currentDriver: DriverProfile? = null

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
}
