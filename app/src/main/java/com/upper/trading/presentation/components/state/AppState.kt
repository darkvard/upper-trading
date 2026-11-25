package com.upper.trading.presentation.components.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import com.upper.trading.presentation.components.dialog.AppDialogHostState
import com.upper.trading.presentation.components.dialog.rememberAppDialogHostState
import com.upper.trading.presentation.components.snackbar.AppSnackbarHostState
import com.upper.trading.presentation.components.snackbar.rememberAppSnackbarHostState

@Stable
class AppState(
    val snackbar: AppSnackbarHostState,
    val dialog: AppDialogHostState
)

val LocalAppState = staticCompositionLocalOf<AppState> {
    error("LocalAppState is not provided")
}

@Composable
fun rememberAppState(
    snackbar: AppSnackbarHostState = rememberAppSnackbarHostState(),
    dialog: AppDialogHostState = rememberAppDialogHostState()
): AppState = remember(snackbar, dialog) {
    AppState(
        snackbar = snackbar,
        dialog = dialog
    )
}
