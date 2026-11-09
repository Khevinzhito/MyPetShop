package com.example.mypetshop.domain.model

enum class UserRole {
    BUYER,
    ADMIN,
    DRIVER,
}

data class AuthUser(
    val email: String,
    val displayName: String,
    val role: UserRole,
    val phone: String = "",
)
