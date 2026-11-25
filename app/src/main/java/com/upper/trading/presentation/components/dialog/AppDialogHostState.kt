package com.upper.trading.presentation.components.dialog

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.upper.trading.R
import com.upper.trading.core.utils.UiText
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

enum class DialogType { CONFIRMATION, GUIDE }

@Immutable
data class AppDialogData(
    val title: UiText? = null,
    val description: UiText? = null,
    val steps: ImmutableList<UiText> = persistentListOf(),
    val icon: ImageVector? = null,
    val type: DialogType = DialogType.CONFIRMATION,
    val primaryColor: Color? = null,
    val dismissName: UiText? = UiText.StringResource(R.string.cancel),
    val confirmName: UiText = UiText.StringResource(R.string.confirm),
    val onConfirm: (() -> Unit)? = null,
    val onDismiss: (() -> Unit)? = null
)

@Stable
class AppDialogHostState(initialData: AppDialogData? = null) {
    var currentData by mutableStateOf(initialData)
        private set

    fun show(data: AppDialogData) {
        currentData = data
    }

    fun dismiss() {
        currentData = null
    }
}

val LocalAppDialog = staticCompositionLocalOf { AppDialogHostState() }

@Composable
fun rememberAppDialogHostState(): AppDialogHostState = remember { AppDialogHostState() }
