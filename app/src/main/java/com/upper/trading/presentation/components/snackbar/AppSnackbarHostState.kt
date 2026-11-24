package com.upper.trading.presentation.components.snackbar

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import com.upper.trading.R
import com.upper.trading.core.utils.UiText

enum class SnackbarType {
    SUCCESS, ERROR, LOADING
}

@Stable
data class AppSnackbarData(
    val type: SnackbarType,
    val title: UiText,
    val message: UiText? = null,
    val actionLabel: UiText? = null,
    val onAction: (() -> Unit)? = null,
    val durationMillis: Long? = AppSnackbarAnimationDefaults.DEFAULT_DISPLAY_DURATION_MS // null = infinity
)

@Stable
class AppSnackbarHostState(initialData: AppSnackbarData? = null) {
    var currentData by mutableStateOf(initialData)
        private set

    fun show(data: AppSnackbarData) {
        currentData = data
    }

    fun dismiss() {
        currentData = null
    }

    fun showSuccess(
        title: UiText,
        message: UiText? = null,
        durationMillis: Long? = AppSnackbarAnimationDefaults.DURATION_SHORT_MS
    ) {
        show(
            AppSnackbarData(
                type = SnackbarType.SUCCESS,
                title = title,
                message = message,
                durationMillis = durationMillis
            )
        )
    }

    fun showSuccess(
        title: String,
        message: String? = null,
        durationMillis: Long? = AppSnackbarAnimationDefaults.DURATION_SHORT_MS
    ) {
        showSuccess(
            title = UiText.DynamicString(title),
            message = message?.let { UiText.DynamicString(it) },
            durationMillis = durationMillis
        )
    }

    fun showError(
        title: UiText,
        message: UiText? = null,
        actionLabel: UiText? = UiText.StringResource(R.string.retry),
        retryAction: (() -> Unit)? = null,
        durationMillis: Long? = AppSnackbarAnimationDefaults.DURATION_LONG_MS
    ) {
        show(
            AppSnackbarData(
                type = SnackbarType.ERROR,
                title = title,
                message = message,
                actionLabel = if (retryAction != null) actionLabel else null,
                onAction = retryAction,
                durationMillis = durationMillis
            )
        )
    }

    fun showError(
        title: String,
        message: String? = null,
        actionLabel: UiText? = UiText.StringResource(R.string.retry),
        retryAction: (() -> Unit)? = null,
        durationMillis: Long? = AppSnackbarAnimationDefaults.DURATION_LONG_MS
    ) {
        showError(
            title = UiText.DynamicString(title),
            message = message?.let { UiText.DynamicString(it) },
            actionLabel = actionLabel,
            retryAction = retryAction,
            durationMillis = durationMillis
        )
    }

    fun showLoading(title: UiText, message: UiText? = null) {
        show(
            AppSnackbarData(
                type = SnackbarType.LOADING,
                title = title,
                message = message,
                durationMillis = null
            )
        )
    }

    fun showLoading(title: String, message: String? = null) {
        showLoading(
            title = UiText.DynamicString(title),
            message = message?.let { UiText.DynamicString(it) }
        )
    }
}

val LocalAppSnackbar = staticCompositionLocalOf { AppSnackbarHostState() }

@Composable
fun rememberAppSnackbarHostState(): AppSnackbarHostState = remember { AppSnackbarHostState() }