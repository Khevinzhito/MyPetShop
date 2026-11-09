package com.example.mypetshop.ui.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mypetshop.data.seed.AdvanceCatalog
import com.example.mypetshop.domain.model.PetGender
import com.example.mypetshop.domain.model.PetProfile
import com.example.mypetshop.domain.model.PetType
import com.example.mypetshop.domain.repository.PetRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PetOnboardingUiState(
    val petType: PetType? = null,
    val ageText: String = "",
    val isSterilized: Boolean = false,
    val hasIllness: Boolean = false,
    val illnessDescription: String = "",
    val currentFood: String = "",
    val breed: String = "",
    val gender: PetGender? = null,
    val wantsAdvanceTransition: Boolean = false,
    val breedExpanded: Boolean = false,
    val petTypeErrorKey: String? = null,
    val ageErrorKey: String? = null,
    val breedErrorKey: String? = null,
    val genderErrorKey: String? = null,
    val currentFoodErrorKey: String? = null,
    val illnessErrorKey: String? = null,
    val saveErrorKey: String? = null,
    val savedProfile: PetProfile? = null,
    val isSaving: Boolean = false,
) {
    val availableBreeds: List<String>
        get() = petType?.let { AdvanceCatalog.breedsFor(it) } ?: emptyList()
}

@HiltViewModel
class PetOnboardingViewModel @Inject constructor(
    private val petRepository: PetRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(PetOnboardingUiState())
    val uiState: StateFlow<PetOnboardingUiState> = _uiState.asStateFlow()

    fun onPetTypeSelected(type: PetType) {
        _uiState.update { it.copy(petType = type, breed = "", petTypeErrorKey = null, breedErrorKey = null) }
    }

    fun onAgeChange(value: String) {
        _uiState.update { it.copy(ageText = value.filter(Char::isDigit), ageErrorKey = null) }
    }

    fun onSterilizedChange(value: Boolean) = _uiState.update { it.copy(isSterilized = value) }

    fun onHasIllnessChange(value: Boolean) {
        _uiState.update {
            it.copy(
                hasIllness = value,
                illnessDescription = if (value) it.illnessDescription else "",
                illnessErrorKey = null,
            )
        }
    }

    fun onIllnessDescriptionChange(value: String) =
        _uiState.update { it.copy(illnessDescription = value, illnessErrorKey = null) }

    fun onCurrentFoodChange(value: String) =
        _uiState.update { it.copy(currentFood = value, currentFoodErrorKey = null) }

    fun onBreedChange(value: String) =
        _uiState.update { it.copy(breed = value, breedErrorKey = null, breedExpanded = false) }

    fun onBreedExpandedChange(expanded: Boolean) = _uiState.update { it.copy(breedExpanded = expanded) }

    fun onGenderSelected(gender: PetGender) =
        _uiState.update { it.copy(gender = gender, genderErrorKey = null) }

    fun onAdvanceTransitionChange(value: Boolean) =
        _uiState.update { it.copy(wantsAdvanceTransition = value) }

    fun onContinue() {
        val state = _uiState.value
        val petTypeError = if (state.petType == null) "error_pet_type" else null
        val age = state.ageText.toIntOrNull()
        val ageError = when {
            state.ageText.isBlank() -> "error_age_empty"
            age == null || age !in 0..30 -> "error_age_invalid"
            else -> null
        }
        val breedError = if (state.breed.isBlank()) "error_breed" else null
        val genderError = if (state.gender == null) "error_gender" else null
        val foodError = if (state.currentFood.isBlank()) "error_current_food" else null
        val illnessError = if (state.hasIllness && state.illnessDescription.trim().length < 3) {
            "error_illness_description"
        } else {
            null
        }

        if (listOf(petTypeError, ageError, breedError, genderError, foodError, illnessError).any { it != null }) {
            _uiState.update {
                it.copy(
                    petTypeErrorKey = petTypeError,
                    ageErrorKey = ageError,
                    breedErrorKey = breedError,
                    genderErrorKey = genderError,
                    currentFoodErrorKey = foodError,
                    illnessErrorKey = illnessError,
                )
            }
            return
        }

        val profile = PetProfile(
            petType = state.petType!!,
            ageYears = age!!,
            isSterilized = state.isSterilized,
            hasIllness = state.hasIllness,
            illnessDescription = state.illnessDescription.trim(),
            currentFood = state.currentFood.trim(),
            breed = state.breed,
            gender = state.gender!!,
            wantsAdvanceTransition = state.wantsAdvanceTransition,
        )

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, saveErrorKey = null) }
            try {
                petRepository.saveProfile(profile)
                _uiState.update { it.copy(savedProfile = profile) }
            } catch (_: Exception) {
                _uiState.update { it.copy(saveErrorKey = "error_save_profile") }
            } finally {
                _uiState.update { it.copy(isSaving = false) }
            }
        }
    }

    fun consumeSavedProfile() {
        _uiState.update { it.copy(savedProfile = null) }
    }
}
