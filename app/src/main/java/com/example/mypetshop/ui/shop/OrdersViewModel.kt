package com.example.mypetshop.ui.shop

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mypetshop.data.session.NavigationSession
import com.example.mypetshop.data.session.ShopSession
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class OrdersViewModel @Inject constructor(
    private val shopSession: ShopSession,
    private val navigationSession: NavigationSession,
) : ViewModel() {

    val orders = shopSession.orders
        .map { allOrders ->
            val email = navigationSession.currentUser?.email?.trim()?.lowercase().orEmpty()
            if (email.isBlank()) allOrders else allOrders.filter { it.clientEmail.trim().lowercase() == email }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun completeOrder(orderId: String) = shopSession.completeOrder(orderId)
}
