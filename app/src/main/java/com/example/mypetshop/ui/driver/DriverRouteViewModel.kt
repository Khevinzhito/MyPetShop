package com.example.mypetshop.ui.driver

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mypetshop.data.session.NavigationSession
import com.example.mypetshop.domain.repository.LocationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DriverRouteUiState(
    val isLoadingLocation: Boolean = false,
    val locationErrorKey: String? = null,
)

@HiltViewModel
class DriverRouteViewModel @Inject constructor(
    private val locationRepository: LocationRepository,
    private val navigationSession: NavigationSession,
) : ViewModel() {

    private val _uiState = MutableStateFlow(DriverRouteUiState())
    val uiState: StateFlow<DriverRouteUiState> = _uiState.asStateFlow()

    fun refreshLocation(onUpdated: (String, Double, Double) -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingLocation = true, locationErrorKey = null) }
            locationRepository.getCurrentLocation()
                .onSuccess { location ->
                    navigationSession.updateDriverLocation(
                        location.formattedAddress,
                        location.latitude,
                        location.longitude,
                    )
                    onUpdated(location.formattedAddress, location.latitude, location.longitude)
                    _uiState.update { it.copy(isLoadingLocation = false) }
                }
                .onFailure {
                    _uiState.update {
                        it.copy(isLoadingLocation = false, locationErrorKey = "error_location_failed")
                    }
                }
        }
    }

    fun onLocationPermissionDenied() {
        _uiState.update { it.copy(locationErrorKey = "error_location_permission") }
    }
}
