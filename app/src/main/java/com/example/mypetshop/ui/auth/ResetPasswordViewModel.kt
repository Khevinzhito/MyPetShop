package com.example.mypetshop.ui.auth

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    val securityQuestion: String = "",
    val answer: String = "",
    val newPassword: String = "",
    val confirmPassword: String = "",
    val passwordVisible: Boolean = false,
    val answerErrorKey: String? = null,
    val passwordErrorKey: String? = null,
    val confirmErrorKey: String? = null,
    val resetSuccessful: Boolean = false,
    val isLoading: Boolean = false,
)

@HiltViewModel
class ResetPasswordViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        ResetPasswordUiState(email = savedStateHandle.get<String>("email").orEmpty()),
    )
    val uiState: StateFlow<ResetPasswordUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val email = _uiState.value.email
            val question = authRepository.getSecurityQuestion(email) ?: "Pregunta de seguridad no disponible"
            _uiState.update { it.copy(securityQuestion = question) }
        }
    }

    fun onAnswerChange(value: String) {
        _uiState.update { it.copy(answer = value, answerErrorKey = null) }
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
        val answerError = if (state.answer.isBlank()) "error_security_answer" else null
        val passwordError = AuthValidator.validatePassword(state.newPassword)
        val confirmError = when {
            state.confirmPassword.isBlank() -> "error_password_empty"
            state.confirmPassword != state.newPassword -> "error_password_mismatch"
            else -> null
        }
        if (answerError != null || passwordError != null || confirmError != null) {
            _uiState.update {
                it.copy(answerErrorKey = answerError, passwordErrorKey = passwordError, confirmErrorKey = confirmError)
            }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val validAnswer = authRepository.verifySecurityAnswer(state.email, state.answer)
            if (!validAnswer) {
                _uiState.update {
                    it.copy(answerErrorKey = "error_answer_invalid", isLoading = false)
                }
                return@launch
            }
            val updated = authRepository.resetPassword(state.email, state.newPassword)
            _uiState.update {
                it.copy(
                    resetSuccessful = updated,
                    answerErrorKey = if (updated) null else "error_reset_failed",
                    isLoading = false,
                )
            }
        }
    }

    fun consumeResetSuccess() {
        _uiState.update { it.copy(resetSuccessful = false) }
    }
}
