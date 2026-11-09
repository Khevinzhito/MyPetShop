package com.example.mypetshop.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mypetshop.data.auth.BiometricCredentialStore
import com.example.mypetshop.data.demo.DemoAccounts
import com.example.mypetshop.data.session.NavigationSession
import com.example.mypetshop.domain.model.AuthUser
import com.example.mypetshop.domain.model.DriverProfile
import com.example.mypetshop.domain.model.UserRole
import com.example.mypetshop.domain.repository.AuthRepository
import com.example.mypetshop.domain.repository.PetRepository
import com.example.mypetshop.util.AuthValidator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val phone: String = "",
    val passwordVisible: Boolean = false,
    val enableBiometric: Boolean = false,
    val emailErrorKey: String? = null,
    val passwordErrorKey: String? = null,
    val phoneErrorKey: String? = null,
    val isLoginSuccessful: Boolean = false,
    val loggedInRole: UserRole? = null,
    val skipOnboarding: Boolean = false,
    val showBiometricButton: Boolean = false,
    val isLoading: Boolean = false,
)

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val navigationSession: NavigationSession,
    private val petRepository: PetRepository,
    private val authRepository: AuthRepository,
    private val biometricCredentialStore: BiometricCredentialStore,
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val enabled = biometricCredentialStore.isEnabled()
            val storedEmail = biometricCredentialStore.getStoredEmail()
            _uiState.update {
                it.copy(
                    showBiometricButton = enabled && !storedEmail.isNullOrBlank(),
                    email = if (enabled && !storedEmail.isNullOrBlank()) storedEmail else it.email,
                )
            }
        }
    }

    fun onEmailChange(value: String) {
        _uiState.update { it.copy(email = value, emailErrorKey = null, isLoginSuccessful = false) }
    }

    fun onPasswordChange(value: String) {
        _uiState.update { it.copy(password = value, passwordErrorKey = null, isLoginSuccessful = false) }
    }

    fun onPhoneChange(value: String) {
        _uiState.update { it.copy(phone = value, phoneErrorKey = null, isLoginSuccessful = false) }
    }

    fun onPasswordVisibilityToggle() {
        _uiState.update { it.copy(passwordVisible = !it.passwordVisible) }
    }

    fun onEnableBiometricChange(enabled: Boolean) {
        _uiState.update { it.copy(enableBiometric = enabled) }
    }

    fun loginWithDemoAccount(role: UserRole) {
        val seed = DemoAccounts.seeds().firstOrNull { it.role == role } ?: return
        if (!seed.hasPassword) {
            _uiState.update {
                it.copy(
                    email = seed.email,
                    passwordErrorKey = "error_demo_password_missing",
                    isLoading = false,
                )
            }
            return
        }
        _uiState.update { it.copy(email = seed.email, password = seed.rawPassword) }
        performLogin(seed.email, seed.rawPassword, saveBiometric = false)
    }

    fun onLoginClick() {
        val state = _uiState.value
        val emailError = AuthValidator.validateEmail(state.email)
        val passwordError = AuthValidator.validatePassword(state.password)
        val phoneError = AuthValidator.validatePhone(state.phone)
        if (emailError != null || passwordError != null || phoneError != null) {
            _uiState.update {
                it.copy(
                    emailErrorKey = emailError,
                    passwordErrorKey = passwordError,
                    phoneErrorKey = phoneError,
                    isLoginSuccessful = false,
                )
            }
            return
        }
        performLogin(state.email, state.password, saveBiometric = state.enableBiometric)
    }

    fun onBiometricLoginSuccess() {
        viewModelScope.launch {
            val email = biometricCredentialStore.getStoredEmail() ?: return@launch
            val user = authRepository.getUserByEmail(email) ?: return@launch
            applyLogin(user, saveBiometric = false)
        }
    }

    private fun performLogin(email: String, password: String, saveBiometric: Boolean) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val user = authRepository.login(email, password)
            if (user == null) {
                _uiState.update {
                    it.copy(
                        passwordErrorKey = "error_login_failed",
                        isLoginSuccessful = false,
                        isLoading = false,
                    )
                }
                return@launch
            }
            if (saveBiometric) {
                biometricCredentialStore.saveCredential(user.email)
            }
            applyLogin(user, saveBiometric = saveBiometric)
        }
    }

    private suspend fun applyLogin(user: AuthUser, saveBiometric: Boolean) {
        navigationSession.currentUser = user
        if (user.role == UserRole.DRIVER) {
            navigationSession.currentDriver = buildDriverProfile(user)
        }
        val savedProfile = if (user.role == UserRole.BUYER) {
            petRepository.observeLatestProfile().first()
        } else {
            null
        }
        navigationSession.currentProfile = savedProfile
        val showBiometric = biometricCredentialStore.isEnabled() &&
            !biometricCredentialStore.getStoredEmail().isNullOrBlank()
        _uiState.update {
            it.copy(
                emailErrorKey = null,
                passwordErrorKey = null,
                phoneErrorKey = null,
                isLoginSuccessful = true,
                loggedInRole = user.role,
                skipOnboarding = user.role != UserRole.BUYER || savedProfile != null,
                showBiometricButton = showBiometric || saveBiometric,
                isLoading = false,
            )
        }
    }

    fun consumeLoginSuccess() {
        _uiState.update {
            it.copy(isLoginSuccessful = false, loggedInRole = null, skipOnboarding = false)
        }
    }

    private fun buildDriverProfile(user: AuthUser) = DriverProfile(
        fullName = user.displayName,
        cedula = "",
        phone = user.phone,
        vehicleType = "Moto",
        vehiclePlate = "—",
        vehicleBrand = "—",
        vehicleColor = "—",
    )
}
