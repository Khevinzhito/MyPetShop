package com.example.mypetshop.ui.auth

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.mypetshop.R
import com.example.mypetshop.ui.components.AppTopBar
import com.example.mypetshop.ui.theme.BackgroundWhite
import com.example.mypetshop.ui.theme.OrangeDark
import com.example.mypetshop.ui.theme.OrangeLight
import com.example.mypetshop.ui.theme.OrangePrimary
import com.example.mypetshop.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResetPasswordScreen(
    onResetSuccess: () -> Unit,
    onBack: () -> Unit,
    viewModel: ResetPasswordViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = OrangePrimary,
        unfocusedBorderColor = OrangeLight,
        focusedLabelColor = OrangeDark,
    )

    LaunchedEffect(uiState.resetSuccessful) {
        if (uiState.resetSuccessful) {
            viewModel.consumeResetSuccess()
            onResetSuccess()
        }
    }

    Scaffold(
        containerColor = BackgroundWhite,
        topBar = {
            AppTopBar(
                title = stringResource(R.string.reset_password_title),
                showBack = true,
                onBack = onBack,
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = BackgroundWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            ) {
                Column(Modifier.padding(24.dp)) {
                    Text(stringResource(R.string.reset_password_title), style = MaterialTheme.typography.headlineSmall, color = OrangeDark)
                    Spacer(Modifier.height(8.dp))
                    Text(stringResource(R.string.reset_password_subtitle), color = TextSecondary)
                    Spacer(Modifier.height(24.dp))

                    OutlinedTextField(
                        value = uiState.code,
                        onValueChange = viewModel::onCodeChange,
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text(stringResource(R.string.reset_code_hint)) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        isError = uiState.codeErrorKey != null,
                        supportingText = uiState.codeErrorKey?.let { { AuthErrorText(it) } },
                        colors = fieldColors,
                    )
                    Spacer(Modifier.height(12.dp))

                    OutlinedTextField(
                        value = uiState.newPassword,
                        onValueChange = viewModel::onNewPasswordChange,
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text(stringResource(R.string.new_password_hint)) },
                        singleLine = true,
                        visualTransformation = if (uiState.passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        trailingIcon = {
                            IconButton(onClick = viewModel::onPasswordVisibilityToggle) {
                                Icon(
                                    if (uiState.passwordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                                    contentDescription = null,
                                    tint = OrangePrimary,
                                )
                            }
                        },
                        isError = uiState.passwordErrorKey != null,
                        supportingText = uiState.passwordErrorKey?.let { { AuthErrorText(it) } },
                        colors = fieldColors,
                    )
                    Spacer(Modifier.height(12.dp))

                    OutlinedTextField(
                        value = uiState.confirmPassword,
                        onValueChange = viewModel::onConfirmPasswordChange,
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text(stringResource(R.string.confirm_password_hint)) },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        isError = uiState.confirmErrorKey != null,
                        supportingText = uiState.confirmErrorKey?.let { { AuthErrorText(it) } },
                        colors = fieldColors,
                    )
                    Spacer(Modifier.height(24.dp))

                    Button(
                        onClick = viewModel::onResetClick,
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !uiState.isLoading,
                        colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary, contentColor = BackgroundWhite),
                    ) {
                        if (uiState.isLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), color = BackgroundWhite, strokeWidth = 2.dp)
                        } else {
                            Text(stringResource(R.string.reset_password_button))
                        }
                    }
                }
            }
        }
    }
}
