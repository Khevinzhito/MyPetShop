package com.example.mypetshop.ui.shop

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mypetshop.data.session.ShopSession
import com.example.mypetshop.domain.model.GeoLocation
import com.example.mypetshop.domain.repository.LocationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CartUiState(
    val deliveryLocation: GeoLocation? = null,
    val isLoadingLocation: Boolean = false,
    val locationErrorKey: String? = null,
    val checkoutBlocked: Boolean = false,
    val showCheckoutDialog: Boolean = false,
    val paymentMethod: String = "Tarjeta",
    val distanceKm: Int = 3,
    val paymentErrorKey: String? = null,
) {
    val shippingCents: Long get() = distanceKm * 2000L * 100L
    fun totalWithShipping(subtotal: Long): Long = subtotal + shippingCents
}

@HiltViewModel
class CartViewModel @Inject constructor(
    private val shopSession: ShopSession,
    private val locationRepository: LocationRepository,
) : ViewModel() {

    val cartItems = shopSession.cartItems
    val totalCents: Long get() = shopSession.cartTotalCents

    private val _uiState = MutableStateFlow(CartUiState())
    val uiState: StateFlow<CartUiState> = _uiState.asStateFlow()

    fun updateQuantity(productId: String, quantity: Int) =
        shopSession.updateQuantity(productId, quantity)

    fun removeFromCart(productId: String) = shopSession.removeFromCart(productId)

    fun captureDeliveryLocation() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingLocation = true, locationErrorKey = null) }
            locationRepository.getCurrentLocation()
                .onSuccess { location ->
                    _uiState.update {
                        it.copy(
                            deliveryLocation = location,
                            isLoadingLocation = false,
                            locationErrorKey = null,
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

    fun checkout(): Boolean {
        val location = _uiState.value.deliveryLocation
        if (location == null) {
            _uiState.update { it.copy(checkoutBlocked = true, locationErrorKey = "error_location_required") }
            return false
        }
        _uiState.update { it.copy(showCheckoutDialog = true, locationErrorKey = null, checkoutBlocked = false) }
        return false
    }

    fun closeCheckoutDialog() {
        _uiState.update { it.copy(showCheckoutDialog = false, paymentErrorKey = null) }
    }

    fun setPaymentMethod(method: String) {
        _uiState.update { it.copy(paymentMethod = method, paymentErrorKey = null) }
    }

    fun setDistanceKm(km: Int) {
        _uiState.update { it.copy(distanceKm = km.coerceAtLeast(1)) }
    }

    fun confirmPaymentAndCheckout(): Boolean {
        val state = _uiState.value
        val location = state.deliveryLocation ?: return false
        if (state.paymentMethod.isBlank()) {
            _uiState.update { it.copy(paymentErrorKey = "error_payment_method") }
            return false
        }
        shopSession.checkout(
            deliveryLocation = location,
            paymentMethod = state.paymentMethod,
            shippingCents = state.shippingCents,
        )
        _uiState.value = CartUiState()
        return true
    }
}
