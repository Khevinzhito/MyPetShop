package com.example.mypetshop.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mypetshop.data.session.NavigationSession
import com.example.mypetshop.domain.model.AuthUser
import com.example.mypetshop.domain.model.DriverProfile
import com.example.mypetshop.domain.model.UserRole
import com.example.mypetshop.domain.repository.AuthRepository
import com.example.mypetshop.domain.repository.RegisterResult
import com.example.mypetshop.util.AuthValidator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RegisterUiState(
    val email: String = "",
    val password: String = "",
    val phone: String = "",
    val passwordVisible: Boolean = false,
    val selectedRole: UserRole = UserRole.BUYER,
    val emailErrorKey: String? = null,
    val passwordErrorKey: String? = null,
    val phoneErrorKey: String? = null,
    val registerErrorKey: String? = null,
    val isRegisterSuccessful: Boolean = false,
    val registeredRole: UserRole = UserRole.BUYER,
    val isLoading: Boolean = false,
)

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val navigationSession: NavigationSession,
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun onEmailChange(value: String) {
        _uiState.update { it.copy(email = value, emailErrorKey = null, registerErrorKey = null, isRegisterSuccessful = false) }
    }

    fun onPasswordChange(value: String) {
        _uiState.update { it.copy(password = value, passwordErrorKey = null, registerErrorKey = null, isRegisterSuccessful = false) }
    }

    fun onPhoneChange(value: String) {
        _uiState.update { it.copy(phone = value, phoneErrorKey = null, registerErrorKey = null, isRegisterSuccessful = false) }
    }

    fun onPasswordVisibilityToggle() {
        _uiState.update { it.copy(passwordVisible = !it.passwordVisible) }
    }

    fun onRoleChange(role: UserRole) {
        _uiState.update { it.copy(selectedRole = role, registerErrorKey = null, isRegisterSuccessful = false) }
    }

    fun onRegisterClick() {
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
                    isRegisterSuccessful = false,
                )
            }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val displayName = state.email.substringBefore("@").replaceFirstChar { it.uppercase() }
            when (
                authRepository.register(
                    email = state.email,
                    password = state.password,
                    phone = state.phone,
                    displayName = displayName,
                    role = state.selectedRole,
                )
            ) {
                RegisterResult.EmailAlreadyExists -> {
                    _uiState.update {
                        it.copy(
                            registerErrorKey = "error_email_exists",
                            isRegisterSuccessful = false,
                            isLoading = false,
                        )
                    }
                }
                RegisterResult.Success -> {
                    val user = AuthUser(
                        email = state.email.trim().lowercase(),
                        displayName = displayName,
                        role = state.selectedRole,
                        phone = state.phone.trim(),
                    )
                    navigationSession.currentUser = user
                    if (state.selectedRole == UserRole.DRIVER) {
                        navigationSession.currentDriver = DriverProfile(
                            fullName = displayName,
                            cedula = "",
                            phone = state.phone.trim(),
                            vehicleType = "",
                            vehiclePlate = "",
                            vehicleBrand = "",
                            vehicleColor = "",
                        )
                    }
                    _uiState.update {
                        it.copy(
                            isRegisterSuccessful = true,
                            registeredRole = state.selectedRole,
                            isLoading = false,
                        )
                    }
                }
            }
        }
    }

    fun consumeRegisterSuccess() {
        _uiState.update { it.copy(isRegisterSuccessful = false) }
    }
}
