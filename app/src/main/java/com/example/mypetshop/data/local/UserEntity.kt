package com.example.mypetshop.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val email: String,
    val password: String,
    val displayName: String,
    val role: String,
    val phone: String = "",
)
