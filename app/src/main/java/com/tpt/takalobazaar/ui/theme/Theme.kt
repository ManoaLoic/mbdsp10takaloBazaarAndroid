package com.tpt.takalobazaar.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorPalette = darkColors(
    primary = MainColor,
    primaryVariant = DarkBlue,
    secondary = SecondColor,
    background = White,
    surface = LightGray,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Black,
    onSurface = Black,
)

private val LightColorPalette = lightColors(
    primary = MainColor,
    primaryVariant = DarkBlue,
    secondary = SecondColor,
    background = White,
    surface = LightGray,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Black,
    onSurface = Black,
)

@Composable
fun StoreTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
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