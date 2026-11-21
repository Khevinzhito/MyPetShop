package com.example.mypetshop.ui.driver

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mypetshop.domain.model.DriverProfile
import com.example.mypetshop.domain.repository.LocationRepository
import com.example.mypetshop.util.AuthValidator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DriverRegisterUiState(
    val fullName: String = "",
    val cedula: String = "",
    val phone: String = "",
    val vehicleType: String = "",
    val vehiclePlate: String = "",
    val vehicleBrand: String = "",
    val vehicleColor: String = "",
    val profilePhotoUri: String? = null,
    val assignedLocation: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val profilePhotoErrorKey: String? = null,
    val biometricEnabled: Boolean = false,
    val fullNameErrorKey: String? = null,
    val cedulaErrorKey: String? = null,
    val phoneErrorKey: String? = null,
    val vehicleTypeErrorKey: String? = null,
    val vehiclePlateErrorKey: String? = null,
    val vehicleBrandErrorKey: String? = null,
    val vehicleColorErrorKey: String? = null,
    val locationErrorKey: String? = null,
    val isLoadingLocation: Boolean = false,
    val registrationComplete: Boolean = false,
)

@HiltViewModel
class DriverRegisterViewModel @Inject constructor(
    private val locationRepository: LocationRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(DriverRegisterUiState())
    val uiState: StateFlow<DriverRegisterUiState> = _uiState.asStateFlow()

    fun onFullNameChange(value: String) =
        _uiState.update { it.copy(fullName = value, fullNameErrorKey = null) }

    fun onCedulaChange(value: String) =
        _uiState.update { it.copy(cedula = value.filter(Char::isDigit), cedulaErrorKey = null) }

    fun onPhoneChange(value: String) =
        _uiState.update { it.copy(phone = value.filter(Char::isDigit), phoneErrorKey = null) }

    fun onVehicleTypeChange(value: String) =
        _uiState.update { it.copy(vehicleType = value, vehicleTypeErrorKey = null) }

    fun onVehiclePlateChange(value: String) =
        _uiState.update { it.copy(vehiclePlate = value.uppercase(), vehiclePlateErrorKey = null) }

    fun onVehicleBrandChange(value: String) =
        _uiState.update { it.copy(vehicleBrand = value, vehicleBrandErrorKey = null) }

    fun onVehicleColorChange(value: String) =
        _uiState.update { it.copy(vehicleColor = value, vehicleColorErrorKey = null) }

    fun onProfilePhotoSelected(uri: String?) =
        _uiState.update { it.copy(profilePhotoUri = uri, profilePhotoErrorKey = null) }

    fun onCaptureLocation() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingLocation = true, locationErrorKey = null) }
            locationRepository.getCurrentLocation()
                .onSuccess { location ->
                    _uiState.update {
                        it.copy(
                            assignedLocation = location.formattedAddress,
                            latitude = location.latitude,
                            longitude = location.longitude,
                            isLoadingLocation = false,
                        )
                    }
                }
                .onFailure {
                    _uiState.update {
                        it.copy(
                            isLoadingLocation = false,
                            locationErrorKey = "error_location_failed",
                        )
                    }
                }
        }
    }

    fun onLocationPermissionDenied() {
        _uiState.update { it.copy(locationErrorKey = "error_location_permission") }
    }

    fun onBiometricEnabledChange(enabled: Boolean) =
        _uiState.update { it.copy(biometricEnabled = enabled) }

    fun onRegisterClick() {
        val state = _uiState.value
        val fullNameError = if (state.fullName.trim().length < 3) "error_full_name" else null
        val cedulaError = when {
            state.cedula.length < 6 -> "error_cedula"
            else -> null
        }
        val phoneError = AuthValidator.validatePhone(state.phone) ?: if (state.phone.isBlank()) {
            "error_phone_required"
        } else {
            null
        }
        val vehicleTypeError = if (state.vehicleType.isBlank()) "error_vehicle_type" else null
        val vehiclePlateError = if (state.vehiclePlate.length < 5) "error_vehicle_plate" else null
        val vehicleBrandError = if (state.vehicleBrand.isBlank()) "error_vehicle_brand" else null
        val vehicleColorError = if (state.vehicleColor.isBlank()) "error_vehicle_color" else null
        val photoError = if (state.profilePhotoUri.isNullOrBlank()) "error_profile_photo" else null
        val locationError = if (state.assignedLocation.isBlank()) "error_location_required" else null

        if (listOf(
                fullNameError,
                cedulaError,
                phoneError,
                vehicleTypeError,
                vehiclePlateError,
                vehicleBrandError,
                vehicleColorError,
                photoError,
                locationError,
            ).any { it != null }
        ) {
            _uiState.update {
                it.copy(
                    fullNameErrorKey = fullNameError,
                    cedulaErrorKey = cedulaError,
                    phoneErrorKey = phoneError,
                    vehicleTypeErrorKey = vehicleTypeError,
                    vehiclePlateErrorKey = vehiclePlateError,
                    vehicleBrandErrorKey = vehicleBrandError,
                    vehicleColorErrorKey = vehicleColorError,
                    profilePhotoErrorKey = photoError,
                    locationErrorKey = locationError,
                )
            }
            return
        }

        _uiState.update { it.copy(registrationComplete = true) }
    }

    fun buildProfile(): DriverProfile {
        val state = _uiState.value
        return DriverProfile(
            fullName = state.fullName.trim(),
            cedula = state.cedula,
            phone = state.phone,
            vehicleType = state.vehicleType,
            vehiclePlate = state.vehiclePlate,
            vehicleBrand = state.vehicleBrand,
            vehicleColor = state.vehicleColor,
            profilePhotoUri = state.profilePhotoUri,
            assignedLocation = state.assignedLocation,
            latitude = state.latitude,
            longitude = state.longitude,
            biometricEnabled = state.biometricEnabled,
        )
    }

    fun consumeRegistrationComplete() {
        _uiState.update { it.copy(registrationComplete = false) }
    }
}
