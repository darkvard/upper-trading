package com.upper.trading.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = AppColor.Primary,
    onPrimary = Color.White,
    primaryContainer = AppColor.PrimaryVariant,
    background = AppColor.DarkBackground,
    onBackground = AppColor.DarkTextPrimary,
    surface = AppColor.DarkSurface,
    onSurface = AppColor.DarkTextPrimary,
    surfaceVariant = AppColor.DarkSurfaceVariant,
    onSurfaceVariant = AppColor.DarkTextSecondary,
    outline = AppColor.DarkBorder,
    outlineVariant = AppColor.DarkDivider
)

private val LightColorScheme = lightColorScheme(
    primary = AppColor.Primary,
    onPrimary = Color.White,
    primaryContainer = AppColor.PrimaryVariant,
    background = AppColor.LightBackground,
    onBackground = AppColor.LightTextPrimary,
    surface = AppColor.LightSurface,
    onSurface = AppColor.LightTextPrimary,
    surfaceVariant = AppColor.LightSurfaceVariant,
    onSurfaceVariant = AppColor.LightTextSecondary,
    outline = AppColor.LightBorder,
    outlineVariant = AppColor.LightDivider
)

@Composable
fun TradingTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = UpperTypography,
        shapes = Shapes,
        content = content
    )
}
