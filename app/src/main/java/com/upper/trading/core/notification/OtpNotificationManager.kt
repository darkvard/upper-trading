package com.upper.trading.core.notification

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.SystemClock
import android.widget.RemoteViews
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.upper.trading.R
import com.upper.trading.presentation.features.home.HomeActivity

object OtpNotificationManager {
    const val CHANNEL_ID = "channel_upper_otp"
    private const val NOTIFICATION_ID = 1001

    fun createChannelIfNeeded(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = context.getSystemService(NotificationManager::class.java) ?: return
            if (manager.getNotificationChannel(CHANNEL_ID) != null) return

            val channel = NotificationChannel(
                CHANNEL_ID,
                context.getString(R.string.notif_otp_channel_name),
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = context.getString(R.string.notif_otp_channel_description)
                enableVibration(true)
            }
            manager.createNotificationChannel(channel)
        }
    }

    fun createChannel(context: Context) = createChannelIfNeeded(context)

    fun showOtpNotification(
        context: Context,
        otpCode: String,
        destination: String? = null,
        countdownSeconds: Int = 300
    ) {
        createChannelIfNeeded(context)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val hasPermission = ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
            if (!hasPermission) return
        }

        val stopTime = SystemClock.elapsedRealtime() + countdownSeconds * 1000L

        val contentIntent = PendingIntent.getActivity(
            context,
            0,
            Intent(context, HomeActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
                putExtra(OtpNotificationReceiver.EXTRA_OTP_CODE, otpCode)
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val copyIntent = PendingIntent.getBroadcast(
            context,
            1,
            Intent(context, OtpNotificationReceiver::class.java).apply {
                action = OtpNotificationReceiver.ACTION_COPY_OTP
                putExtra(OtpNotificationReceiver.EXTRA_OTP_CODE, otpCode)
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val descriptionText = when {
            destination?.contains("@") == true -> context.getString(R.string.notif_otp_desc_email, otpCode)
            !destination.isNullOrBlank() -> context.getString(R.string.notif_otp_desc_phone, otpCode)
            else -> context.getString(R.string.notif_otp_desc_generic, otpCode)
        }

        val collapsed = RemoteViews(context.packageName, R.layout.notification_otp_collapsed).apply {
            setTextViewText(R.id.tv_collapsed_title, context.getString(R.string.app_name))
            setTextViewText(R.id.tv_collapsed_desc, descriptionText)
            setOnClickPendingIntent(R.id.btn_collapsed_copy, copyIntent)
        }

        val expanded = RemoteViews(context.packageName, R.layout.notification_otp_expanded).apply {
            val digits = otpCode.take(6).padEnd(6, ' ')
            setTextViewText(R.id.tv_otp_digit_1, digits[0].toString())
            setTextViewText(R.id.tv_otp_digit_2, digits[1].toString())
            setTextViewText(R.id.tv_otp_digit_3, digits[2].toString())
            setTextViewText(R.id.tv_otp_digit_4, digits[3].toString())
            setTextViewText(R.id.tv_otp_digit_5, digits[4].toString())
            setTextViewText(R.id.tv_otp_digit_6, digits[5].toString())
            setChronometer(R.id.chronometer_expanded_countdown, stopTime, "%s", true)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                setChronometerCountDown(R.id.chronometer_expanded_countdown, true)
            }
            setOnClickPendingIntent(R.id.btn_expanded_copy, copyIntent)
        }

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setColor(ContextCompat.getColor(context, R.color.notif_primary))
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setCustomContentView(collapsed)
            .setCustomBigContentView(expanded)
            .setContentIntent(contentIntent)
            .setAutoCancel(true)
            .setTimeoutAfter(countdownSeconds * 1000L)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .build()

        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, notification)
    }
}
