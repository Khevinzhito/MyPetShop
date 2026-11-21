package com.example.mypetshop.data.demo

import com.example.mypetshop.BuildConfig
import com.example.mypetshop.domain.model.AuthUser
import com.example.mypetshop.domain.model.UserRole

/**
 * Metadata publica de las cuentas demo (solo email + nombre + rol).
 *
 * Las contrasenas NO viven en el codigo fuente: se leen desde BuildConfig (poblado por
 * local.properties, que no se commitea). Antes de almacenarlas en la base de datos se
 * hashean con [com.example.mypetshop.util.PasswordHasher].
 */
object DemoAccounts {

    const val BUYER_EMAIL = "kbrgvinzhito@gmail.com"
    const val BUYER_NAME = "Kevin Zhito"

    const val ADMIN_EMAIL = "admin@mypetshop.com"
    const val ADMIN_NAME = "Administrador MyPetShop"

    const val DRIVER_EMAIL = "repartidor@mypetshop.com"
    const val DRIVER_NAME = "Repartidor MyPetShop"
    const val DRIVER_PHONE = "3001234567"

    data class Seed(
        val email: String,
        val displayName: String,
        val role: UserRole,
        val rawPassword: String,
        val phone: String = "",
    ) {
        val hasPassword: Boolean get() = rawPassword.isNotBlank()
    }

    fun seeds(): List<Seed> = listOf(
        Seed(BUYER_EMAIL, BUYER_NAME, UserRole.BUYER, BuildConfig.DEMO_BUYER_PASSWORD),
        Seed(ADMIN_EMAIL, ADMIN_NAME, UserRole.ADMIN, BuildConfig.DEMO_ADMIN_PASSWORD),
        Seed(DRIVER_EMAIL, DRIVER_NAME, UserRole.DRIVER, BuildConfig.DEMO_DRIVER_PASSWORD, DRIVER_PHONE),
    )

    fun buyerUser() = AuthUser(BUYER_EMAIL, BUYER_NAME, UserRole.BUYER)
    fun adminUser() = AuthUser(ADMIN_EMAIL, ADMIN_NAME, UserRole.ADMIN)
    fun driverUser() = AuthUser(DRIVER_EMAIL, DRIVER_NAME, UserRole.DRIVER, DRIVER_PHONE)
}
