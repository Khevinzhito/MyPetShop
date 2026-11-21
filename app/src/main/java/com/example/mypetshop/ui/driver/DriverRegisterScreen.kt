package com.example.mypetshop.ui.driver

import android.Manifest
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.example.mypetshop.R
import com.example.mypetshop.ui.components.AppLogo
import com.example.mypetshop.ui.components.AppTopBar
import com.example.mypetshop.ui.theme.BackgroundWhite
import com.example.mypetshop.ui.theme.DriverAccent
import com.example.mypetshop.ui.theme.DriverBackground
import com.example.mypetshop.ui.theme.DriverMuted
import com.example.mypetshop.ui.theme.DriverOnSurface
import com.example.mypetshop.ui.theme.DriverSurface
import com.example.mypetshop.ui.theme.ErrorRed
import com.example.mypetshop.ui.theme.OrangeDark
import com.example.mypetshop.ui.theme.OrangeLight
import com.example.mypetshop.ui.theme.OrangePrimary
import com.example.mypetshop.ui.theme.TextSecondary
import com.example.mypetshop.util.BiometricHelper
import com.example.mypetshop.util.LocationPermission
import com.example.mypetshop.util.findFragmentActivity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DriverRegisterScreen(
    onRegistrationComplete: (com.example.mypetshop.domain.model.DriverProfile) -> Unit,
    onBack: () -> Unit,
    embedInScaffold: Boolean = true,
    useDriverTheme: Boolean = false,
    viewModel: DriverRegisterViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val biometricRegisterTitle = stringResource(R.string.driver_biometric_register_title)
    val biometricRegisterSubtitle = stringResource(R.string.driver_biometric_register_subtitle)
    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = if (useDriverTheme) DriverAccent else OrangePrimary,
        unfocusedBorderColor = if (useDriverTheme) DriverMuted else OrangeLight,
        focusedLabelColor = if (useDriverTheme) DriverOnSurface else OrangeDark,
        unfocusedLabelColor = if (useDriverTheme) DriverMuted else OrangeDark,
        focusedTextColor = if (useDriverTheme) DriverOnSurface else OrangeDark,
        unfocusedTextColor = if (useDriverTheme) DriverOnSurface else OrangeDark,
    )
    val bgColor = if (useDriverTheme) DriverBackground else BackgroundWhite
    val subtitleColor = if (useDriverTheme) DriverMuted else TextSecondary
    val titleColor = if (useDriverTheme) DriverOnSurface else OrangeDark
    val accent = if (useDriverTheme) DriverAccent else OrangePrimary
    val vehicleTypes = listOf(
        stringResource(R.string.vehicle_type_moto),
        stringResource(R.string.vehicle_type_car),
        stringResource(R.string.vehicle_type_bike),
    )
    var vehicleExpanded by remember { mutableStateOf(false) }

    val photoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
    ) { uri: Uri? ->
        viewModel.onProfilePhotoSelected(uri?.toString())
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions(),
    ) { grants ->
        val granted = grants[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            grants[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (granted) viewModel.onCaptureLocation() else viewModel.onLocationPermissionDenied()
    }

    fun requestLocation() {
        if (LocationPermission.hasFineOrCoarse(context)) {
            viewModel.onCaptureLocation()
        } else {
            locationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                ),
            )
        }
    }

    LaunchedEffect(uiState.registrationComplete) {
        if (uiState.registrationComplete) {
            val profile = viewModel.buildProfile()
            viewModel.consumeRegistrationComplete()
            onRegistrationComplete(profile)
        }
    }

    val formContent: @Composable (Modifier) -> Unit = { modifier ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(bgColor)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.height(16.dp))
            if (!useDriverTheme) {
                AppLogo(size = 80.dp, applyTint = false)
                Spacer(Modifier.height(8.dp))
            }
            Text(stringResource(R.string.driver_register_subtitle), color = subtitleColor)
            Spacer(Modifier.height(20.dp))

            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .border(2.dp, accent, CircleShape)
                    .background(if (useDriverTheme) DriverSurface else OrangeLight.copy(alpha = 0.3f))
                    .clickable {
                        photoPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    },
                contentAlignment = Alignment.Center,
            ) {
                if (uiState.profilePhotoUri != null) {
                    AsyncImage(
                        model = uiState.profilePhotoUri,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                    )
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.AddAPhoto, null, tint = accent)
                        Text(
                            stringResource(R.string.driver_photo_hint),
                            style = MaterialTheme.typography.labelSmall,
                            color = titleColor,
                        )
                    }
                }
            }
            uiState.profilePhotoErrorKey?.let {
                Text(stringResource(R.string.error_profile_photo), color = ErrorRed, style = MaterialTheme.typography.bodySmall)
            }
            Spacer(Modifier.height(20.dp))

            DriverField(
                value = uiState.fullName,
                onValueChange = viewModel::onFullNameChange,
                label = stringResource(R.string.driver_full_name_hint),
                errorKey = uiState.fullNameErrorKey,
                colors = fieldColors,
            )
            Spacer(Modifier.height(12.dp))
            DriverField(
                value = uiState.cedula,
                onValueChange = viewModel::onCedulaChange,
                label = stringResource(R.string.driver_cedula_hint),
                errorKey = uiState.cedulaErrorKey,
                keyboardType = KeyboardType.Number,
                colors = fieldColors,
            )
            Spacer(Modifier.height(12.dp))
            DriverField(
                value = uiState.phone,
                onValueChange = viewModel::onPhoneChange,
                label = stringResource(R.string.driver_phone_hint),
                errorKey = uiState.phoneErrorKey,
                keyboardType = KeyboardType.Phone,
                colors = fieldColors,
            )
            Spacer(Modifier.height(12.dp))

            ExposedDropdownMenuBox(
                expanded = vehicleExpanded,
                onExpandedChange = { vehicleExpanded = it },
            ) {
                OutlinedTextField(
                    value = uiState.vehicleType,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(stringResource(R.string.driver_vehicle_type_hint)) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = vehicleExpanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor(),
                    isError = uiState.vehicleTypeErrorKey != null,
                    supportingText = uiState.vehicleTypeErrorKey?.let {
                        { DriverError(it) }
                    },
                    colors = fieldColors,
                )
                ExposedDropdownMenu(
                    expanded = vehicleExpanded,
                    onDismissRequest = { vehicleExpanded = false },
                ) {
                    vehicleTypes.forEach { type ->
                        DropdownMenuItem(
                            text = { Text(type) },
                            onClick = {
                                viewModel.onVehicleTypeChange(type)
                                vehicleExpanded = false
                            },
                        )
                    }
                }
            }
            Spacer(Modifier.height(12.dp))
            DriverField(
                value = uiState.vehiclePlate,
                onValueChange = viewModel::onVehiclePlateChange,
                label = stringResource(R.string.driver_vehicle_plate_hint),
                errorKey = uiState.vehiclePlateErrorKey,
                colors = fieldColors,
            )
            Spacer(Modifier.height(12.dp))
            DriverField(
                value = uiState.vehicleBrand,
                onValueChange = viewModel::onVehicleBrandChange,
                label = stringResource(R.string.driver_vehicle_brand_hint),
                errorKey = uiState.vehicleBrandErrorKey,
                colors = fieldColors,
            )
            Spacer(Modifier.height(12.dp))
            DriverField(
                value = uiState.vehicleColor,
                onValueChange = viewModel::onVehicleColorChange,
                label = stringResource(R.string.driver_vehicle_color_hint),
                errorKey = uiState.vehicleColorErrorKey,
                colors = fieldColors,
            )
            Spacer(Modifier.height(12.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = if (useDriverTheme) DriverSurface else OrangeLight.copy(alpha = 0.25f)),
                shape = RoundedCornerShape(12.dp),
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text(stringResource(R.string.driver_assigned_location), color = titleColor, fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold)
                    Spacer(Modifier.height(8.dp))
                    Text(
                        if (uiState.assignedLocation.isBlank()) {
                            stringResource(R.string.delivery_location_hint)
                        } else {
                            uiState.assignedLocation
                        },
                        color = subtitleColor,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    uiState.locationErrorKey?.let { key ->
                        Spacer(Modifier.height(4.dp))
                        com.example.mypetshop.ui.auth.AuthErrorText(key)
                    }
                    Spacer(Modifier.height(8.dp))
                    OutlinedButton(
                        onClick = ::requestLocation,
                        enabled = !uiState.isLoadingLocation,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = accent),
                    ) {
                        Icon(Icons.Default.Refresh, null, modifier = Modifier.size(18.dp))
                        Text(stringResource(R.string.capture_location_button), modifier = Modifier.padding(start = 6.dp))
                    }
                }
            }
            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Fingerprint, null, tint = accent)
                    Text(
                        stringResource(R.string.driver_biometric_label),
                        modifier = Modifier.padding(start = 8.dp),
                        color = titleColor,
                    )
                }
                Switch(
                    checked = uiState.biometricEnabled,
                    onCheckedChange = viewModel::onBiometricEnabledChange,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = BackgroundWhite,
                        checkedTrackColor = accent,
                    ),
                )
            }
            if (uiState.biometricEnabled) {
                Text(
                    stringResource(R.string.driver_biometric_hint),
                    color = subtitleColor,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 4.dp),
                )
            }
            Spacer(Modifier.height(24.dp))

            Button(
                onClick = {
                    val activity = context.findFragmentActivity()
                    if (uiState.biometricEnabled && activity != null && BiometricHelper.isAvailable(activity)) {
                        BiometricHelper.authenticate(
                            activity = activity,
                            title = biometricRegisterTitle,
                            subtitle = biometricRegisterSubtitle,
                            onSuccess = viewModel::onRegisterClick,
                            onError = { viewModel.onRegisterClick() },
                        )
                    } else {
                        viewModel.onRegisterClick()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = accent,
                    contentColor = if (useDriverTheme) DriverBackground else BackgroundWhite,
                ),
            ) {
                Text(stringResource(R.string.driver_register_submit))
            }
            Spacer(Modifier.height(24.dp))
        }
    }

    if (embedInScaffold) {
        Scaffold(
            containerColor = bgColor,
            topBar = {
                AppTopBar(
                    title = stringResource(R.string.driver_register_title),
                    showBack = true,
                    onBack = onBack,
                )
            },
        ) { padding -> formContent(Modifier.padding(padding)) }
    } else {
        formContent(Modifier)
    }
}

@Composable
private fun DriverField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    errorKey: String?,
    colors: androidx.compose.material3.TextFieldColors,
    keyboardType: KeyboardType = KeyboardType.Text,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        label = { Text(label) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        isError = errorKey != null,
        supportingText = errorKey?.let { { DriverError(it) } },
        colors = colors,
    )
}

@Composable
private fun DriverError(key: String) {
    val res = when (key) {
        "error_full_name" -> R.string.error_full_name
        "error_cedula" -> R.string.error_cedula
        "error_phone_required", "error_phone_invalid" -> R.string.error_phone_invalid
        "error_vehicle_type" -> R.string.error_vehicle_type
        "error_vehicle_plate" -> R.string.error_vehicle_plate
        "error_vehicle_brand" -> R.string.error_vehicle_brand
        "error_vehicle_color" -> R.string.error_vehicle_color
        else -> R.string.error_email_invalid
    }
    Text(stringResource(res), color = ErrorRed, style = MaterialTheme.typography.bodySmall)
}
