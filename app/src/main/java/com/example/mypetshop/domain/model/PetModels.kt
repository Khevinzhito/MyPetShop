package com.example.mypetshop.domain.model

enum class PetType(val labelKey: String) {
    DOG("pet_type_dog"),
    CAT("pet_type_cat"),
}

enum class PetGender(val labelKey: String) {
    MALE("pet_gender_male"),
    FEMALE("pet_gender_female"),
}

data class PetProfile(
    val id: Long = 0L,
    val petType: PetType,
    val ageYears: Int,
    val isSterilized: Boolean,
    val hasIllness: Boolean,
    val illnessDescription: String,
    val currentFood: String,
    val breed: String,
    val gender: PetGender,
    val wantsAdvanceTransition: Boolean,
)

data class AdvanceRecipe(
    val id: String,
    val name: String,
    val petType: PetType,
    val imageDescription: String,
    val characteristics: List<String>,
    val suitableFor: String,
)
