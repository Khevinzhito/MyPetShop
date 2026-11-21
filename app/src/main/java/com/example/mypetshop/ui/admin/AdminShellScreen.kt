package com.example.mypetshop.ui.admin

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.mypetshop.R
import com.example.mypetshop.domain.model.ProductCategory
import com.example.mypetshop.domain.model.ProductPetType
import com.example.mypetshop.domain.model.SalesSummary
import com.example.mypetshop.domain.model.ShopProduct
import com.example.mypetshop.domain.model.UserOrder
import com.example.mypetshop.domain.model.UserOrderStatus
import com.example.mypetshop.ui.components.AppLogo
import com.example.mypetshop.ui.components.ProductImage
import com.example.mypetshop.ui.shop.formatPrice
import com.example.mypetshop.ui.theme.BackgroundWhite
import com.example.mypetshop.ui.theme.ErrorRed
import com.example.mypetshop.ui.theme.OrangeDark
import com.example.mypetshop.ui.theme.OrangeLight
import com.example.mypetshop.ui.theme.OrangePrimary
import com.example.mypetshop.ui.theme.TextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private enum class AdminTab { SALES, PRODUCTS, MESSAGES }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminShellScreen(
    onLogout: () -> Unit,
    productViewModel: AdminProductViewModel = hiltViewModel(),
    messagesViewModel: AdminMessagesViewModel = hiltViewModel(),
    salesViewModel: AdminSalesViewModel = hiltViewModel(),
) {
    var selectedTab by remember { mutableStateOf(AdminTab.SALES) }
    val salesSummary by salesViewModel.summary.collectAsState()

    Scaffold(
        containerColor = BackgroundWhite,
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Column {
                            Text(stringResource(R.string.admin_panel_title), color = BackgroundWhite)
                            Text(
                                stringResource(R.string.admin_panel_subtitle),
                                style = MaterialTheme.typography.labelSmall,
                                color = BackgroundWhite.copy(alpha = 0.85f),
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = onLogout) {
                            Icon(Icons.Default.Logout, contentDescription = stringResource(R.string.admin_logout), tint = BackgroundWhite)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = OrangePrimary),
                )
                AdminSalesToolbar(summary = salesSummary)
            }
        },
        bottomBar = {
            NavigationBar(containerColor = BackgroundWhite) {
                NavigationBarItem(
                    selected = selectedTab == AdminTab.SALES,
                    onClick = { selectedTab = AdminTab.SALES },
                    icon = { Icon(Icons.Default.ShowChart, null) },
                    label = { Text(stringResource(R.string.admin_tab_sales)) },
                )
                NavigationBarItem(
                    selected = selectedTab == AdminTab.PRODUCTS,
                    onClick = { selectedTab = AdminTab.PRODUCTS },
                    icon = { Icon(Icons.Default.Store, null) },
                    label = { Text(stringResource(R.string.admin_tab_products)) },
                )
                NavigationBarItem(
                    selected = selectedTab == AdminTab.MESSAGES,
                    onClick = { selectedTab = AdminTab.MESSAGES },
                    icon = { Icon(Icons.Default.Message, null) },
                    label = { Text(stringResource(R.string.admin_tab_messages)) },
                )
            }
        },
        floatingActionButton = {
            if (selectedTab == AdminTab.PRODUCTS) {
                FloatingActionButton(
                    onClick = productViewModel::openCreateForm,
                    containerColor = OrangePrimary,
                ) {
                    Icon(Icons.Default.Add, contentDescription = stringResource(R.string.admin_add_product), tint = BackgroundWhite)
                }
            }
        },
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            when (selectedTab) {
                AdminTab.SALES -> AdminSalesScreen(salesViewModel)
                AdminTab.PRODUCTS -> AdminProductListScreen(productViewModel)
                AdminTab.MESSAGES -> AdminMessagesScreen(messagesViewModel)
            }
        }
    }
}

@Composable
private fun AdminSalesToolbar(summary: SalesSummary) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(OrangeDark)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        AdminToolbarMetric(
            modifier = Modifier.weight(1f),
            icon = Icons.Default.AttachMoney,
            label = stringResource(R.string.admin_toolbar_sales),
            value = formatPrice(summary.completedSalesCents),
            subtitle = stringResource(R.string.admin_toolbar_orders_count, summary.completedOrders),
        )
        AdminToolbarMetric(
            modifier = Modifier.weight(1f),
            icon = Icons.Default.ShowChart,
            label = stringResource(R.string.admin_toolbar_profit),
            value = formatPrice(summary.estimatedProfitCents),
            subtitle = stringResource(R.string.admin_toolbar_margin_hint),
        )
    }
}

