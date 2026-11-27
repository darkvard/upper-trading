package com.upper.trading.presentation.features.auth.otp

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.upper.trading.R
import com.upper.trading.core.notification.OtpNotificationManager
import com.upper.trading.core.notification.OtpNotificationReceiver
import com.upper.trading.core.utils.ObserveAsEvents
import com.upper.trading.core.utils.ThemePreviews
import com.upper.trading.core.utils.TradingPreview
import com.upper.trading.core.utils.bounceClick
import com.upper.trading.presentation.components.button.AppPrimaryButton
import com.upper.trading.presentation.components.dialog.LocalAppDialog
import com.upper.trading.presentation.components.layout.ProgressBarActionLayout
import com.upper.trading.presentation.components.state.LocalAppState
import com.upper.trading.presentation.features.auth.otp.components.OtpTextField
import com.upper.trading.presentation.features.auth.otp.components.rememberOtpPermissionHandler
import com.upper.trading.presentation.features.auth.otp.components.rememberOtpTextFieldState
import com.upper.trading.presentation.features.auth.signin.AuthInputMode
import com.upper.trading.presentation.navigation.AuthNavKey
import com.upper.trading.presentation.navigation.RegistrationFlowStep
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList

@Composable
fun AuthOtpRoute(
    navKeyOtp: AuthNavKey.Otp,
    onBackClick: () -> Unit,
    onCloseClick: () -> Unit,
    onNextClick: () -> Unit,
    viewmodel: AuthOtpViewModel = hiltViewModel(
        creationCallback = { factory: AuthOtpViewModel.Factory ->
            factory.create(navKeyOtp = navKeyOtp)
        }
    )
) {
    val uiState by viewmodel.state.collectAsStateWithLifecycle()
    val snackbar = LocalAppState.current.snackbar
    val context = LocalContext.current
    val appDialog = LocalAppDialog.current

    val onSendOtpWithPermissionCheck = rememberOtpPermissionHandler(
        appDialog = appDialog,
        snackbar = snackbar,
        hasDeniedInStorage = uiState.hasDeniedNotificationPermission,
        onPermissionDeniedInStorage = { isDenied ->
            viewmodel.onIntent(AuthOtpIntent.SetNotificationPermissionDenied(isDenied))
        },
        onPermissionGranted = { viewmodel.onIntent(AuthOtpIntent.SendOtp) }
    )

    val otpState = rememberOtpTextFieldState(
        digits = uiState.digitInput,
        onDigitsChange = { viewmodel.onIntent(AuthOtpIntent.OnDigitsChange(it)) }
    )

    OtpAutoFillEffect(
        digitCount = uiState.digitCount,
        onDigitsChange = { viewmodel.onIntent(AuthOtpIntent.OnDigitsChange(it)) }
    )

    ObserveAsEvents(viewmodel.effect) { effect ->
        when (effect) {
            AuthOtpEffect.NavigateSuccessScreen -> {
                onNextClick()
            }

            is AuthOtpEffect.ShowToast -> {
                if (effect.isSuccess) {
                    snackbar.showSuccess(title = effect.message)
                } else {
                    snackbar.showError(title = effect.message)
                }
            }

            is AuthOtpEffect.ShowOtpNotification -> {
                OtpNotificationManager.showOtpNotification(context, effect.otpCode, effect.destination)
            }
        }
    }

    AuthOtpScreen(
        uiState = uiState,
        otpState = otpState,
        onBackClick = onBackClick,
        onCloseClick = onCloseClick,
        onSendOtpClick = onSendOtpWithPermissionCheck,
        onVerifyClick = { viewmodel.onIntent(AuthOtpIntent.VerifyOtp) },
        onResendClick = onSendOtpWithPermissionCheck
    )
}

