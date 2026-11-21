package com.example.mypetshop.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey val id: String,
    val name: String,
    val description: String,
    val priceCents: Long,
    val brand: String,
    val category: String,
    val petType: String,
    val imageUri: String?,
    val isActive: Boolean,
)
