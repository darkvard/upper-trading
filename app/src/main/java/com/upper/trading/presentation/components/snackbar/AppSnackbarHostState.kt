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
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.job

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
    private var activeJob: Job? = null

    var currentData by mutableStateOf(initialData)
        private set

    suspend fun show(data: AppSnackbarData) {
        activeJob?.cancel()
        coroutineScope {
            activeJob = coroutineContext.job
            if (currentData != null && currentData != data) {
                dismiss()
                delay(AppSnackbarAnimationDefaults.EXIT_DURATION_MS.toLong())
            }
            currentData = data
            if (data.durationMillis != null && data.durationMillis > 0) {
                try {
                    delay(data.durationMillis)
                    if (currentData == data) dismiss()
                } catch (_: CancellationException) { }
            }
        }
    }

    fun dismiss() {
        activeJob?.cancel()
        currentData = null
    }

    suspend fun showSuccess(title: UiText, message: UiText? = null) {
        show(
            AppSnackbarData(
                type = SnackbarType.SUCCESS,
                title = title,
                message = message
            )
        )
    }

    suspend fun showSuccess(title: String, message: String? = null) {
        showSuccess(
            title = UiText.DynamicString(title),
            message = message?.let { UiText.DynamicString(it) }
        )
    }

    suspend fun showError(
        title: UiText,
        message: UiText? = null,
        actionLabel: UiText? = UiText.StringResource(R.string.retry),
        retryAction: (() -> Unit)? = null
    ) {
        show(
            AppSnackbarData(
                type = SnackbarType.ERROR,
                title = title,
                message = message,
                actionLabel = if (retryAction != null) actionLabel else null,
                onAction = retryAction
            )
        )
    }

    suspend fun showError(
        title: String,
        message: String? = null,
        actionLabel: UiText? = UiText.StringResource(R.string.retry),
        retryAction: (() -> Unit)? = null
    ) {
        showError(
            title = UiText.DynamicString(title),
            message = message?.let { UiText.DynamicString(it) },
            actionLabel = actionLabel,
            retryAction = retryAction
        )
    }

    suspend fun showLoading(title: UiText, message: UiText? = null) {
        show(
            AppSnackbarData(
                type = SnackbarType.LOADING,
                title = title,
                message = message,
                durationMillis = null
            )
        )
    }

    suspend fun showLoading(title: String, message: String? = null) {
        showLoading(
            title = UiText.DynamicString(title),
            message = message?.let { UiText.DynamicString(it) }
        )
    }
}

val LocalAppSnackbar = staticCompositionLocalOf { AppSnackbarHostState() }

@Composable
fun rememberAppSnackbarHostState(): AppSnackbarHostState = remember { AppSnackbarHostState() }