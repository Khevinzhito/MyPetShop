package com.example.mypetshop.ui.shop

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mypetshop.data.session.ShopSession
import com.example.mypetshop.domain.model.ShopProduct
import com.example.mypetshop.domain.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ShopViewModel @Inject constructor(
    private val shopSession: ShopSession,
    productRepository: ProductRepository,
) : ViewModel() {

    val products: StateFlow<List<ShopProduct>> = productRepository
        .observeActiveProducts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val cartItems = shopSession.cartItems

    init {
        viewModelScope.launch {
            productRepository.seedIfEmpty()
        }
    }

    fun addToCart(product: ShopProduct) = shopSession.addToCart(product)
}
