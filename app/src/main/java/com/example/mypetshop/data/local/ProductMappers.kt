package com.example.mypetshop.data.local

import com.example.mypetshop.domain.model.ProductCategory
import com.example.mypetshop.domain.model.ProductPetType
import com.example.mypetshop.domain.model.ShopProduct

fun ProductEntity.toDomain(): ShopProduct = ShopProduct(
    id = id,
    name = name,
    description = description,
    priceCents = priceCents,
    brand = brand,
    category = ProductCategory.valueOf(category),
    petType = ProductPetType.valueOf(petType),
    imageUri = imageUri,
    isActive = isActive,
)

fun ShopProduct.toEntity(): ProductEntity = ProductEntity(
    id = id,
    name = name,
    description = description,
    priceCents = priceCents,
    brand = brand,
    category = category.name,
    petType = petType.name,
    imageUri = imageUri,
    isActive = isActive,
)
