package com.upper.trading.core.utils

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.widget.Toast
import androidx.annotation.StringRes

tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

tailrec fun Context.findFragmentActivity(): androidx.fragment.app.FragmentActivity? = when (this) {
    is androidx.fragment.app.FragmentActivity -> this
    is ContextWrapper -> baseContext?.findFragmentActivity()
    else -> null
}

fun Context.showToast(message: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, message, duration).show()
}

fun Context.showToast(@StringRes messageResId: Int, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, messageResId, duration).show()
}