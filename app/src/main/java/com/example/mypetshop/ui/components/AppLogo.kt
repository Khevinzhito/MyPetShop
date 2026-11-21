package com.example.mypetshop.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.mypetshop.R
import com.example.mypetshop.ui.theme.OrangePrimary

@Composable
fun AppLogo(
    modifier: Modifier = Modifier,
    size: Dp = 120.dp,
    applyTint: Boolean = false,
) {
    Image(
        painter = painterResource(R.drawable.ic_app_logo),
        contentDescription = stringResource(R.string.app_name),
        modifier = modifier.size(size),
        contentScale = ContentScale.Fit,
        colorFilter = if (applyTint) ColorFilter.tint(OrangePrimary) else null,
    )
}
