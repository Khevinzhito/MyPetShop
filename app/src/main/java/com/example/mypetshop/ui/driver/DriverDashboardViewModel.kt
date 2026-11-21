package com.example.mypetshop.ui.driver

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mypetshop.data.demo.DeliveryOrderGenerator
import com.example.mypetshop.data.session.ShopSession
import com.example.mypetshop.domain.model.DeliveryOrder
import com.example.mypetshop.domain.model.DeliveryStatus
import com.example.mypetshop.domain.model.UserOrderStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

data class DriverDashboardUiState(
    val orders: List<DeliveryOrder> = emptyList(),
    val selectedOrderId: String? = null,
    val showMessageConfirmDialog: Boolean = false,
    val messageCode: String = "",
    val messageCodeInput: String = "",
    val confirmationMessage: String? = null,
    val confirmationError: String? = null,
)

@HiltViewModel
class DriverDashboardViewModel @Inject constructor(
    private val shopSession: ShopSession,
) : ViewModel() {

    private val _uiState = MutableStateFlow(DriverDashboardUiState())
    val uiState: StateFlow<DriverDashboardUiState> = _uiState.asStateFlow()

    init {
        shopSession.seedDemoSalesIfEmpty()
        viewModelScope.launch {
            shopSession.orders.collect { userOrders ->
                val deliveryOrders = userOrders.map { order ->
                    val productName = order.items.joinToString(separator = ", ") { "${it.product.name} (x${it.quantity})" }
                    val status = if (order.status == UserOrderStatus.COMPLETED) DeliveryStatus.DELIVERED else DeliveryStatus.PENDING
                    DeliveryOrder(
                        id = order.id,
                        clientPhoneMasked = "*** *** 4567",
                        productName = productName.ifBlank { "Producto MyPetShop" },
                        clientAddress = order.deliveryAddress.ifBlank { "Bogotá, Colombia" },
                        driverAddress = "Centro de despacho Logus, Bogotá",
                        confirmByTime = order.createdAtLabel,
                        status = status,
                        totalCents = order.totalCents,
                        paymentMethod = order.paymentMethod.ifBlank { "Tarjeta" },
                    )
                }
                val finalOrders = if (deliveryOrders.isNotEmpty()) deliveryOrders else DeliveryOrderGenerator.generate()
                _uiState.update { state ->
                    val selectedId = state.selectedOrderId?.let { id -> if (finalOrders.any { it.id == id }) id else finalOrders.firstOrNull()?.id }
                        ?: finalOrders.firstOrNull()?.id
                    state.copy(orders = finalOrders, selectedOrderId = selectedId)
                }
            }
        }
    }

    fun selectOrder(orderId: String) {
        _uiState.update { it.copy(selectedOrderId = orderId, confirmationMessage = null, confirmationError = null) }
    }

    fun showMessageConfirmation() {
        val code = Random.nextInt(1000, 9999).toString()
        _uiState.update {
            it.copy(
                showMessageConfirmDialog = true,
                messageCode = code,
                messageCodeInput = "",
                confirmationError = null,
            )
        }
    }

    fun onMessageCodeInputChange(value: String) {
        _uiState.update { it.copy(messageCodeInput = value.filter(Char::isDigit).take(4)) }
    }

    fun confirmByMessage() {
        val state = _uiState.value
        if (state.messageCodeInput == state.messageCode) {
            markSelectedDelivered()
            _uiState.update {
                it.copy(
                    showMessageConfirmDialog = false,
                    confirmationMessage = "delivery_confirmed_message",
                    confirmationError = null,
                )
            }
        } else {
            _uiState.update { it.copy(confirmationError = "error_message_code") }
        }
    }

    fun dismissMessageDialog() {
        _uiState.update { it.copy(showMessageConfirmDialog = false, confirmationError = null) }
    }

    fun confirmByBiometric() {
        markSelectedDelivered()
        _uiState.update {
            it.copy(
                confirmationMessage = "delivery_confirmed_biometric",
                confirmationError = null,
            )
        }
    }

    private fun markSelectedDelivered() {
        val selectedId = _uiState.value.selectedOrderId ?: return
        shopSession.completeOrder(selectedId)
    }

    val selectedOrder: DeliveryOrder?
        get() = _uiState.value.orders.find { it.id == _uiState.value.selectedOrderId }
}
