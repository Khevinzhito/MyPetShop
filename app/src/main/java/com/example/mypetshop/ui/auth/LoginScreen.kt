package com.example.mypetshop.ui.auth

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
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.mypetshop.R
import com.example.mypetshop.data.demo.DemoAccounts
import com.example.mypetshop.domain.model.UserRole
import com.example.mypetshop.ui.components.AppLogo
import com.example.mypetshop.ui.components.AppTopBar
import com.example.mypetshop.ui.theme.BackgroundWhite
import com.example.mypetshop.ui.theme.OrangeDark
import com.example.mypetshop.ui.theme.OrangeLight
import com.example.mypetshop.ui.theme.OrangePrimary
import com.example.mypetshop.ui.theme.TextSecondary
import com.example.mypetshop.util.BiometricHelper
import com.example.mypetshop.util.findFragmentActivity

@Composable
fun LoginScreen(
    onLoginSuccess: (UserRole, Boolean) -> Unit,
    onRegisterClick: (() -> Unit)? = null,
    onForgotPasswordClick: (() -> Unit)? = null,
    onBack: (() -> Unit)? = null,
    viewModel: LoginViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = OrangePrimary,
        unfocusedBorderColor = OrangeLight,
        focusedLabelColor = OrangeDark,
    )
    val biometricTitle = stringResource(R.string.login_biometric_title)
    val biometricSubtitle = stringResource(R.string.login_biometric_subtitle)

    LaunchedEffect(uiState.isLoginSuccessful, uiState.loggedInRole, uiState.skipOnboarding) {
        if (uiState.isLoginSuccessful) {
            val role = uiState.loggedInRole ?: UserRole.BUYER
            val skipOnboarding = uiState.skipOnboarding
            viewModel.consumeLoginSuccess()
            onLoginSuccess(role, skipOnboarding)
        }
    }

    Scaffold(
        containerColor = BackgroundWhite,
        topBar = {
            AppTopBar(
                title = stringResource(R.string.login_title),
                showBack = onBack != null,
                onBack = { onBack?.invoke() },
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
            AppLogo(modifier = Modifier.fillMaxWidth(), size = 100.dp, applyTint = false)
            Spacer(Modifier.height(12.dp))

            DemoAccountCard(
                title = stringResource(R.string.registered_user_title),
                email = DemoAccounts.BUYER_EMAIL,
                hint = stringResource(R.string.registered_user_hint),
                buttonText = stringResource(R.string.registered_user_button),
                onClick = { viewModel.loginWithDemoAccount(UserRole.BUYER) },
            )
            Spacer(Modifier.height(12.dp))
            DemoAccountCard(
                title = stringResource(R.string.admin_user_title),
                email = DemoAccounts.ADMIN_EMAIL,
                hint = stringResource(R.string.admin_user_hint),
                buttonText = stringResource(R.string.admin_user_button),
                onClick = { viewModel.loginWithDemoAccount(UserRole.ADMIN) },
                accent = OrangeDark,
            )
            Spacer(Modifier.height(12.dp))
            DemoAccountCard(
                title = stringResource(R.string.driver_user_title),
                email = DemoAccounts.DRIVER_EMAIL,
                hint = stringResource(R.string.driver_user_hint),
                buttonText = stringResource(R.string.driver_user_button),
                onClick = { viewModel.loginWithDemoAccount(UserRole.DRIVER) },
                icon = Icons.Default.LocalShipping,
            )
            Spacer(Modifier.height(16.dp))

            if (uiState.showBiometricButton) {
                OutlinedButton(
                    onClick = {
                        val activity = context.findFragmentActivity()
                        if ((activity != null) && BiometricHelper.isAvailable(activity)) {
                            BiometricHelper.authenticate(
                                activity = activity,
                                title = biometricTitle,
                                subtitle = biometricSubtitle,
                                onSuccess = viewModel::onBiometricLoginSuccess,
                                onError = {},
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = OrangePrimary),
                ) {
                    Icon(Icons.Default.Fingerprint, null, modifier = Modifier.size(22.dp))
                    Text(stringResource(R.string.login_biometric_button), modifier = Modifier.padding(start = 8.dp))
                }
                Spacer(Modifier.height(16.dp))
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = BackgroundWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            ) {
                Column(Modifier.padding(24.dp)) {
                    Text(
                        stringResource(R.string.login_title),
                        style = MaterialTheme.typography.headlineSmall,
                        color = OrangeDark,
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(stringResource(R.string.login_subtitle), color = TextSecondary)
                    Spacer(Modifier.height(24.dp))

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
                    Spacer(Modifier.height(8.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = uiState.enableBiometric,
                            onCheckedChange = viewModel::onEnableBiometricChange,
                            colors = CheckboxDefaults.colors(checkedColor = OrangePrimary),
                        )
                        Text(stringResource(R.string.login_enable_biometric), color = TextSecondary, style = MaterialTheme.typography.bodySmall)
                    }
                    Spacer(Modifier.height(16.dp))

                    Button(
                        onClick = viewModel::onLoginClick,
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !uiState.isLoading,
                        colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary, contentColor = BackgroundWhite),
                    ) {
                        if (uiState.isLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), color = BackgroundWhite, strokeWidth = 2.dp)
                        } else {
                            Text(stringResource(R.string.login_button))
                        }
                    }
                    onForgotPasswordClick?.let { forgot ->
                        TextButton(onClick = forgot, modifier = Modifier.fillMaxWidth()) {
                            Text(stringResource(R.string.forgot_password_link), color = OrangePrimary)
                        }
                    }
                    onRegisterClick?.let { register ->
                        TextButton(onClick = register, modifier = Modifier.fillMaxWidth()) {
                            Text(stringResource(R.string.no_account_register), color = OrangePrimary)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DemoAccountCard(
    title: String,
    email: String,
    hint: String,
    buttonText: String,
    onClick: () -> Unit,
    accent: androidx.compose.ui.graphics.Color = OrangePrimary,
    icon: androidx.compose.ui.graphics.vector.ImageVector = Icons.Default.AccountCircle,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = OrangeLight.copy(alpha = 0.35f)),
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, null, tint = accent)
                Text(title, modifier = Modifier.padding(start = 8.dp), style = MaterialTheme.typography.titleSmall, color = OrangeDark, fontWeight = FontWeight.SemiBold)
            }
            Spacer(Modifier.height(6.dp))
            Text(email, color = OrangeDark, style = MaterialTheme.typography.bodySmall)
            Text(hint, color = TextSecondary, style = MaterialTheme.typography.bodySmall)
            Spacer(Modifier.height(8.dp))
            OutlinedButton(onClick = onClick, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.outlinedButtonColors(contentColor = accent)) {
                Text(buttonText)
            }
        }
    }
}
