package com.example.mypetshop.ui.shop

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.mypetshop.R
import com.example.mypetshop.domain.model.CartItem
import com.example.mypetshop.ui.auth.AuthErrorText
import com.example.mypetshop.ui.theme.BackgroundWhite
import com.example.mypetshop.ui.theme.OrangeDark
import com.example.mypetshop.ui.theme.OrangeLight
import com.example.mypetshop.ui.theme.OrangePrimary
import com.example.mypetshop.ui.theme.TextSecondary
import com.example.mypetshop.util.LocationPermission

@Composable
fun CartScreen(
    onOrderPlaced: () -> Unit,
    viewModel: CartViewModel = hiltViewModel(),
) {
    val cartItems by viewModel.cartItems.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions(),
    ) { grants ->
        val granted = grants[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            grants[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (granted) {
            viewModel.captureDeliveryLocation()
        } else {
            viewModel.onLocationPermissionDenied()
        }
    }

    fun requestLocation() {
        if (LocationPermission.hasFineOrCoarse(context)) {
            viewModel.captureDeliveryLocation()
        } else {
            locationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                ),
            )
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        Text(
            stringResource(R.string.cart_title),
            style = MaterialTheme.typography.headlineSmall,
            color = OrangeDark,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.height(16.dp))

        if (cartItems.isEmpty()) {
            Text(stringResource(R.string.cart_empty), color = TextSecondary, modifier = Modifier.padding(top = 32.dp))
        } else {
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(cartItems, key = { it.product.id }) { item ->
                    CartItemRow(
                        item = item,
                        onIncrease = { viewModel.updateQuantity(item.product.id, item.quantity + 1) },
                        onDecrease = { viewModel.updateQuantity(item.product.id, item.quantity - 1) },
                        onRemove = { viewModel.removeFromCart(item.product.id) },
                    )
                    Spacer(Modifier.height(8.dp))
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = OrangeLight.copy(alpha = 0.25f)),
            ) {
                Column(Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocationOn, null, tint = OrangePrimary)
                        Text(
                            stringResource(R.string.delivery_location_title),
                            modifier = Modifier.padding(start = 8.dp),
                            fontWeight = FontWeight.SemiBold,
                            color = OrangeDark,
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                    val location = uiState.deliveryLocation
                    if (location != null) {
                        Text(location.formattedAddress, color = TextSecondary, style = MaterialTheme.typography.bodyMedium)
                        Text(
                            stringResource(R.string.delivery_coords, location.latitude, location.longitude),
                            color = TextSecondary,
                            style = MaterialTheme.typography.bodySmall,
                        )
                    } else {
                        Text(stringResource(R.string.delivery_location_hint), color = TextSecondary, style = MaterialTheme.typography.bodySmall)
                    }
                    uiState.locationErrorKey?.let { key ->
                        Spacer(Modifier.height(4.dp))
                        AuthErrorText(key)
                    }
                    Spacer(Modifier.height(8.dp))
                    OutlinedButton(
                        onClick = ::requestLocation,
                        enabled = !uiState.isLoadingLocation,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = OrangePrimary),
                    ) {
                        if (uiState.isLoadingLocation) {
                            CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp, color = OrangePrimary)
                        } else {
                            Text(stringResource(R.string.capture_location_button))
                        }
                    }
                }
            }
            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(stringResource(R.string.cart_total), fontWeight = FontWeight.Bold, color = OrangeDark)
                Text(formatPrice(viewModel.totalCents), fontWeight = FontWeight.Bold, color = OrangePrimary, style = MaterialTheme.typography.titleLarge)
            }
            Spacer(Modifier.height(12.dp))
            Button(
                onClick = {
                    viewModel.checkout()
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary, contentColor = BackgroundWhite),
            ) {
                Text(stringResource(R.string.checkout_button))
            }
        }
    }

    if (uiState.showCheckoutDialog) {
        CheckoutDialog(
            uiState = uiState,
            subtotalCents = viewModel.totalCents,
            onDismiss = viewModel::closeCheckoutDialog,
            onSelectPayment = viewModel::setPaymentMethod,
            onConfirm = {
                if (viewModel.confirmPaymentAndCheckout()) {
                    onOrderPlaced()
                }
            },
        )
    }
}

@Composable
private fun CheckoutDialog(
    uiState: CartUiState,
    subtotalCents: Long,
    onDismiss: () -> Unit,
    onSelectPayment: (String) -> Unit,
    onConfirm: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.checkout_dialog_title), fontWeight = FontWeight.Bold, color = OrangeDark) },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.subtotal_label) + " " + formatPrice(subtotalCents), fontWeight = FontWeight.Medium)
                Spacer(Modifier.height(4.dp))
                Text(stringResource(R.string.shipping_distance_label, uiState.distanceKm), style = MaterialTheme.typography.bodyMedium)
                Text(stringResource(R.string.shipping_cost_label, formatPrice(uiState.shippingCents)), style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                Spacer(Modifier.height(8.dp))
                Text(stringResource(R.string.total_with_shipping_label), fontWeight = FontWeight.Bold)
                Text(formatPrice(uiState.totalWithShipping(subtotalCents)), fontWeight = FontWeight.Bold, color = OrangePrimary, style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(16.dp))
                Text(stringResource(R.string.payment_method_title), fontWeight = FontWeight.SemiBold, color = OrangeDark)
                Spacer(Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = { onSelectPayment("Tarjeta") },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = if (uiState.paymentMethod == "Tarjeta") OrangePrimary.copy(alpha = 0.15f) else BackgroundWhite,
                            contentColor = OrangePrimary,
                        ),
                    ) {
                        Text(stringResource(R.string.payment_card), style = MaterialTheme.typography.labelSmall)
                    }
                    OutlinedButton(
                        onClick = { onSelectPayment("Nequi") },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = if (uiState.paymentMethod == "Nequi") OrangePrimary.copy(alpha = 0.15f) else BackgroundWhite,
                            contentColor = OrangePrimary,
                        ),
                    ) {
                        Text(stringResource(R.string.payment_nequi), style = MaterialTheme.typography.labelSmall)
                    }
                }
                uiState.paymentErrorKey?.let { key ->
                    Spacer(Modifier.height(8.dp))
                    AuthErrorText(key)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary, contentColor = BackgroundWhite),
            ) {
                Text(stringResource(R.string.confirm_payment_button))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        },
    )
}

@Composable
private fun CartItemRow(
    item: CartItem,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    onRemove: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = BackgroundWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(item.product.name, fontWeight = FontWeight.Medium, color = OrangeDark)
                Text(formatPrice(item.product.priceCents), color = OrangePrimary)
            }
            IconButton(onClick = onDecrease) {
                Icon(Icons.Default.Remove, null, tint = OrangePrimary)
            }
            Text(item.quantity.toString(), fontWeight = FontWeight.Bold)
            IconButton(onClick = onIncrease) {
                Icon(Icons.Default.Add, null, tint = OrangePrimary)
            }
            IconButton(onClick = onRemove) {
                Icon(Icons.Default.Delete, null, tint = OrangePrimary)
            }
        }
    }
}
