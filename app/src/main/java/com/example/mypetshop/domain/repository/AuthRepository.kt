package com.example.mypetshop.domain.repository

import com.example.mypetshop.domain.model.AuthUser
import com.example.mypetshop.domain.model.UserRole

interface AuthRepository {
    suspend fun seedIfEmpty()
    suspend fun login(email: String, password: String): AuthUser?
    suspend fun register(
        email: String,
        password: String,
        phone: String,
        displayName: String,
        role: UserRole,
    ): RegisterResult
    suspend fun userExists(email: String): Boolean
    suspend fun resetPassword(email: String, newPassword: String): Boolean
    suspend fun getUserByEmail(email: String): AuthUser?
}

sealed class RegisterResult {
    data object Success : RegisterResult()
    data object EmailAlreadyExists : RegisterResult()
}
