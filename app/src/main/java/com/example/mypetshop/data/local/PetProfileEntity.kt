package com.example.mypetshop.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pet_profiles")
data class PetProfileEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val petType: String,
    val ageYears: Int,
    val isSterilized: Boolean,
    val hasIllness: Boolean,
    val illnessDescription: String,
    val currentFood: String,
    val breed: String,
    val gender: String,
    val wantsAdvanceTransition: Boolean,
)
