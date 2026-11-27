package com.upper.trading.presentation.features.auth.otp.components

import android.Manifest
import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.upper.trading.R
import com.upper.trading.core.notification.OtpNotificationManager
import com.upper.trading.core.utils.UiText
import com.upper.trading.core.utils.findActivity
import com.upper.trading.core.utils.hasNotificationPermission
import com.upper.trading.core.utils.isNotificationChannelEnabled
import com.upper.trading.core.utils.openAppSettings
import com.upper.trading.core.utils.openNotificationChannelSettings
import com.upper.trading.core.utils.shouldShowNotificationPermissionRationale
import com.upper.trading.presentation.components.dialog.AppDialogData
import com.upper.trading.presentation.components.dialog.AppDialogHostState
import com.upper.trading.presentation.components.dialog.DialogType
import com.upper.trading.presentation.components.snackbar.AppSnackbarHostState

@Composable
fun rememberOtpPermissionHandler(
    appDialog: AppDialogHostState,
    snackbar: AppSnackbarHostState,
    hasDeniedInStorage: Boolean = false,
    onPermissionDeniedInStorage: (Boolean) -> Unit = {},
    onPermissionGranted: () -> Unit
): () -> Unit {
    val context = LocalContext.current
    var hasRequestedPermission by rememberSaveable { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            onPermissionDeniedInStorage(false)
            onPermissionGranted()
        } else {
            hasRequestedPermission = true
            onPermissionDeniedInStorage(true)
            snackbar.showError(
                title = UiText.StringResource(R.string.error_notification_permission_required),
                actionLabel = UiText.StringResource(R.string.open_settings),
                retryAction = { context.openAppSettings() }
            )
        }
    }

    val handler = remember(context, appDialog, snackbar, permissionLauncher, hasDeniedInStorage) {
        OtpPermissionHandler(
            context = context,
            appDialog = appDialog,
            snackbar = snackbar,
            permissionLauncher = permissionLauncher,
            hasDeniedInStorage = { hasDeniedInStorage },
            hasRequestedPermission = { hasRequestedPermission },
            setHasRequestedPermission = { hasRequestedPermission = it }
        )
    }

    return remember(handler) {
        { handler.handlePermission(onPermissionGranted) }
    }
}


/**
 * 2-Phase Notification Permission Handler:
 *
 * Phase 1 (prepare):
 * - Checks if permission is already granted -> triggers onGranted immediately.
 * - If not, checks whether the permission is permanently denied (or user disabled it).
 * - Prepares dialog content:
 *   - If permanently denied: "Open Settings" dialog configuration.
 *   - If requestable: "Enable Notifications" dialog configuration.
 *
 * Phase 2 (handle):
 * - Displays a single Dialog with the prepared configuration.
 * - Dispatches the appropriate action (Settings navigation or System permission launcher).
 * - Handles user cancel / deny with a unified error notification.
 */
class OtpPermissionHandler(
    private val context: Context,
    private val appDialog: AppDialogHostState,
    private val snackbar: AppSnackbarHostState,
    private val permissionLauncher: ActivityResultLauncher<String>,
    private val hasDeniedInStorage: () -> Boolean,
    private val hasRequestedPermission: () -> Boolean,
    private val setHasRequestedPermission: (Boolean) -> Unit,
) {
    fun handlePermission(onGranted: () -> Unit) {
        // Step 1: Ensure channel is created if needed
        OtpNotificationManager.createChannelIfNeeded(context)

        val hasAppPermission = context.hasNotificationPermission()
        val isChannelEnabled = context.isNotificationChannelEnabled(OtpNotificationManager.CHANNEL_ID)

        // Step 2: If both App-level permission and Channel are enabled -> proceed directly
        if (hasAppPermission && isChannelEnabled) {
            onGranted()
            return
        }

        // Step 3: Phase 1 (prepare) - Inspect permission state and prepare Dialog parameters
        val activity = context.findActivity()
        val isPermanentlyDenied = hasDeniedInStorage() || (
            activity != null &&
                !activity.shouldShowNotificationPermissionRationale() &&
                hasRequestedPermission()
        )

        val dialogConfig = prepareDialogConfig(
            hasAppPermission = hasAppPermission,
            isChannelEnabled = isChannelEnabled,
            isPermanentlyDenied = isPermanentlyDenied
        )

        // Step 4: Phase 2 (handle) - Show a single dialog with prepared configuration
        appDialog.show(
            AppDialogData(
                type = DialogType.CONFIRMATION,
                icon = Icons.Rounded.Notifications,
                title = dialogConfig.title,
                description = dialogConfig.description,
                confirmName = dialogConfig.confirmName,
                dismissName = UiText.StringResource(R.string.cancel),
                onConfirm = {
                    dialogConfig.onConfirm()
                },
                onDismiss = {
                    snackbar.showError(
                        title = UiText.StringResource(R.string.error_notification_permission_required),
                        retryAction = { handlePermission(onGranted) }
                    )
                }
            )
        )
    }

    private fun prepareDialogConfig(
        hasAppPermission: Boolean,
        isChannelEnabled: Boolean,
        isPermanentlyDenied: Boolean
    ): DialogConfig {
        return when {
            // Case 1: App permission is granted, but the specific Channel is disabled in settings
            hasAppPermission && !isChannelEnabled -> DialogConfig(
                title = UiText.StringResource(R.string.notif_channel_disabled_title),
                description = UiText.StringResource(R.string.notif_channel_disabled_desc),
                confirmName = UiText.StringResource(R.string.open_settings),
                onConfirm = { context.openNotificationChannelSettings(OtpNotificationManager.CHANNEL_ID) }
            )
            // Case 2: App permission is permanently denied (or marked in storage)
            isPermanentlyDenied -> DialogConfig(
                title = UiText.StringResource(R.string.notif_permission_dialog_title),
                description = UiText.StringResource(R.string.notif_permission_settings_desc),
                confirmName = UiText.StringResource(R.string.open_settings),
                onConfirm = { context.openAppSettings() }
            )
            // Case 3: App permission can be requested via system dialog
            else -> DialogConfig(
                title = UiText.StringResource(R.string.notif_permission_dialog_title),
                description = UiText.StringResource(R.string.notif_permission_dialog_desc),
                confirmName = UiText.StringResource(R.string.notif_permission_dialog_confirm),
                onConfirm = {
                    setHasRequestedPermission(true)
                    permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            )
        }
    }

    private data class DialogConfig(
        val title: UiText,
        val description: UiText,
        val confirmName: UiText,
        val onConfirm: () -> Unit
    )
}
