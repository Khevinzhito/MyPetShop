package com.example.mypetshop.ui.shop

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.mypetshop.R
import com.example.mypetshop.ui.theme.BackgroundWhite
import com.example.mypetshop.ui.theme.OrangeDark
import com.example.mypetshop.ui.theme.TextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun BuyerMessagesScreen(
    viewModel: BuyerMessagesViewModel = hiltViewModel(),
) {
    val messages by viewModel.messages.collectAsState()

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize().background(BackgroundWhite),
    ) {
        item {
            Text(
                stringResource(R.string.buyer_messages_title),
                style = MaterialTheme.typography.titleLarge,
                color = OrangeDark,
                fontWeight = FontWeight.Bold,
            )
            Text(stringResource(R.string.buyer_messages_subtitle), color = TextSecondary)
        }
        if (messages.isEmpty()) {
            item { Text(stringResource(R.string.buyer_messages_empty), color = TextSecondary) }
        } else {
            items(messages, key = { it.id }) { message ->
                Card(shape = RoundedCornerShape(12.dp)) {
                    Column(Modifier.padding(16.dp)) {
                        Text(message.subject, fontWeight = FontWeight.SemiBold, color = OrangeDark)
                        message.orderId?.let {
                            Text(
                                stringResource(R.string.admin_message_order_label, it),
                                style = MaterialTheme.typography.labelSmall,
                                color = TextSecondary,
                            )
                        }
                        Spacer(Modifier.height(8.dp))
                        Text(message.body, color = TextSecondary)
                        Spacer(Modifier.height(4.dp))
                        Text(
                            formatMessageDate(message.sentAtMillis),
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary,
                        )
                    }
                }
            }
        }
    }
}

private fun formatMessageDate(millis: Long): String =
    SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("es", "CO")).format(Date(millis))
