package com.example.mypetshop.domain.model

enum class DeliveryStatus {
    PENDING,
    IN_TRANSIT,
    DELIVERED,
}

data class DeliveryOrder(
    val id: String,
    val clientPhoneMasked: String,
    val productName: String,
    val clientAddress: String,
    val driverAddress: String,
    val confirmByTime: String,
    val status: DeliveryStatus = DeliveryStatus.PENDING,
    val totalCents: Long = 0L,
    val paymentMethod: String = "Tarjeta",
)
