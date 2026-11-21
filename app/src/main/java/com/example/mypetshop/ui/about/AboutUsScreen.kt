package com.example.mypetshop.ui.about

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.mypetshop.R
import com.example.mypetshop.ui.components.AppTopBar
import com.example.mypetshop.ui.theme.BackgroundWhite
import com.example.mypetshop.ui.theme.OrangeDark
import com.example.mypetshop.ui.theme.TextSecondary

@Composable
fun AboutUsScreen(
    onBack: () -> Unit,
    embedInScaffold: Boolean = true,
) {
    val content: @Composable (Modifier) -> Unit = { modifier ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = BackgroundWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            ) {
                Column(Modifier.padding(24.dp)) {
                    Image(
                        painter = painterResource(R.drawable.logo_logus),
                        contentDescription = null,
                        modifier = Modifier.fillMaxWidth().height(100.dp),
                        contentScale = ContentScale.Fit,
                    )
                    Spacer(Modifier.height(16.dp))
                    Text(
                        stringResource(R.string.about_us_origin),
                        style = MaterialTheme.typography.titleLarge,
                        color = OrangeDark,
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(stringResource(R.string.about_us_origin_body), color = TextSecondary)
                    Spacer(Modifier.height(16.dp))
                    Text(
                        stringResource(R.string.about_logus_title),
                        style = MaterialTheme.typography.titleMedium,
                        color = OrangeDark,
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(stringResource(R.string.about_logus_body), color = TextSecondary)
                }
            }
        }
    }

    if (embedInScaffold) {
        Scaffold(
            containerColor = BackgroundWhite,
            topBar = {
                AppTopBar(
                    title = stringResource(R.string.about_us_screen_title),
                    showBack = true,
                    onBack = onBack,
                )
            },
        ) { padding -> content(Modifier.padding(padding)) }
    } else {
        content(Modifier)
    }
}