@Composable
private fun AdminToolbarMetric(
    label: String,
    value: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = BackgroundWhite.copy(alpha = 0.12f)),
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(icon, contentDescription = null, tint = BackgroundWhite, modifier = Modifier.height(28.dp))
            Column(Modifier.padding(start = 10.dp)) {
                Text(label, style = MaterialTheme.typography.labelSmall, color = BackgroundWhite.copy(alpha = 0.85f))
                Text(value, style = MaterialTheme.typography.titleMedium, color = BackgroundWhite, fontWeight = FontWeight.Bold)
                Text(subtitle, style = MaterialTheme.typography.labelSmall, color = BackgroundWhite.copy(alpha = 0.7f))
            }
        }
    }
}

@Composable
private fun AdminSalesScreen(viewModel: AdminSalesViewModel) {
    val summary by viewModel.summary.collectAsState()
    val orders by viewModel.orders.collectAsState()

    LazyColumn(
        contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Text(
                stringResource(R.string.admin_sales_title),
                style = MaterialTheme.typography.titleLarge,
                color = OrangeDark,
                fontWeight = FontWeight.Bold,
            )
            Text(stringResource(R.string.admin_sales_subtitle), color = TextSecondary)
            Spacer(Modifier.height(8.dp))
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                SalesStatCard(
                    modifier = Modifier.weight(1f),
                    title = stringResource(R.string.admin_sales_total),
                    value = formatPrice(summary.totalSalesCents),
                    detail = stringResource(R.string.admin_sales_total_orders, summary.totalOrders),
                )
                SalesStatCard(
                    modifier = Modifier.weight(1f),
                    title = stringResource(R.string.admin_sales_pending),
                    value = formatPrice(summary.pendingSalesCents),
                    detail = stringResource(R.string.admin_sales_pending_orders, summary.pendingOrders),
                )
            }
        }
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = OrangeLight.copy(alpha = 0.35f)),
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text(stringResource(R.string.admin_profit_title), color = OrangeDark, fontWeight = FontWeight.SemiBold)
                    Text(
                        formatPrice(summary.estimatedProfitCents),
                        style = MaterialTheme.typography.headlineMedium,
                        color = OrangePrimary,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(stringResource(R.string.admin_profit_hint), color = TextSecondary, style = MaterialTheme.typography.bodySmall)
                }
            }
        }
        item {
            Text(
                stringResource(R.string.admin_sales_recent_orders),
                style = MaterialTheme.typography.titleMedium,
                color = OrangeDark,
                fontWeight = FontWeight.SemiBold,
            )
        }
        if (orders.isEmpty()) {
            item { Text(stringResource(R.string.admin_sales_no_orders), color = TextSecondary) }
        } else {
            items(orders, key = { it.id }) { order ->
                AdminOrderCard(order)
            }
        }
    }
}

@Composable
private fun SalesStatCard(
    title: String,
    value: String,
    detail: String,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(Modifier.padding(14.dp)) {
            Text(title, style = MaterialTheme.typography.labelMedium, color = TextSecondary)
            Text(value, style = MaterialTheme.typography.titleLarge, color = OrangePrimary, fontWeight = FontWeight.Bold)
            Text(detail, style = MaterialTheme.typography.labelSmall, color = TextSecondary)
        }
    }
}

