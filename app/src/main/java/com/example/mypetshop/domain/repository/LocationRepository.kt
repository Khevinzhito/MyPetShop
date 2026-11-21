package com.example.mypetshop.domain.repository

import com.example.mypetshop.domain.model.GeoLocation

interface LocationRepository {
    suspend fun getCurrentLocation(): Result<GeoLocation>
}
