package com.example.mypetshop.domain.model

data class GeoLocation(
    val latitude: Double,
    val longitude: Double,
    val formattedAddress: String,
)
