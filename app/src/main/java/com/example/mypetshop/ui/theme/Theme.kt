package com.example.mypetshop.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val MyPetShopColorScheme = lightColorScheme(
    primary = OrangePrimary,
    onPrimary = BackgroundWhite,
    primaryContainer = OrangeLight,
    onPrimaryContainer = TextPrimary,
    background = BackgroundWhite,
    onBackground = TextPrimary,
    surface = BackgroundWhite,
    onSurface = TextPrimary,
    error = ErrorRed,
)

@Composable
fun MyPetShopTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = MyPetShopColorScheme, content = content)
}
