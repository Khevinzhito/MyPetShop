package com.example.mypetshop.ui.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mypetshop.data.session.ShopSession
import com.example.mypetshop.domain.model.SalesSummary
import com.example.mypetshop.domain.model.UserOrder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdminSalesViewModel @Inject constructor(
    private val shopSession: ShopSession,
) : ViewModel() {

    val summary: StateFlow<SalesSummary> = shopSession.orders
        .map { shopSession.computeSalesSummary(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), SalesSummary())

    val orders: StateFlow<List<UserOrder>> = shopSession.orders
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    init {
        viewModelScope.launch { shopSession.seedDemoSalesIfEmpty() }
    }
}
