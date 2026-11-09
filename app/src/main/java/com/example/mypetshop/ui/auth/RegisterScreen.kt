package com.example.mypetshop.ui.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.mypetshop.R
import com.example.mypetshop.domain.model.UserRole
import com.example.mypetshop.ui.components.AppLogo
import com.example.mypetshop.ui.components.AppTopBar
import com.example.mypetshop.ui.theme.BackgroundWhite
import com.example.mypetshop.ui.theme.OrangeDark
import com.example.mypetshop.ui.theme.OrangeLight
import com.example.mypetshop.ui.theme.OrangePrimary
import com.example.mypetshop.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onRegisterSuccess: (UserRole) -> Unit,
    onLoginClick: () -> Unit,
    onBack: () -> Unit,
    viewModel: RegisterViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = OrangePrimary,
        unfocusedBorderColor = OrangeLight,
        focusedLabelColor = OrangeDark,
    )

    LaunchedEffect(uiState.isRegisterSuccessful) {
        if (uiState.isRegisterSuccessful) {
            val role = uiState.registeredRole
            viewModel.consumeRegisterSuccess()
            onRegisterSuccess(role)
        }
    }

    Scaffold(
        containerColor = BackgroundWhite,
        topBar = {
            AppTopBar(
                title = stringResource(R.string.register_title),
                showBack = true,
                onBack = onBack,
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp, vertical = 16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            AppLogo(size = 100.dp, applyTint = false)
            Spacer(Modifier.height(16.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = BackgroundWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            ) {
                Column(Modifier.padding(24.dp)) {
                    Text(stringResource(R.string.register_title), style = MaterialTheme.typography.headlineSmall, color = OrangeDark)
                    Spacer(Modifier.height(8.dp))
                    Text(stringResource(R.string.register_subtitle), color = TextSecondary)
                    Spacer(Modifier.height(16.dp))
                    Text(stringResource(R.string.register_role_label), color = OrangeDark, style = MaterialTheme.typography.labelLarge)
                    Spacer(Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(
                            selected = uiState.selectedRole == UserRole.BUYER,
                            onClick = { viewModel.onRoleChange(UserRole.BUYER) },
                            label = { Text(stringResource(R.string.role_buyer)) },
                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = OrangeLight),
                        )
                        FilterChip(
                            selected = uiState.selectedRole == UserRole.DRIVER,
                            onClick = { viewModel.onRoleChange(UserRole.DRIVER) },
                            label = { Text(stringResource(R.string.role_driver)) },
                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = OrangeLight),
                        )
                    }
                    Spacer(Modifier.height(16.dp))

                    OutlinedTextField(
                        value = uiState.email,
                        onValueChange = viewModel::onEmailChange,
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text(stringResource(R.string.email_hint)) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        isError = uiState.emailErrorKey != null,
                        supportingText = uiState.emailErrorKey?.let { { AuthErrorText(it) } },
                        colors = fieldColors,
                    )
                    Spacer(Modifier.height(12.dp))

                    OutlinedTextField(
                        value = uiState.password,
                        onValueChange = viewModel::onPasswordChange,
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text(stringResource(R.string.password_hint)) },
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
                        supportingText = {
                            val errorKey = uiState.passwordErrorKey
                            if (errorKey != null) AuthErrorText(errorKey)
                            else Text(stringResource(R.string.password_rules), color = TextSecondary, style = MaterialTheme.typography.bodySmall)
                        },
                        colors = fieldColors,
                    )
                    Spacer(Modifier.height(12.dp))

                    OutlinedTextField(
                        value = uiState.phone,
                        onValueChange = viewModel::onPhoneChange,
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text(stringResource(R.string.phone_hint)) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        isError = uiState.phoneErrorKey != null,
                        supportingText = uiState.phoneErrorKey?.let { { AuthErrorText(it) } },
                        colors = fieldColors,
                    )
                    uiState.registerErrorKey?.let { key ->
                        Spacer(Modifier.height(8.dp))
                        AuthErrorText(key)
                    }
                    Spacer(Modifier.height(24.dp))

                    Button(
                        onClick = viewModel::onRegisterClick,
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !uiState.isLoading,
                        colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary, contentColor = BackgroundWhite),
                    ) {
                        if (uiState.isLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), color = BackgroundWhite, strokeWidth = 2.dp)
                        } else {
                            Text(stringResource(R.string.register_button))
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    TextButton(onClick = onLoginClick, modifier = Modifier.fillMaxWidth()) {
                        Text(stringResource(R.string.already_have_account), color = OrangePrimary)
                    }
                }
            }
        }
    }
}
