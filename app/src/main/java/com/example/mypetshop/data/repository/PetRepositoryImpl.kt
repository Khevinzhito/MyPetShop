package com.example.mypetshop.data.repository

import com.example.mypetshop.data.local.PetProfileDao
import com.example.mypetshop.data.local.PetProfileEntity
import com.example.mypetshop.domain.model.PetGender
import com.example.mypetshop.domain.model.PetProfile
import com.example.mypetshop.domain.model.PetType
import com.example.mypetshop.domain.repository.PetRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PetRepositoryImpl @Inject constructor(
    private val petProfileDao: PetProfileDao,
) : PetRepository {

    override fun observeLatestProfile(): Flow<PetProfile?> =
        petProfileDao.observeLatest().map { it?.toDomain() }

    override suspend fun saveProfile(profile: PetProfile) {
        petProfileDao.insert(profile.toEntity())
    }

    private fun PetProfileEntity.toDomain() = PetProfile(
        id = id,
        petType = PetType.valueOf(petType),
        ageYears = ageYears,
        isSterilized = isSterilized,
        hasIllness = hasIllness,
        illnessDescription = illnessDescription,
        currentFood = currentFood,
        breed = breed,
        gender = PetGender.valueOf(gender),
        wantsAdvanceTransition = wantsAdvanceTransition,
    )

    private fun PetProfile.toEntity() = PetProfileEntity(
        id = id,
        petType = petType.name,
        ageYears = ageYears,
        isSterilized = isSterilized,
        hasIllness = hasIllness,
        illnessDescription = illnessDescription,
        currentFood = currentFood,
        breed = breed,
        gender = gender.name,
        wantsAdvanceTransition = wantsAdvanceTransition,
    )
}
