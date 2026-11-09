package com.example.mypetshop.ui.components

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.ui.res.stringResource
import com.example.mypetshop.R
import com.example.mypetshop.ui.theme.BackgroundWhite
import com.example.mypetshop.ui.theme.OrangePrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    title: String = stringResource(R.string.app_name),
    showBack: Boolean = false,
    onBack: () -> Unit = {},
) {
    TopAppBar(
        title = { Text(text = title, color = BackgroundWhite) },
        navigationIcon = {
            if (showBack) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back),
                        tint = BackgroundWhite,
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = OrangePrimary,
            titleContentColor = BackgroundWhite,
        ),
    )
}
