package com.example.mypetshop.data.repository

import com.example.mypetshop.data.local.ProductDao
import com.example.mypetshop.data.local.toDomain
import com.example.mypetshop.data.local.toEntity
import com.example.mypetshop.data.seed.PetFoodSeedCatalog
import com.example.mypetshop.domain.model.ShopProduct
import com.example.mypetshop.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProductRepositoryImpl @Inject constructor(
    private val productDao: ProductDao,
) : ProductRepository {

    override fun observeActiveProducts(): Flow<List<ShopProduct>> =
        productDao.observeActive().map { list -> list.map { it.toDomain() } }

    override fun observeAllProducts(): Flow<List<ShopProduct>> =
        productDao.observeAll().map { list -> list.map { it.toDomain() } }

    override suspend fun upsert(product: ShopProduct) {
        productDao.upsert(product.toEntity())
    }

    override suspend fun delete(productId: String) {
        productDao.deleteById(productId)
    }

    override suspend fun seedIfEmpty() {
        if (productDao.count() == 0) {
            productDao.insertAll(PetFoodSeedCatalog.products.map { it.toEntity() })
        }
    }
}
