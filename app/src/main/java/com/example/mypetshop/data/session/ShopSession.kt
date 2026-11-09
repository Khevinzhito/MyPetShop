package com.example.mypetshop.data.session

import com.example.mypetshop.data.demo.DemoAccounts
import com.example.mypetshop.data.seed.PetFoodSeedCatalog
import com.example.mypetshop.domain.model.CartItem
import com.example.mypetshop.domain.model.SalesSummary
import com.example.mypetshop.domain.model.ShopProduct
import com.example.mypetshop.domain.model.UserOrder
import com.example.mypetshop.domain.model.GeoLocation
import com.example.mypetshop.domain.model.UserOrderStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ShopSession @Inject constructor(
    private val navigationSession: NavigationSession,
) {

    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems.asStateFlow()

    private val _orders = MutableStateFlow<List<UserOrder>>(emptyList())
    val orders: StateFlow<List<UserOrder>> = _orders.asStateFlow()

    val cartItemCount: Int
        get() = _cartItems.value.sumOf { it.quantity }

    val cartTotalCents: Long
        get() = _cartItems.value.sumOf { it.product.priceCents * it.quantity }

    fun addToCart(product: ShopProduct) {
        _cartItems.update { items ->
            val existing = items.find { it.product.id == product.id }
            if (existing != null) {
                items.map {
                    if (it.product.id == product.id) it.copy(quantity = it.quantity + 1) else it
                }
            } else {
                items + CartItem(product, 1)
            }
        }
    }

    fun updateQuantity(productId: String, quantity: Int) {
        if (quantity <= 0) {
            removeFromCart(productId)
            return
        }
        _cartItems.update { items ->
            items.map { if (it.product.id == productId) it.copy(quantity = quantity) else it }
        }
    }

    fun removeFromCart(productId: String) {
        _cartItems.update { items -> items.filter { it.product.id != productId } }
    }

    fun checkout(
        deliveryLocation: GeoLocation? = null,
        paymentMethod: String = "Tarjeta",
        shippingCents: Long = 0L,
    ): UserOrder? {
        val items = _cartItems.value
        if (items.isEmpty()) return null
        val user = navigationSession.currentUser
        val order = UserOrder(
            id = "ORD-${System.currentTimeMillis() % 100000}",
            items = items,
            totalCents = cartTotalCents + shippingCents,
            status = UserOrderStatus.PENDING,
            createdAtLabel = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("es", "CO")).format(Date()),
            clientEmail = user?.email.orEmpty(),
            clientName = user?.displayName.orEmpty(),
            deliveryAddress = deliveryLocation?.formattedAddress.orEmpty(),
            deliveryLatitude = deliveryLocation?.latitude ?: 0.0,
            deliveryLongitude = deliveryLocation?.longitude ?: 0.0,
            paymentMethod = paymentMethod,
            shippingCents = shippingCents,
        )
        _orders.update { it + order }
        _cartItems.value = emptyList()
        return order
    }

    fun completeOrder(orderId: String) {
        _orders.update { orders ->
            orders.map { if (it.id == orderId) it.copy(status = UserOrderStatus.COMPLETED) else it }
        }
    }

    fun computeSalesSummary(orders: List<UserOrder> = _orders.value): SalesSummary {
        val completed = orders.filter { it.status == UserOrderStatus.COMPLETED }
        val pending = orders.filter { it.status == UserOrderStatus.PENDING }
        val completedTotal = completed.sumOf { it.totalCents }
        return SalesSummary(
            totalSalesCents = orders.sumOf { it.totalCents },
            completedSalesCents = completedTotal,
            pendingSalesCents = pending.sumOf { it.totalCents },
            totalOrders = orders.size,
            completedOrders = completed.size,
            pendingOrders = pending.size,
            estimatedProfitCents = completedTotal * PROFIT_MARGIN_PERCENT / 100,
        )
    }

    fun seedDemoSalesIfEmpty() {
        if (_orders.value.isNotEmpty()) return
        val products = PetFoodSeedCatalog.products
        val demoOrders = listOf(
            demoOrder("ORD-10001", products[0], 1, UserOrderStatus.COMPLETED, "15/05/2026 10:30"),
            demoOrder("ORD-10002", products[3], 2, UserOrderStatus.COMPLETED, "16/05/2026 14:15"),
            demoOrder("ORD-10003", products[16], 1, UserOrderStatus.COMPLETED, "17/05/2026 09:00"),
            demoOrder("ORD-10004", products[20], 1, UserOrderStatus.PENDING, "18/05/2026 11:45"),
        )
        _orders.value = demoOrders
    }

    private fun demoOrder(
        id: String,
        product: ShopProduct,
        quantity: Int,
        status: UserOrderStatus,
        dateLabel: String,
    ): UserOrder {
        val items = listOf(CartItem(product, quantity))
        return UserOrder(
            id = id,
            items = items,
            totalCents = product.priceCents * quantity,
            status = status,
            createdAtLabel = dateLabel,
            clientEmail = DemoAccounts.BUYER_EMAIL,
            clientName = DemoAccounts.BUYER_NAME,
        )
    }

    fun observeClientEmails(): List<Pair<String, String>> {
        val fromOrders = _orders.value
            .filter { it.clientEmail.isNotBlank() }
            .map { it.clientEmail to it.clientName }
        val defaults = listOf(
            DemoAccounts.BUYER_EMAIL to DemoAccounts.BUYER_NAME,
        )
        return (fromOrders + defaults).distinctBy { it.first }
    }

    companion object {
        private const val PROFIT_MARGIN_PERCENT = 22
    }
}
