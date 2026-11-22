package com.upper.trading.presentation.theme

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.res.Configuration
import android.content.res.Resources
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

/**
 * Custom [ContextWrapper] designed to bridge Android's resource qualifier system with Compose in-app theming.
 *
 * ### The Problem (Device Mode vs. In-App Theme):
 * Android resolves drawable resources (`res/drawable/` vs `res/drawable-night/`) at runtime using
 * `Configuration.uiMode`. By default, this reads from the device/system OS setting.
 * When a user's phone is set to Dark Mode in the OS, but the user selects Light Mode inside the app:
 * Compose's [MaterialTheme] switches to light colors in memory, but `painterResource(id)` queries
 * the default [Resources] instance which is still flagged with `UI_MODE_NIGHT_YES`.
 * Consequently, Android loads dark illustrations from `drawable-night/` on top of a light background.
 *
 * ### Why [ThemedContext] is required (The Hilt Constraint):
 * Calling `context.createConfigurationContext(config)` directly overrides `Configuration.uiMode`,
 * but it returns a detached `android.app.ContextImpl`.
 * Hilt (`hiltViewModel()`) traverses `context.findActivity()` by recursively unwrapping [ContextWrapper].
 * If [LocalContext] is overridden with a raw `ContextImpl`, Hilt fails to find the hosting [Activity],
 * throwing: `IllegalStateException: Expected an activity context for creating a HiltViewModelFactory`.
 *
 * ### The Solution:
 * By subclassing [ContextWrapper] with the original [Activity] as its `baseContext`:
 * 1. Hilt safely unwraps `baseContext` and locates the hosting [Activity] without crash.
 * 2. [getResources] delegates to [configContext] whose `uiMode` matches the in-app theme,
 *    forcing `painterResource()` to resolve the correct light or night drawables.
 */
private class ThemedContext(
    base: Context,
    private val configContext: Context
) : ContextWrapper(base) {
    override fun getResources(): Resources = configContext.resources
}


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

    // Synchronize Android's Resources with the app's in-app theme selection.
    // This prevents desynchronization where the device OS is in Dark Mode but the app is in Light Mode
    // (or vice versa), ensuring both Compose colors and XML drawable resources match the exact same theme.
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val themedContext = remember(context, configuration, darkTheme) {
        val targetNightMode = if (darkTheme) {
            Configuration.UI_MODE_NIGHT_YES
        } else {
            Configuration.UI_MODE_NIGHT_NO
        }
        val config = Configuration(configuration).apply {
            uiMode = (uiMode and Configuration.UI_MODE_NIGHT_MASK.inv()) or targetNightMode
        }
        val configContext = context.createConfigurationContext(config)
        ThemedContext(context, configContext)
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window ?: return@SideEffect
            val insetsController = WindowCompat.getInsetsController(window, view)
            insetsController.isAppearanceLightStatusBars = !darkTheme
            insetsController.isAppearanceLightNavigationBars = !darkTheme
        }
    }

    // CompositionLocalProvider implicitly provides `themedContext` down the entire Compose subtree.
    // Any child composable calling `painterResource()` or querying resources will use
    // `LocalContext.current.resources`, resolving theme-accurate drawables without manual plumbing.
    // Note: We deliberately do NOT override `LocalConfiguration` here so that `isSystemInDarkTheme()`
    // continues to accurately reflect the real device OS theme.
    CompositionLocalProvider(
        LocalContext provides themedContext
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography  = UpperTypography,
            shapes      = Shapes,
            content     = content
        )
    }
}
