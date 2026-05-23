package com.Kelompok4.semart.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.Kelompok4.semart.R

val Poppins = FontFamily(

    Font(
        R.font.poppins_regular,
        weight = FontWeight.Normal
    ),

    Font(
        R.font.poppins_medium,
        weight = FontWeight.Medium
    ),

    Font(
        R.font.poppins_semibold,
        weight = FontWeight.SemiBold
    ),

    Font(
        R.font.poppins_bold,
        weight = FontWeight.Bold
    )
)

val Typography = Typography(

    bodyLarge = TextStyle(
        fontFamily = Poppins,
        fontSize = 16.sp
    ),

    bodyMedium = TextStyle(
        fontFamily = Poppins,
        fontSize = 14.sp
    ),

    headlineLarge = TextStyle(
        fontFamily = Poppins,
        fontWeight = FontWeight.Bold,
        fontSize = 30.sp
    ),

    titleLarge = TextStyle(
        fontFamily = Poppins,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp
    )
)