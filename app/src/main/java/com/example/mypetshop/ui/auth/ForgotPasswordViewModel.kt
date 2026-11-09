package com.example.mypetshop.ui.auth

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

data class ForgotPasswordUiState(
    val email: String = "",
    val emailErrorKey: String? = null,
    val demoResetCode: String? = null,
    val codeSent: Boolean = false,
    val isLoading: Boolean = false,
)

@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val passwordResetStore: PasswordResetStore,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ForgotPasswordUiState())
    val uiState: StateFlow<ForgotPasswordUiState> = _uiState.asStateFlow()

    fun onEmailChange(value: String) {
        _uiState.update { it.copy(email = value, emailErrorKey = null, codeSent = false, demoResetCode = null) }
    }

    fun onSendCode() {
        val emailError = AuthValidator.validateEmail(_uiState.value.email)
        if (emailError != null) {
            _uiState.update { it.copy(emailErrorKey = emailError) }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val email = _uiState.value.email.trim().lowercase()
            if (!authRepository.userExists(email)) {
                _uiState.update {
                    it.copy(
                        emailErrorKey = "error_email_not_found",
                        isLoading = false,
                    )
                }
                return@launch
            }
            val code = passwordResetStore.createResetCode(email)
            _uiState.update {
                it.copy(
                    demoResetCode = code,
                    codeSent = true,
                    isLoading = false,
                )
            }
        }
    }
}
