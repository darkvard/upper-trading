package com.upper.trading.presentation.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat


private val LightColorScheme = lightColorScheme(
    // Primary
    primary             = purple5,          // #613DE4
    onPrimary           = Color.White,
    primaryContainer    = purple2,          // #C0B1F4 — light tint
    onPrimaryContainer  = purple8,          // #27185B — dark text on light tint

    // Secondary (blue shades — neutral cool)
    secondary              = blueShades9,   // #566FAE
    onSecondary            = Color.White,
    secondaryContainer     = blueShades2,   // #FBFCFE
    onSecondaryContainer   = blueShades10,  // #2E4D9B

    // Background
    background   = blueShades2,            // #FBFCFE — Bg
    onBackground = blackShades10,          // #111112 — Primary Body text

    // Surface
    surface             = blueShades5,     // #F5F7FC — Surfaces
    onSurface           = blackShades10,   // #111112
    surfaceVariant      = blueShades3,     // #F9FAFD
    onSurfaceVariant    = blackShades7,    // #67676D — Secondary Body text

    // Outline / Border / Divider
    outline         = blueShades6,         // #CDD5E9 — Border and divider
    outlineVariant  = blueShades4,         // #F7F9FD

    // Error
    error             = red5,             // #EF4444
    onError           = Color.White,
    errorContainer    = red1,             // #FCDADA
    onErrorContainer  = red8,             // #661C1C

    // Tertiary (Warning / Attention)
    tertiary            = orange5,        // #F59E0B — Warning
    onTertiary          = Color.White,
    tertiaryContainer   = orange1,        // #FDECCE
    onTertiaryContainer = orange85,       // #4A2F03
)


private val DarkColorScheme = darkColorScheme(
    // Primary
    primary             = purple5,         // #613DE4 — same as light
    onPrimary           = Color.White,
    primaryContainer    = purple8,         // #27185B — dark container
    onPrimaryContainer  = purple1,         // #DFD8FA — light text on dark container

    // Secondary
    secondary              = blueShades7,  // #A5B3D5
    onSecondary            = blackShades95,// #1F1F21
    secondaryContainer     = blackShades9, // #2E2E30
    onSecondaryContainer   = blueShades5,  // #F5F7FC

    // Background
    background   = darkBlueShades2,         // #1D2939 — dark Bg
    onBackground = Color.White,           // Primary Body text in dark

    // Surface
    surface             = darkBlueShades5,   // #1D2939 — dark Surfaces
    onSurface           = Color.White,
    surfaceVariant      = blackShades85,  // #3C3C40
    onSurfaceVariant    = blackShades3,   // #C7C7CC — Secondary Body text

    // Outline / Border / Divider
    outline         = blackShades8,       // #4B4B4F — Border in dark
    outlineVariant  = blackShades85,      // #3C3C40

    // Error
    error             = red5,            // #EF4444
    onError           = Color.White,
    errorContainer    = red8,            // #661C1C
    onErrorContainer  = red1,            // #FCDADA

    // Tertiary (Warning / Attention)
    tertiary            = orange5,       // #F59E0B
    onTertiary          = Color.White,
    tertiaryContainer   = orange85,      // #4A2F03
    onTertiaryContainer = orange1,       // #FDECCE
)

@Composable
fun TradingTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window ?: return@SideEffect
            val insetsController = WindowCompat.getInsetsController(window, view)
            insetsController.isAppearanceLightStatusBars = !darkTheme
            insetsController.isAppearanceLightNavigationBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography  = UpperTypography,
        shapes      = Shapes,
        content     = content
    )
}
