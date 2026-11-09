package com.example.mypetshop.ui.driver

import androidx.compose.foundation.background
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.TwoWheeler
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.mypetshop.R
import com.example.mypetshop.ui.theme.DriverAccent
import com.example.mypetshop.ui.theme.DriverBackground
import com.example.mypetshop.ui.theme.DriverMuted
import com.example.mypetshop.ui.theme.DriverOnSurface
import com.example.mypetshop.ui.theme.DriverSurface

@Composable
fun DriverRequirementsScreen(
    onStartRegistration: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DriverBackground)
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
    ) {
        Text(
            stringResource(R.string.driver_requirements_title),
            style = MaterialTheme.typography.headlineSmall,
            color = DriverOnSurface,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            stringResource(R.string.driver_requirements_subtitle),
            color = DriverMuted,
            style = MaterialTheme.typography.bodyMedium,
        )
        Spacer(Modifier.height(24.dp))

        RequirementCard(
            icon = Icons.Default.CameraAlt,
            title = stringResource(R.string.driver_req_photo_title),
            description = stringResource(R.string.driver_req_photo_desc),
        )
        RequirementCard(
            icon = Icons.Default.Badge,
            title = stringResource(R.string.driver_req_cedula_title),
            description = stringResource(R.string.driver_req_cedula_desc),
        )
        RequirementCard(
            icon = Icons.Default.LocationOn,
            title = stringResource(R.string.driver_req_location_title),
            description = stringResource(R.string.driver_req_location_desc),
        )
        RequirementCard(
            icon = Icons.Default.TwoWheeler,
            title = stringResource(R.string.driver_req_vehicle_title),
            description = stringResource(R.string.driver_req_vehicle_desc),
        )
        RequirementCard(
            icon = Icons.Default.Fingerprint,
            title = stringResource(R.string.driver_req_biometric_title),
            description = stringResource(R.string.driver_req_biometric_desc),
        )

        Spacer(Modifier.height(24.dp))
        Button(
            onClick = onStartRegistration,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = DriverAccent,
                contentColor = DriverBackground,
            ),
            shape = RoundedCornerShape(12.dp),
        ) {
            Text(stringResource(R.string.driver_start_registration))
        }
    }
}

@Composable
private fun RequirementCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = DriverSurface),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top,
        ) {
            Icon(icon, null, tint = DriverAccent, modifier = Modifier.size(28.dp))
            Column(modifier = Modifier.padding(start = 14.dp)) {
                Text(title, color = DriverOnSurface, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(4.dp))
                Text(description, color = DriverMuted, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
