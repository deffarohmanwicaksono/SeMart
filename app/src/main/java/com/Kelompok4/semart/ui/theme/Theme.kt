package com.Kelompok4.semart.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val SeMartColorScheme = lightColorScheme(
    primary = PrimaryBlue,
    secondary = PrimaryBlue,
    background = SoftBlueBg,
    surface = Color.White,
    onPrimary = Color.White,
    onBackground = DarkText
)

@Composable
fun SeMartTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = SeMartColorScheme,
        typography = Typography,
        content = content
    )
}