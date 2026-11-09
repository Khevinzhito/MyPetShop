package com.example.mypetshop.domain.model

data class DriverProfile(
    val fullName: String,
    val cedula: String,
    val phone: String,
    val vehicleType: String,
    val vehiclePlate: String,
    val vehicleBrand: String,
    val vehicleColor: String,
    val profilePhotoUri: String? = null,
    val assignedLocation: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val biometricEnabled: Boolean = false,
)
