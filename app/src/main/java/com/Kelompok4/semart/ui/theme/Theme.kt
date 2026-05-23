package com.Kelompok4.semart.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val SeMartColorScheme = lightColorScheme(

    primary = PrimaryBlue,
    secondary = SecondaryBlue,
    background = BackgroundBlue,
    surface = White,
    onPrimary = White,
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