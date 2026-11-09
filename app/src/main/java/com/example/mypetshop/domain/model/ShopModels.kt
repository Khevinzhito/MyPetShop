package com.example.mypetshop.domain.model

enum class ProductCategory {
    PREMIUM,
    STANDARD,
    SNACK,
}

enum class ProductPetType {
    DOG,
    CAT,
    BOTH,
}

data class ShopProduct(
    val id: String,
    val name: String,
    val description: String,
    val priceCents: Long,
    val brand: String,
    val category: ProductCategory = ProductCategory.SNACK,
    val petType: ProductPetType = ProductPetType.BOTH,
    val imageUri: String? = null,
    val isActive: Boolean = true,
)

data class CartItem(
    val product: ShopProduct,
    val quantity: Int,
)

enum class UserOrderStatus {
    PENDING,
    COMPLETED,
}

data class UserOrder(
    val id: String,
    val items: List<CartItem>,
    val totalCents: Long,
    val status: UserOrderStatus,
    val createdAtLabel: String,
    val clientEmail: String = "",
    val clientName: String = "",
    val deliveryAddress: String = "",
    val deliveryLatitude: Double = 0.0,
    val deliveryLongitude: Double = 0.0,
    val paymentMethod: String = "",
    val shippingCents: Long = 0L,
)

data class ClientMessage(
    val id: Long = 0,
    val clientEmail: String,
    val clientName: String,
    val orderId: String?,
    val subject: String,
    val body: String,
    val sentAtMillis: Long,
    val isFromAdmin: Boolean = true,
)
