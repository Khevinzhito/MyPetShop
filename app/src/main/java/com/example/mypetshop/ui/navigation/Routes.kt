package com.example.mypetshop.ui.navigation

object Routes {
    const val AUTH_WELCOME = "auth_welcome"
    const val REGISTER = "register"
    const val LOGIN = "login"
    const val FORGOT_PASSWORD = "forgot_password"
    const val RESET_PASSWORD = "reset_password/{email}"
    const val PET_ONBOARDING = "pet_onboarding"
    const val MAIN = "main"
    const val ADMIN = "admin"
    const val DRIVER = "driver"

    fun resetPassword(email: String) = "reset_password/$email"
}
