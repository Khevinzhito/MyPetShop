package com.example.mypetshop.ui.driver

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.mypetshop.R
import com.example.mypetshop.domain.model.DeliveryOrder
import com.example.mypetshop.ui.shop.formatPrice
import com.example.mypetshop.domain.model.DeliveryStatus
import com.example.mypetshop.ui.components.AppTopBar
import com.example.mypetshop.util.BiometricHelper
import com.example.mypetshop.util.findFragmentActivity
import com.example.mypetshop.ui.theme.DriverAccent
import com.example.mypetshop.ui.theme.DriverBackground
import com.example.mypetshop.ui.theme.DriverMuted
import com.example.mypetshop.ui.theme.DriverOnSurface
import com.example.mypetshop.ui.theme.DriverSurface
import com.example.mypetshop.ui.theme.BackgroundWhite
import com.example.mypetshop.ui.theme.ErrorRed
import com.example.mypetshop.ui.theme.OrangeDark
import com.example.mypetshop.ui.theme.OrangeLight
import com.example.mypetshop.ui.theme.OrangePrimary
import com.example.mypetshop.ui.theme.TextPrimary
import com.example.mypetshop.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DriverDashboardScreen(
    driverName: String,
    driverLocation: String = "",
    embedInScaffold: Boolean = true,
    useDriverTheme: Boolean = false,
    viewModel: DriverDashboardViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val biometricTitle = stringResource(R.string.driver_confirm_biometric_title)
    val biometricSubtitle = stringResource(R.string.driver_confirm_biometric_subtitle)
    val selectedOrder = uiState.orders.find { it.id == uiState.selectedOrderId }
    val titleColor = if (useDriverTheme) DriverOnSurface else OrangeDark
    val cardBg = if (useDriverTheme) DriverSurface else BackgroundWhite
    val accent = if (useDriverTheme) DriverAccent else OrangePrimary
    val muted = if (useDriverTheme) DriverMuted else TextSecondary
    val textPrimary = if (useDriverTheme) DriverOnSurface else TextPrimary

    val content: @Composable (Modifier) -> Unit = { modifier ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .then(if (useDriverTheme) Modifier.background(DriverBackground) else Modifier)
                .padding(horizontal = 16.dp),
        ) {
            item {
                Text(
                    text = stringResource(R.string.driver_welcome, driverName),
                    style = MaterialTheme.typography.titleMedium,
                    color = titleColor,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(top = 12.dp, bottom = 4.dp),
                )
                Text(
                    text = stringResource(R.string.driver_select_order_hint),
                    style = MaterialTheme.typography.bodySmall,
                    color = muted,
                    modifier = Modifier.padding(bottom = 12.dp),
                )
            }

            items(uiState.orders, key = { it.id }) { order ->
                OrderCard(
                    order = order,
                    selected = order.id == uiState.selectedOrderId,
                    onClick = { viewModel.selectOrder(order.id) },
                    useDriverTheme = useDriverTheme,
                )
                Spacer(Modifier.height(8.dp))
            }

            item {
                selectedOrder?.let { order ->
                    Spacer(Modifier.height(8.dp))
                    WhatsAppLocationCard(order = order, accent = accent, useDriverTheme = useDriverTheme)
                    Spacer(Modifier.height(12.dp))
                    LocationEvidenceCard(
                        order = order,
                        driverLocation = driverLocation.ifBlank { order.driverAddress },
                        useDriverTheme = useDriverTheme,
                    )
                    Spacer(Modifier.height(12.dp))
                    ConfirmDeliverySection(
                        order = order,
                        accent = accent,
                        titleColor = titleColor,
                        onConfirmMessage = viewModel::showMessageConfirmation,
                        onConfirmBiometric = {
                            val activity = context.findFragmentActivity()
                            if (activity != null && BiometricHelper.isAvailable(activity)) {
                                BiometricHelper.authenticate(
                                    activity = activity,
                                    title = biometricTitle,
                                    subtitle = biometricSubtitle,
                                    onSuccess = viewModel::confirmByBiometric,
                                    onError = {},
                                )
                            }
                        },
                    )
                    uiState.confirmationMessage?.let { key ->
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = when (key) {
                                "delivery_confirmed_message" -> stringResource(R.string.delivery_confirmed_message)
                                "delivery_confirmed_biometric" -> stringResource(R.string.delivery_confirmed_biometric)
                                else -> key
                            },
                            color = accent,
                            fontWeight = FontWeight.Medium,
                        )
                    }
                    Spacer(Modifier.height(24.dp))
                }
            }
        }
    }

    if (embedInScaffold) {
        Scaffold(
            containerColor = if (useDriverTheme) DriverBackground else BackgroundWhite,
            topBar = { AppTopBar(title = stringResource(R.string.driver_dashboard_title)) },
        ) { padding -> content(Modifier.padding(padding)) }
    } else {
        content(Modifier)
    }

    if (uiState.showMessageConfirmDialog) {
        AlertDialog(
            onDismissRequest = viewModel::dismissMessageDialog,
            title = { Text(stringResource(R.string.confirm_by_message_title)) },
            text = {
                Column {
                    Text(stringResource(R.string.confirm_by_message_body, uiState.messageCode))
                    Spacer(Modifier.height(12.dp))
                    OutlinedTextField(
                        value = uiState.messageCodeInput,
                        onValueChange = viewModel::onMessageCodeInputChange,
                        label = { Text(stringResource(R.string.confirm_code_hint)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    uiState.confirmationError?.let {
                        Text(
                            stringResource(R.string.error_message_code),
                            color = ErrorRed,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(top = 4.dp),
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = viewModel::confirmByMessage) {
                    Text(stringResource(R.string.confirm_delivery_button))
                }
            },
            dismissButton = {
                TextButton(onClick = viewModel::dismissMessageDialog) {
                    Text(stringResource(R.string.recipe_dialog_close))
                }
            },
        )
    }
}

@Composable
private fun LocationEvidenceCard(
    order: DeliveryOrder,
    driverLocation: String,
    useDriverTheme: Boolean,
) {
    val cardColor = if (useDriverTheme) DriverSurface else OrangeLight.copy(alpha = 0.25f)
    val titleColor = if (useDriverTheme) DriverOnSurface else OrangeDark
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(
                stringResource(R.string.location_evidence_title),
                fontWeight = FontWeight.Bold,
                color = titleColor,
            )
            Spacer(Modifier.height(12.dp))
            LocationRow(
                icon = Icons.Default.LocationOn,
                label = stringResource(R.string.client_location_label),
                address = order.clientAddress,
                useDriverTheme = useDriverTheme,
            )
            Spacer(Modifier.height(10.dp))
            LocationRow(
                icon = Icons.Default.MyLocation,
                label = stringResource(R.string.driver_location_label),
                address = driverLocation,
                useDriverTheme = useDriverTheme,
            )
        }
    }
}

@Composable
private fun LocationRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    address: String,
    useDriverTheme: Boolean,
) {
    val accent = if (useDriverTheme) DriverAccent else OrangePrimary
    val titleColor = if (useDriverTheme) DriverOnSurface else OrangeDark
    val textColor = if (useDriverTheme) DriverOnSurface else TextPrimary
    Row(verticalAlignment = Alignment.Top) {
        Icon(icon, null, tint = accent, modifier = Modifier.size(22.dp))
        Spacer(Modifier.width(10.dp))
        Column {
            Text(label, fontWeight = FontWeight.SemiBold, color = titleColor, style = MaterialTheme.typography.labelLarge)
            Text(address, color = textColor, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
private fun OrderCard(
    order: DeliveryOrder,
    selected: Boolean,
    onClick: () -> Unit,
    useDriverTheme: Boolean,
) {
    val selectedBg = if (useDriverTheme) DriverAccent.copy(alpha = 0.15f) else OrangeLight.copy(alpha = 0.5f)
    val cardBg = if (useDriverTheme) DriverSurface else BackgroundWhite
    val accent = if (useDriverTheme) DriverAccent else OrangePrimary
    val titleColor = if (useDriverTheme) DriverOnSurface else OrangeDark
    val muted = if (useDriverTheme) DriverMuted else TextSecondary
    val textColor = if (useDriverTheme) DriverOnSurface else TextPrimary
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) selectedBg else cardBg,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (selected) 6.dp else 2.dp),
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(order.id, fontWeight = FontWeight.Bold, color = titleColor)
                StatusChip(order.status, useDriverTheme)
            }
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    stringResource(R.string.driver_order_total, formatPrice(order.totalCents)),
                    fontWeight = FontWeight.Bold,
                    color = accent,
                    style = MaterialTheme.typography.titleMedium,
                )
                Text(
                    stringResource(R.string.driver_payment_method, order.paymentMethod),
                    color = muted,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Medium,
                )
            }
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Call, null, tint = accent, modifier = Modifier.size(18.dp))
                Text(order.clientPhoneMasked, modifier = Modifier.padding(start = 8.dp), color = textColor)
            }
            Spacer(Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.ShoppingBag, null, tint = accent, modifier = Modifier.size(18.dp))
                Text(order.productName, modifier = Modifier.padding(start = 8.dp), color = muted, style = MaterialTheme.typography.bodySmall)
            }
            Spacer(Modifier.height(4.dp))
            Text(
                stringResource(R.string.confirm_by_time, order.confirmByTime),
                color = titleColor,
                style = MaterialTheme.typography.labelMedium,
            )
        }
    }
}

