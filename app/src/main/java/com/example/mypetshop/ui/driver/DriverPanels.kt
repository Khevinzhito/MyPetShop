package com.example.mypetshop.ui.driver

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.mypetshop.R
import com.example.mypetshop.domain.model.DriverProfile
import com.example.mypetshop.ui.auth.AuthErrorText
import com.example.mypetshop.ui.theme.DriverAccent
import com.example.mypetshop.ui.theme.DriverBackground
import com.example.mypetshop.ui.theme.DriverMuted
import com.example.mypetshop.ui.theme.DriverOnSurface
import com.example.mypetshop.ui.theme.DriverSurface

@Composable
fun DriverToolsPanel(
    driverLocation: String,
    onRefreshLocation: (() -> Unit)? = null,
    showRefreshLocation: Boolean = false,
    isLoadingLocation: Boolean = false,
    locationErrorKey: String? = null,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
    ) {
        Text(
            stringResource(R.string.driver_tools_title),
            color = DriverOnSurface,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.height(16.dp))
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = DriverSurface),
        ) {
            Column(Modifier.padding(16.dp)) {
                Icon(Icons.Default.LocationOn, null, tint = DriverAccent)
                Spacer(Modifier.height(8.dp))
                Text(
                    stringResource(R.string.driver_current_location),
                    color = DriverMuted,
                    style = MaterialTheme.typography.labelMedium,
                )
                Text(
                    if (driverLocation.isBlank()) stringResource(R.string.delivery_location_hint) else driverLocation,
                    color = DriverOnSurface,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(top = 4.dp),
                )
                locationErrorKey?.let { key ->
                    Spacer(Modifier.height(4.dp))
                    AuthErrorText(key)
                }
                if (showRefreshLocation && onRefreshLocation != null) {
                    Spacer(Modifier.height(12.dp))
                    Button(
                        onClick = onRefreshLocation,
                        enabled = !isLoadingLocation,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = DriverAccent,
                            contentColor = DriverBackground,
                        ),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        if (isLoadingLocation) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                color = DriverBackground,
                                strokeWidth = 2.dp,
                            )
                        } else {
                            Icon(Icons.Default.Refresh, null)
                            Text(
                                stringResource(R.string.capture_location_button),
                                modifier = Modifier.padding(start = 8.dp),
                            )
                        }
                    }
                }
            }
        }
        Spacer(Modifier.height(12.dp))
        Text(
            stringResource(R.string.driver_tools_hint),
            color = DriverMuted,
            style = MaterialTheme.typography.bodySmall,
        )
    }
}

@Composable
fun DriverProfilePanel(
    driver: DriverProfile,
    onBackToStore: (() -> Unit)?,
    onLogout: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
            .background(DriverBackground),
    ) {
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = DriverSurface),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(Modifier.padding(16.dp)) {
                if (driver.profilePhotoUri != null) {
                    AsyncImage(
                        model = driver.profilePhotoUri,
                        contentDescription = null,
                        modifier = Modifier
                            .size(80.dp)
                            .padding(bottom = 8.dp),
                    )
                }
                ProfileRow(stringResource(R.string.driver_full_name_hint), driver.fullName)
                ProfileRow(stringResource(R.string.driver_cedula_hint), driver.cedula.ifBlank { "—" })
                ProfileRow(stringResource(R.string.driver_phone_hint), driver.phone.ifBlank { "—" })
                ProfileRow(stringResource(R.string.driver_vehicle_type_hint), driver.vehicleType.ifBlank { "—" })
                ProfileRow(stringResource(R.string.driver_vehicle_plate_hint), driver.vehiclePlate.ifBlank { "—" })
                ProfileRow(stringResource(R.string.driver_current_location), driver.assignedLocation.ifBlank { "—" })
            }
        }
        Spacer(Modifier.height(16.dp))
        onBackToStore?.let { back ->
            Button(
                onClick = back,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = DriverAccent, contentColor = DriverBackground),
            ) {
                Text(stringResource(R.string.back_to_store))
            }
            Spacer(Modifier.height(8.dp))
        }
        Button(
            onClick = onLogout,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = DriverMuted, contentColor = DriverBackground),
        ) {
            Text(stringResource(R.string.logout))
        }
    }
}

@Composable
private fun ProfileRow(label: String, value: String) {
    Text(label, color = DriverMuted, style = MaterialTheme.typography.labelSmall)
    Text(value, color = DriverOnSurface, fontWeight = FontWeight.Medium, modifier = Modifier.padding(bottom = 8.dp))
}
