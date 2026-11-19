package com.upper.trading.core.utils

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.upper.trading.presentation.theme.TradingTheme

/**
 * A wrapper composable for Previews that encapsulates [TradingTheme] and [Surface].
 * Eliminates boilerplate in `@Preview` and `@ThemePreviews` composable functions.
 *
 * Example:
 * ```
 * @ThemePreviews
 * @Composable
 * fun MyScreenPreview() {
 *     TradingPreview {
 *         MyScreen()
 *     }
 * }
 * ```
 *
 * @param modifier Optional [Modifier] applied to the underlying [Surface].
 * @param darkTheme Controls dark mode. Defaults to [isSystemInDarkTheme], allowing `@ThemePreviews`
 *                  to automatically preview both Light and Dark modes.
 * @param color The background color applied to [Surface]. Defaults to [MaterialTheme.colorScheme.background].
 * @param content The composable content to preview.
 */
@Composable
fun TradingPreview(
    modifier: Modifier = Modifier,
    darkTheme: Boolean = isSystemInDarkTheme(),
    color: Color? = null,
    content: @Composable () -> Unit
) {
    TradingTheme(darkTheme = darkTheme) {
        Surface(
            modifier = modifier,
            color = color ?: MaterialTheme.colorScheme.background,
            content = content
        )
    }
}
