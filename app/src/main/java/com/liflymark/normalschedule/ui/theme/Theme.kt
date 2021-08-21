package com.liflymark.normalschedule.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.liflymark.test.ui.theme.Typography

private val DarkColorPalette = darkColors(
    primary = PrimaryD,
    primaryVariant = Color(0xFF6E92FF),
    secondary = Secondary,
    onSecondary = onSecondary,
    onPrimary = onPrimary,
)

private val LightColorPalette = lightColors(
    primary = Primary,
    primaryVariant = Color(0xff6ec6ff),
    secondary = Secondary,
    onSecondary = onSecondary,
    onPrimary = onPrimary,
)

@Composable
fun NorScTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable() () -> Unit) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}