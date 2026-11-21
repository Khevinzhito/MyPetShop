package com.example.mypetshop.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.mypetshop.data.session.NavigationSession
import com.example.mypetshop.domain.model.DriverProfile
import com.example.mypetshop.domain.model.UserRole
import com.example.mypetshop.ui.admin.AdminShellScreen
import com.example.mypetshop.ui.auth.AuthWelcomeScreen
import com.example.mypetshop.ui.auth.ForgotPasswordScreen
import com.example.mypetshop.ui.auth.LoginScreen
import com.example.mypetshop.ui.auth.RegisterScreen
import com.example.mypetshop.ui.auth.ResetPasswordScreen
import com.example.mypetshop.ui.driver.DriverShellScreen
import com.example.mypetshop.ui.main.MainScreen
import com.example.mypetshop.ui.onboarding.PetOnboardingScreen

@Composable
fun AppNavigation(session: NavigationSession) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Routes.AUTH_WELCOME) {
        composable(Routes.AUTH_WELCOME) {
            AuthWelcomeScreen(
                onRegisterClick = { navController.navigate(Routes.REGISTER) },
                onLoginClick = { navController.navigate(Routes.LOGIN) },
            )
        }

        composable(Routes.REGISTER) {
            RegisterScreen(
                onRegisterSuccess = { role ->
                    when (role) {
                        UserRole.DRIVER -> navController.navigate(Routes.DRIVER) {
                            popUpTo(Routes.AUTH_WELCOME) { inclusive = true }
                        }
                        UserRole.BUYER -> navController.navigate(Routes.PET_ONBOARDING) {
                            popUpTo(Routes.AUTH_WELCOME) { inclusive = true }
                        }
                        UserRole.ADMIN -> navController.navigate(Routes.ADMIN) {
                            popUpTo(Routes.AUTH_WELCOME) { inclusive = true }
                        }
                    }
                },
                onLoginClick = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.REGISTER) { inclusive = true }
                    }
                },
                onBack = { navController.popBackStack() },
            )
        }

        composable(Routes.LOGIN) {
            LoginScreen(
                onLoginSuccess = { role, skipOnboarding ->
                    when (role) {
                        UserRole.ADMIN -> navController.navigate(Routes.ADMIN) {
                            popUpTo(Routes.AUTH_WELCOME) { inclusive = true }
                        }
                        UserRole.DRIVER -> navController.navigate(Routes.DRIVER) {
                            popUpTo(Routes.AUTH_WELCOME) { inclusive = true }
                        }
                        UserRole.BUYER -> {
                            val destination = if (skipOnboarding) Routes.MAIN else Routes.PET_ONBOARDING
                            navController.navigate(destination) {
                                popUpTo(Routes.AUTH_WELCOME) { inclusive = true }
                            }
                        }
                    }
                },
                onRegisterClick = {
                    navController.navigate(Routes.REGISTER) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onForgotPasswordClick = { navController.navigate(Routes.FORGOT_PASSWORD) },
                onBack = { navController.popBackStack() },
            )
        }

        composable(Routes.FORGOT_PASSWORD) {
            ForgotPasswordScreen(
                onCodeSent = { email ->
                    navController.navigate(Routes.resetPassword(email)) {
                        popUpTo(Routes.FORGOT_PASSWORD) { inclusive = true }
                    }
                },
                onBack = { navController.popBackStack() },
            )
        }

        composable(
            route = Routes.RESET_PASSWORD,
            arguments = listOf(navArgument("email") { type = NavType.StringType }),
        ) {
            ResetPasswordScreen(
                onResetSuccess = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.AUTH_WELCOME) { inclusive = false }
                    }
                },
                onBack = { navController.popBackStack() },
            )
        }

        composable(Routes.PET_ONBOARDING) {
            PetOnboardingScreen(
                onProfileSaved = { profile ->
                    session.currentProfile = profile
                    navController.navigate(Routes.MAIN) {
                        popUpTo(Routes.PET_ONBOARDING) { inclusive = true }
                    }
                },
                onSkip = {
                    navController.navigate(Routes.MAIN) {
                        popUpTo(Routes.PET_ONBOARDING) { inclusive = true }
                    }
                },
            )
        }

        composable(Routes.MAIN) {
            MainScreen(
                profile = session.currentProfile,
                session = session,
                onPetProfileSaved = { profile -> session.currentProfile = profile },
                onLogout = {
                    session.clearSession()
                    navController.navigate(Routes.AUTH_WELCOME) {
                        popUpTo(0) { inclusive = true }
                    }
                },
            )
        }

        composable(Routes.ADMIN) {
            AdminShellScreen(
                onLogout = {
                    session.clearSession()
                    navController.navigate(Routes.AUTH_WELCOME) {
                        popUpTo(0) { inclusive = true }
                    }
                },
            )
        }

        composable(Routes.DRIVER) {
            val driver = session.currentDriver ?: session.currentUser?.let { user ->
                DriverProfile(
                    fullName = user.displayName,
                    cedula = "",
                    phone = user.phone,
                    vehicleType = "Moto",
                    vehiclePlate = "—",
                    vehicleBrand = "—",
                    vehicleColor = "—",
                )
            } ?: return@composable

            DriverShellScreen(
                driver = driver,
                session = session,
                showBackToStore = false,
                onExitToBuyer = {},
                onLogout = {
                    session.clearSession()
                    navController.navigate(Routes.AUTH_WELCOME) {
                        popUpTo(0) { inclusive = true }
                    }
                },
            )
        }
    }
}