@Composable
fun AuthOtpScreen(
    uiState: AuthOtpUiState,
    otpState: TextFieldState,
    onBackClick: () -> Unit,
    onCloseClick: () -> Unit,
    onSendOtpClick: () -> Unit,
    onVerifyClick: () -> Unit,
    onResendClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val progressBarState = remember(uiState.fromOnboarding) {
        if (uiState.fromOnboarding) {
            RegistrationFlowStep.OTP.toProgressBarState()
        } else {
            null
        }
    }

    ProgressBarActionLayout(
        onBackClick = onBackClick,
        onCloseClick = onCloseClick,
        progressBarState = progressBarState,
        modifier = modifier
    ) {
        AuthOtpContent(
            uiState = uiState,
            otpState = otpState,
            onSendOtpClick = onSendOtpClick,
            onVerifyClick = onVerifyClick,
            onResendClick = onResendClick,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
fun AuthOtpContent(
    uiState: AuthOtpUiState,
    otpState: TextFieldState,
    onSendOtpClick: () -> Unit,
    onVerifyClick: () -> Unit,
    onResendClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(top = 24.dp, start = 16.dp, end = 16.dp)
    ) {
        Text(
            text = if (uiState.isEmailChannel) {
                stringResource(R.string.confirm_your_email)
            } else {
                stringResource(R.string.confirm_your_phone)
            },
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = stringResource(
                R.string.confirm_otp_description,
                uiState.digitCount,
                uiState.destination
            ),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(40.dp))

        OtpTextField(
            state = otpState,
            digitCount = uiState.digitCount,
            isValid = uiState.isValid,
            modifier = Modifier.fillMaxWidth(),
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.didnt_get_a_code),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.width(4.dp))

            Text(
                text = if (uiState.resendCountdown > 0) {
                    stringResource(R.string.resend_with_countdown, uiState.resendCountdown)
                } else {
                    stringResource(R.string.resend)
                },
                style = MaterialTheme.typography.bodySmall,
                color = if (uiState.canSendOtp) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                modifier = Modifier.then(
                    if (uiState.canSendOtp) {
                        Modifier.bounceClick { onResendClick() }
                    } else {
                        Modifier.alpha(0.5f)
                    }
                )
            )
        }
        Spacer(modifier = Modifier.weight(1f))

        AppPrimaryButton(
            text = if (!uiState.hasSentOtp) {
                stringResource(R.string.send_otp)
            } else {
                stringResource(R.string.verify_otp)
            },
            enabled = if (!uiState.hasSentOtp) uiState.canSendOtp else uiState.isButtonEnabled,
            isLoading = if (!uiState.hasSentOtp) uiState.isResending else uiState.isLoading,
            modifier = Modifier
                .navigationBarsPadding()
                .padding(bottom = 40.dp),
            onClick = if (!uiState.hasSentOtp) onSendOtpClick else onVerifyClick,
        )
    }
}

@Composable
private fun OtpAutoFillEffect(
    digitCount: Int,
    onDigitsChange: (ImmutableList<Int>) -> Unit
) {
    val context = LocalContext.current
    val currentDigitCount by rememberUpdatedState(digitCount)
    val currentOnDigitsChange by rememberUpdatedState(onDigitsChange)

    // Cold Start
    LaunchedEffect(Unit) {
        val activity = context as? Activity
        val initialOtp = activity?.intent?.getStringExtra(OtpNotificationReceiver.EXTRA_OTP_CODE)
        if (!initialOtp.isNullOrBlank()) {
            val digits = initialOtp.mapNotNull { it.digitToIntOrNull() }.toPersistentList()
            if (digits.size == currentDigitCount) {
                currentOnDigitsChange(digits)
                activity.intent?.removeExtra(OtpNotificationReceiver.EXTRA_OTP_CODE)
            }
        }
    }

    // Real-time Listening
    DisposableEffect(context) {
        val filter = IntentFilter(OtpNotificationReceiver.ACTION_OTP_COPIED)
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(ctx: Context?, intent: Intent?) {
                val code = intent?.getStringExtra(OtpNotificationReceiver.EXTRA_OTP_CODE) ?: return
                val digits = code.mapNotNull { it.digitToIntOrNull() }.toPersistentList()
                if (digits.size == currentDigitCount) {
                    currentOnDigitsChange(digits)
                }
            }
        }
        ContextCompat.registerReceiver(
            context,
            receiver,
            filter,
            ContextCompat.RECEIVER_NOT_EXPORTED
        )
        onDispose {
            try {
                context.unregisterReceiver(receiver)
            } catch (e: IllegalArgumentException) {
                Log.w("OtpAutoFillEffect", "Receiver already unregistered or not registered", e)
            }
        }
    }
}

@ThemePreviews
@Composable
fun PreviewAuthOtpDefaultScreen() {
    TradingPreview {
        val otpDefaultState = remember {
            AuthOtpUiState(
                inputMode = AuthInputMode.EMAIL,
                email = "user@gmail.com",
                fromOnboarding = true,
                digitInput = persistentListOf(1, 2, 3),
                resendCountdown = 25
            )
        }

        AuthOtpScreen(
            uiState = otpDefaultState,
            otpState = rememberTextFieldState("123"),
            onBackClick = { },
            onCloseClick = { },
            onSendOtpClick = { },
            onVerifyClick = { },
            onResendClick = { }
        )
    }
}
