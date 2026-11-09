package com.example.mypetshop.ui.shop

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.mypetshop.R
import com.example.mypetshop.domain.model.UserOrder
import com.example.mypetshop.domain.model.UserOrderStatus
import com.example.mypetshop.ui.theme.BackgroundWhite
import com.example.mypetshop.ui.theme.OrangeDark
import com.example.mypetshop.ui.theme.OrangePrimary
import com.example.mypetshop.ui.theme.TextSecondary

@Composable
fun OrdersScreen(
    viewModel: OrdersViewModel = hiltViewModel(),
) {
    val orders by viewModel.orders.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) }
    val pending = orders.filter { it.status == UserOrderStatus.PENDING }
    val completed = orders.filter { it.status == UserOrderStatus.COMPLETED }
    val displayed = if (selectedTab == 0) pending else completed

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            stringResource(R.string.orders_title),
            style = MaterialTheme.typography.headlineSmall,
            color = OrangeDark,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.height(16.dp))
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = BackgroundWhite,
            contentColor = OrangePrimary,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = OrangePrimary,
                )
            },
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text(stringResource(R.string.orders_pending_tab, pending.size)) },
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text(stringResource(R.string.orders_completed_tab, completed.size)) },
            )
        }
        Spacer(Modifier.height(12.dp))

        if (displayed.isEmpty()) {
            Text(
                if (selectedTab == 0) stringResource(R.string.orders_pending_empty)
                else stringResource(R.string.orders_completed_empty),
                color = TextSecondary,
                modifier = Modifier.padding(top = 24.dp),
            )
        } else {
            LazyColumn {
                items(displayed, key = { it.id }) { order ->
                    OrderCard(
                        order = order,
                        onMarkCompleted = { viewModel.completeOrder(order.id) },
                    )
                    Spacer(Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
private fun OrderCard(
    order: UserOrder,
    onMarkCompleted: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = BackgroundWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(order.id, fontWeight = FontWeight.Bold, color = OrangeDark)
            Text(order.createdAtLabel, color = TextSecondary, style = MaterialTheme.typography.bodySmall)
            Spacer(Modifier.height(8.dp))
            order.items.forEach { item ->
                Text("· ${item.product.name} x${item.quantity}", style = MaterialTheme.typography.bodyMedium)
            }
            Spacer(Modifier.height(8.dp))
            Text(formatPrice(order.totalCents), color = OrangePrimary, fontWeight = FontWeight.Bold)
            if (order.status == UserOrderStatus.PENDING) {
                TextButton(onClick = onMarkCompleted, modifier = Modifier.padding(top = 4.dp)) {
                    Text(stringResource(R.string.mark_order_received))
                }
            }
        }
    }
}
