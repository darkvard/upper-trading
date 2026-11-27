package com.upper.trading.core.notification

import android.content.BroadcastReceiver
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.upper.trading.R

class OtpNotificationReceiver : BroadcastReceiver() {

    companion object {
        const val ACTION_COPY_OTP = "com.upper.trading.ACTION_COPY_OTP"
        const val ACTION_OTP_COPIED = "com.upper.trading.ACTION_OTP_COPIED"
        const val EXTRA_OTP_CODE = "extra_otp_code"
    }

    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action == ACTION_COPY_OTP) {
            val otpCode = intent.getStringExtra(EXTRA_OTP_CODE) ?: return
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText(context.getString(R.string.clip_label_otp), otpCode)
            clipboard.setPrimaryClip(clip)

            Toast.makeText(
                context,
                context.getString(R.string.notif_otp_copied_toast),
                Toast.LENGTH_SHORT
            ).show()

            val autoFillIntent = Intent(ACTION_OTP_COPIED).apply {
                putExtra(EXTRA_OTP_CODE, otpCode)
                setPackage(context.packageName)
            }
            context.sendBroadcast(autoFillIntent)
        }
    }
}
