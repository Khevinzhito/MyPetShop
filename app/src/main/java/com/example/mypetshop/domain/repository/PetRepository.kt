package com.example.mypetshop.domain.repository

import com.example.mypetshop.domain.model.PetProfile
import kotlinx.coroutines.flow.Flow

interface PetRepository {
    fun observeLatestProfile(): Flow<PetProfile?>
    suspend fun saveProfile(profile: PetProfile)
}
