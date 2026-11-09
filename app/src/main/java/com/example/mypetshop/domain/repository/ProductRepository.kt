package com.example.mypetshop.domain.repository

import com.example.mypetshop.domain.model.ShopProduct
import kotlinx.coroutines.flow.Flow

interface ProductRepository {
    fun observeActiveProducts(): Flow<List<ShopProduct>>
    fun observeAllProducts(): Flow<List<ShopProduct>>
    suspend fun upsert(product: ShopProduct)
    suspend fun delete(productId: String)
    suspend fun seedIfEmpty()
}
