package com.example.mypetshop.ui.auth

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mypetshop.data.auth.PasswordResetStore
import com.example.mypetshop.domain.repository.AuthRepository
import com.example.mypetshop.util.AuthValidator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ResetPasswordUiState(
    val email: String = "",
    val code: String = "",
    val newPassword: String = "",
    val confirmPassword: String = "",
    val passwordVisible: Boolean = false,
    val codeErrorKey: String? = null,
    val passwordErrorKey: String? = null,
    val confirmErrorKey: String? = null,
    val resetSuccessful: Boolean = false,
    val isLoading: Boolean = false,
)

@HiltViewModel
class ResetPasswordViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val authRepository: AuthRepository,
    private val passwordResetStore: PasswordResetStore,
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        ResetPasswordUiState(email = savedStateHandle.get<String>("email").orEmpty()),
    )
    val uiState: StateFlow<ResetPasswordUiState> = _uiState.asStateFlow()

    fun onCodeChange(value: String) {
        _uiState.update { it.copy(code = value.filter(Char::isDigit).take(6), codeErrorKey = null) }
    }

    fun onNewPasswordChange(value: String) {
        _uiState.update { it.copy(newPassword = value, passwordErrorKey = null) }
    }

    fun onConfirmPasswordChange(value: String) {
        _uiState.update { it.copy(confirmPassword = value, confirmErrorKey = null) }
    }

    fun onPasswordVisibilityToggle() {
        _uiState.update { it.copy(passwordVisible = !it.passwordVisible) }
    }

    fun onResetClick() {
        val state = _uiState.value
        val passwordError = AuthValidator.validatePassword(state.newPassword)
        val confirmError = when {
            state.confirmPassword.isBlank() -> "error_password_empty"
            state.confirmPassword != state.newPassword -> "error_password_mismatch"
            else -> null
        }
        if (passwordError != null || confirmError != null) {
            _uiState.update {
                it.copy(passwordErrorKey = passwordError, confirmErrorKey = confirmError)
            }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val validCode = passwordResetStore.verifyCode(state.email, state.code)
            if (!validCode) {
                _uiState.update {
                    it.copy(codeErrorKey = "error_reset_code_invalid", isLoading = false)
                }
                return@launch
            }
            val updated = authRepository.resetPassword(state.email, state.newPassword)
            passwordResetStore.clear()
            _uiState.update {
                it.copy(
                    resetSuccessful = updated,
                    codeErrorKey = if (updated) null else "error_reset_failed",
                    isLoading = false,
                )
            }
        }
    }

    fun consumeResetSuccess() {
        _uiState.update { it.copy(resetSuccessful = false) }
    }
}