@Composable
private fun AdminOrderCard(order: UserOrder) {
    Card(shape = RoundedCornerShape(12.dp)) {
        Column(Modifier.padding(16.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(order.id, fontWeight = FontWeight.SemiBold, color = OrangeDark)
                Text(
                    if (order.status == UserOrderStatus.COMPLETED) {
                        stringResource(R.string.status_completed)
                    } else {
                        stringResource(R.string.status_pending)
                    },
                    color = if (order.status == UserOrderStatus.COMPLETED) OrangePrimary else TextSecondary,
                    style = MaterialTheme.typography.labelMedium,
                )
            }
            Text(order.clientName.ifBlank { order.clientEmail }, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            Text(order.createdAtLabel, style = MaterialTheme.typography.labelSmall, color = TextSecondary)
            Spacer(Modifier.height(8.dp))
            order.items.forEach { item ->
                Text(
                    "${item.quantity}x ${item.product.name}",
                    style = MaterialTheme.typography.bodySmall,
                    color = OrangeDark,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Spacer(Modifier.height(4.dp))
            Text(formatPrice(order.totalCents), color = OrangePrimary, fontWeight = FontWeight.Bold)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AdminProductListScreen(viewModel: AdminProductViewModel) {
    val products by viewModel.products.collectAsState()
    val formState by viewModel.formState.collectAsState()
    var deleteTarget by remember { mutableStateOf<ShopProduct?>(null) }

    LazyColumn(
        contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        item {
            AppLogo(size = 72.dp, applyTint = false)
            Spacer(Modifier.height(8.dp))
        }
        items(products, key = { it.id }) { product ->
            AdminProductCard(
                product = product,
                onEdit = { viewModel.openEditForm(product) },
                onDelete = { deleteTarget = product },
            )
        }
    }

    deleteTarget?.let { product ->
        AlertDialog(
            onDismissRequest = { deleteTarget = null },
            title = { Text(stringResource(R.string.admin_delete_product_title)) },
            text = { Text(stringResource(R.string.admin_delete_product_message, product.name)) },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteProduct(product.id)
                    deleteTarget = null
                }) { Text(stringResource(R.string.delete), color = ErrorRed) }
            },
            dismissButton = {
                TextButton(onClick = { deleteTarget = null }) { Text(stringResource(R.string.cancel)) }
            },
        )
    }

    if (formState.isVisible) {
        AdminProductFormDialog(viewModel, formState)
    }
}

@Composable
private fun AdminProductCard(
    product: ShopProduct,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = BackgroundWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            ProductImage(
                imageUri = product.imageUri,
                modifier = Modifier
                    .weight(0.35f)
                    .aspectRatio(1f),
            )
            Column(Modifier.weight(0.65f).padding(start = 12.dp)) {
                Text(product.name, fontWeight = FontWeight.SemiBold, color = OrangeDark, maxLines = 2, overflow = TextOverflow.Ellipsis)
                Text("${product.brand} · ${categoryLabel(product.category)}", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                Text(formatPrice(product.priceCents), color = OrangePrimary, fontWeight = FontWeight.Bold)
                if (!product.isActive) {
                    Text(stringResource(R.string.admin_product_inactive), color = ErrorRed, style = MaterialTheme.typography.labelSmall)
                }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    IconButton(onClick = onEdit) { Icon(Icons.Default.Edit, null, tint = OrangePrimary) }
                    IconButton(onClick = onDelete) { Icon(Icons.Default.Delete, null, tint = ErrorRed) }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun AdminProductFormDialog(
    viewModel: AdminProductViewModel,
    formState: ProductFormState,
) {
    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = OrangePrimary,
        unfocusedBorderColor = OrangeLight,
        focusedLabelColor = OrangeDark,
    )
    val imagePicker = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        viewModel.onImageUriChange(uri?.toString())
    }

    AlertDialog(
        onDismissRequest = viewModel::closeForm,
        title = {
            Text(
                if (formState.id.isBlank()) stringResource(R.string.admin_add_product)
                else stringResource(R.string.admin_edit_product),
            )
        },
        text = {
            Column(
                Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                ProductImage(
                    imageUri = formState.imageUri,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1.6f),
                )
                OutlinedButton(onClick = {
                    imagePicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                }, modifier = Modifier.fillMaxWidth()) {
                    Icon(Icons.Default.Image, null, tint = OrangePrimary)
                    Text(stringResource(R.string.admin_pick_image), modifier = Modifier.padding(start = 8.dp), color = OrangePrimary)
                }
                OutlinedTextField(formState.name, viewModel::onNameChange, label = { Text(stringResource(R.string.admin_product_name)) }, modifier = Modifier.fillMaxWidth(), colors = fieldColors, singleLine = true)
                OutlinedTextField(formState.description, viewModel::onDescriptionChange, label = { Text(stringResource(R.string.admin_product_description)) }, modifier = Modifier.fillMaxWidth(), colors = fieldColors, minLines = 2)
                OutlinedTextField(formState.brand, viewModel::onBrandChange, label = { Text(stringResource(R.string.admin_product_brand)) }, modifier = Modifier.fillMaxWidth(), colors = fieldColors, singleLine = true)
                OutlinedTextField(formState.priceText, viewModel::onPriceChange, label = { Text(stringResource(R.string.admin_product_price)) }, modifier = Modifier.fillMaxWidth(), colors = fieldColors, singleLine = true)
                Text(stringResource(R.string.admin_product_category), color = OrangeDark)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ProductCategory.entries.forEach { category ->
                        FilterChip(
                            selected = formState.category == category,
                            onClick = { viewModel.onCategoryChange(category) },
                            label = { Text(categoryLabel(category)) },
                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = OrangeLight),
                        )
                    }
                }
                Text(stringResource(R.string.admin_product_pet_type), color = OrangeDark)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ProductPetType.entries.forEach { petType ->
                        FilterChip(
                            selected = formState.petType == petType,
                            onClick = { viewModel.onPetTypeChange(petType) },
                            label = { Text(petTypeLabel(petType)) },
                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = OrangeLight),
                        )
                    }
                }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(stringResource(R.string.admin_product_active), color = OrangeDark)
                    Switch(checked = formState.isActive, onCheckedChange = viewModel::onActiveChange)
                }
                formState.errorKey?.let { AdminFormError(it) }
            }
        },
        confirmButton = {
            TextButton(onClick = viewModel::saveProduct) {
                Text(stringResource(R.string.save), color = OrangePrimary)
            }
        },
        dismissButton = {
            TextButton(onClick = viewModel::closeForm) { Text(stringResource(R.string.cancel)) }
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun AdminMessagesScreen(viewModel: AdminMessagesViewModel) {
    val messages by viewModel.messages.collectAsState()
    val compose by viewModel.composeState.collectAsState()
    val clients = remember { viewModel.availableClients() }
    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = OrangePrimary,
        unfocusedBorderColor = OrangeLight,
        focusedLabelColor = OrangeDark,
    )

    if (compose.sentSuccess) {
        AlertDialog(
            onDismissRequest = viewModel::resetCompose,
            title = { Text(stringResource(R.string.admin_message_sent_title)) },
            text = { Text(stringResource(R.string.admin_message_sent_body)) },
            confirmButton = { TextButton(onClick = viewModel::resetCompose) { Text(stringResource(R.string.ok)) } },
        )
    }

    LazyColumn(
        contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Text(stringResource(R.string.admin_compose_message), style = MaterialTheme.typography.titleMedium, color = OrangeDark, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(8.dp))
            Card(colors = CardDefaults.cardColors(containerColor = OrangeLight.copy(alpha = 0.25f))) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(stringResource(R.string.admin_select_client), color = OrangeDark)
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        clients.forEach { (email, name) ->
                            FilterChip(
                                selected = compose.clientEmail == email,
                                onClick = { viewModel.onClientSelected(email, name) },
                                label = { Text(name) },
                                colors = FilterChipDefaults.filterChipColors(selectedContainerColor = OrangeLight),
                            )
                        }
                    }
                    OutlinedTextField(compose.orderId, viewModel::onOrderIdChange, label = { Text(stringResource(R.string.admin_message_order)) }, modifier = Modifier.fillMaxWidth(), colors = fieldColors, singleLine = true)
                    OutlinedTextField(compose.subject, viewModel::onSubjectChange, label = { Text(stringResource(R.string.admin_message_subject)) }, modifier = Modifier.fillMaxWidth(), colors = fieldColors, singleLine = true)
                    OutlinedTextField(compose.body, viewModel::onBodyChange, label = { Text(stringResource(R.string.admin_message_body)) }, modifier = Modifier.fillMaxWidth(), colors = fieldColors, minLines = 4)
                    compose.errorKey?.let { AdminFormError(it) }
                    Button(onClick = viewModel::sendMessage, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary)) {
                        Text(stringResource(R.string.admin_send_message), color = BackgroundWhite)
                    }
                }
            }
        }
        item {
            Text(stringResource(R.string.admin_message_history), style = MaterialTheme.typography.titleMedium, color = OrangeDark, fontWeight = FontWeight.SemiBold)
        }
        if (messages.isEmpty()) {
            item { Text(stringResource(R.string.admin_no_messages), color = TextSecondary) }
        } else {
            items(messages, key = { it.id }) { message ->
                Card(shape = RoundedCornerShape(12.dp)) {
                    Column(Modifier.padding(16.dp)) {
                        Text(message.subject, fontWeight = FontWeight.SemiBold, color = OrangeDark)
                        Text("${message.clientName} · ${message.clientEmail}", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                        message.orderId?.let { Text(stringResource(R.string.admin_message_order_label, it), style = MaterialTheme.typography.labelSmall, color = TextSecondary) }
                        Spacer(Modifier.height(8.dp))
                        Text(message.body, color = TextSecondary)
                        Spacer(Modifier.height(4.dp))
                        Text(formatDate(message.sentAtMillis), style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminFormError(key: String) {
    val res = when (key) {
        "error_product_name" -> R.string.error_product_name
        "error_product_description" -> R.string.error_product_description
        "error_product_brand" -> R.string.error_product_brand
        "error_product_price" -> R.string.error_product_price
        "error_message_client" -> R.string.error_message_client
        "error_message_subject" -> R.string.error_message_subject
        "error_message_body" -> R.string.error_message_body
        else -> R.string.error_product_name
    }
    Text(stringResource(res), color = ErrorRed, style = MaterialTheme.typography.bodySmall)
}

@Composable
private fun categoryLabel(category: ProductCategory): String = when (category) {
    ProductCategory.PREMIUM -> stringResource(R.string.category_premium)
    ProductCategory.STANDARD -> stringResource(R.string.category_standard)
    ProductCategory.SNACK -> stringResource(R.string.category_snack)
}

@Composable
private fun petTypeLabel(petType: ProductPetType): String = when (petType) {
    ProductPetType.DOG -> stringResource(R.string.pet_type_dog)
    ProductPetType.CAT -> stringResource(R.string.pet_type_cat)
    ProductPetType.BOTH -> stringResource(R.string.pet_type_both)
}

private fun formatDate(millis: Long): String =
    SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("es", "CO")).format(Date(millis))
