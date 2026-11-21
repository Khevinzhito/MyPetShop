package com.example.mypetshop.util

import android.util.Patterns

object AuthValidator {

    fun validateEmail(email: String): String? = when {
        email.isBlank() -> "error_email_empty"
        !Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches() -> "error_email_invalid"
        else -> null
    }

    fun validatePassword(password: String): String? = when {
        password.isBlank() -> "error_password_empty"
        password.length < 8 -> "error_password_invalid"
        !password.any { it.isUpperCase() } -> "error_password_invalid"
        !password.any { it.isLowerCase() } -> "error_password_invalid"
        else -> null
    }

    fun validatePhone(phone: String): String? {
        val trimmed = phone.trim()
        if (trimmed.isEmpty()) return null
        val digitsOnly = trimmed.filter { it.isDigit() }
        return if (digitsOnly.length in 8..15) null else "error_phone_invalid"
    }
}