@Composable
private fun StatusChip(status: DeliveryStatus, useDriverTheme: Boolean) {
    val (label, color) = when (status) {
        DeliveryStatus.PENDING -> stringResource(R.string.status_pending) to if (useDriverTheme) DriverMuted else TextSecondary
        DeliveryStatus.IN_TRANSIT -> stringResource(R.string.status_in_transit) to if (useDriverTheme) DriverAccent else OrangePrimary
        DeliveryStatus.DELIVERED -> stringResource(R.string.status_delivered) to if (useDriverTheme) DriverOnSurface else OrangeDark
    }
    Text(
        text = label,
        color = color,
        style = MaterialTheme.typography.labelSmall,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier
            .background(color.copy(alpha = 0.12f), RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp),
    )
}

@Composable
private fun WhatsAppLocationCard(
    order: DeliveryOrder,
    accent: androidx.compose.ui.graphics.Color,
    useDriverTheme: Boolean,
) {
    val headerBg = if (useDriverTheme) DriverAccent else OrangePrimary
    val cardBg = if (useDriverTheme) DriverSurface else BackgroundWhite
    val bubbleBg = if (useDriverTheme) DriverBackground else OrangeLight.copy(alpha = 0.25f)
    val textColor = if (useDriverTheme) DriverOnSurface else TextPrimary
    val muted = if (useDriverTheme) DriverMuted else TextSecondary
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp, bottomStart = 4.dp, bottomEnd = 16.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(headerBg)
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(if (useDriverTheme) DriverBackground else BackgroundWhite),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons.Default.Call, null, tint = headerBg)
                }
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(order.clientPhoneMasked, color = if (useDriverTheme) DriverBackground else BackgroundWhite, fontWeight = FontWeight.Bold)
                    Text(stringResource(R.string.client_location_label), color = (if (useDriverTheme) DriverBackground else BackgroundWhite).copy(alpha = 0.85f), style = MaterialTheme.typography.bodySmall)
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
                    .background(bubbleBg, RoundedCornerShape(12.dp))
                    .padding(12.dp),
            ) {
                Column {
                    Text(
                        order.clientAddress,
                        color = textColor,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        stringResource(R.string.order_detail_message, order.productName, order.confirmByTime),
                        color = muted,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier
                            .background(if (useDriverTheme) DriverSurface else BackgroundWhite, RoundedCornerShape(8.dp))
                            .padding(10.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun ConfirmDeliverySection(
    order: DeliveryOrder,
    accent: androidx.compose.ui.graphics.Color,
    titleColor: androidx.compose.ui.graphics.Color,
    onConfirmMessage: () -> Unit,
    onConfirmBiometric: () -> Unit,
) {
    if (order.status == DeliveryStatus.DELIVERED) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(Icons.Default.CheckCircle, null, tint = accent)
            Text(
                stringResource(R.string.order_already_delivered),
                modifier = Modifier.padding(start = 8.dp),
                color = titleColor,
                fontWeight = FontWeight.Medium,
            )
        }
        return
    }

    Text(
        stringResource(R.string.confirm_delivery_title),
        style = MaterialTheme.typography.titleSmall,
        color = titleColor,
        fontWeight = FontWeight.SemiBold,
    )
    Spacer(Modifier.height(8.dp))
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Button(
            onClick = onConfirmMessage,
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.buttonColors(containerColor = accent, contentColor = if (accent == DriverAccent) DriverBackground else BackgroundWhite),
        ) {
            Icon(Icons.AutoMirrored.Filled.Message, null, modifier = Modifier.size(18.dp))
            Text(stringResource(R.string.confirm_by_message), modifier = Modifier.padding(start = 4.dp), style = MaterialTheme.typography.labelMedium)
        }
        OutlinedButton(
            onClick = onConfirmBiometric,
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = accent),
        ) {
            Icon(Icons.Default.Fingerprint, null, modifier = Modifier.size(18.dp))
            Text(stringResource(R.string.confirm_by_fingerprint), modifier = Modifier.padding(start = 4.dp), style = MaterialTheme.typography.labelMedium)
        }
    }
}
