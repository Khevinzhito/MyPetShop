package com.example.mypetshop.data.seed

import com.example.mypetshop.domain.repository.ProductRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CatalogInitializer @Inject constructor(
    private val productRepository: ProductRepository,
) {
    suspend fun seedIfEmpty() = productRepository.seedIfEmpty()
}
