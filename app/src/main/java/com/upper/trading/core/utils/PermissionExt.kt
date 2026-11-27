package com.upper.trading.core.utils

import android.Manifest
import android.app.Activity
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

/**
 * Checks whether the application has been granted the POST_NOTIFICATIONS permission.
 * On Android versions below 13 (Tiramisu), notification permission is granted automatically at install time.
 */
fun Context.hasNotificationPermission(): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    } else {
        true
    }
}

/**
 * Checks whether a specific notification channel is enabled by the user (API 26+).
 * If running below Android 8.0 (Oreo) or channel does not exist yet, returns true.
 * If the user explicitly disabled the channel, returns false.
 */
fun Context.isNotificationChannelEnabled(channelId: String): Boolean {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val manager = getSystemService(NotificationManager::class.java) ?: return true
        val channel = manager.getNotificationChannel(channelId) ?: return true
        return channel.importance != NotificationManager.IMPORTANCE_NONE
    }
    return true
}

/**
 * Checks whether the UI should show an educational rationale before requesting notification permission.
 */
fun Activity.shouldShowNotificationPermissionRationale(): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        ActivityCompat.shouldShowRequestPermissionRationale(
            this,
            Manifest.permission.POST_NOTIFICATIONS
        )
    } else {
        false
    }
}

/**
 * Navigates the user directly to the application's detailed system settings screen
 * so they can manually toggle permissions (such as Notifications) when permanently denied.
 */
fun Context.openAppSettings() {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
        data = Uri.fromParts("package", packageName, null)
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    startActivity(intent)
}

/**
 * Navigates directly to the system settings screen for a specific notification channel.
 * Falls back to general app settings if channel settings is not supported.
 */
fun Context.openNotificationChannelSettings(channelId: String) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val intent = Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS).apply {
            putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
            putExtra(Settings.EXTRA_CHANNEL_ID, channelId)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
            return
        }
    }
    openAppSettings()
}
