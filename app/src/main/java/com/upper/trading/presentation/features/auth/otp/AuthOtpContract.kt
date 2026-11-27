package com.upper.trading.presentation.features.auth.otp

import androidx.compose.runtime.Immutable
import com.upper.trading.core.utils.UiText
import com.upper.trading.presentation.base.UiEffect
import com.upper.trading.presentation.base.UiIntent
import com.upper.trading.presentation.base.UiState
import com.upper.trading.presentation.features.auth.signin.AuthInputMode
import com.upper.trading.presentation.navigation.AuthNavKey
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Immutable
data class AuthOtpUiState(
    val inputMode: AuthInputMode = AuthInputMode.EMAIL,
    val email: String = "",
    val dialCode: Int = 0,
    val phoneNumber: String = "",
    val fromOnboarding: Boolean = true,
    val digitCount: Int = 6,
    val digitInput: ImmutableList<Int> = persistentListOf(),
    val isValid: Boolean? = null,
    val isLoading: Boolean = false,
    val isResending: Boolean = false,
    val hasSentOtp: Boolean = false,
    val resendCountdown: Int = 0,
    val hasDeniedNotificationPermission: Boolean = false
): UiState {

    val isEmailChannel get() = inputMode == AuthInputMode.EMAIL

    val destination: String
        get() = if (isEmailChannel) email else "+$dialCode $phoneNumber"

    val isButtonEnabled: Boolean
        get() = if (!hasSentOtp) canSendOtp else (digitInput.size == digitCount && !isLoading)

    val canSendOtp: Boolean
        get() = !isResending && (!hasSentOtp || resendCountdown <= 0)

    val canResend: Boolean
        get() = canSendOtp

    companion object {
        fun from(navKeyOtp: AuthNavKey.Otp): AuthOtpUiState {
            return AuthOtpUiState().copy(
                inputMode = navKeyOtp.inputMode,
                email = navKeyOtp.email,
                dialCode = navKeyOtp.dialCode,
                phoneNumber = navKeyOtp.phoneNumber,
                fromOnboarding = navKeyOtp.fromOnboarding
            )
        }
    }
}

sealed interface AuthOtpIntent : UiIntent {
    data object SendOtp : AuthOtpIntent
    data object VerifyOtp : AuthOtpIntent
    data object ResendOtp : AuthOtpIntent
    data class OnDigitsChange(val digits: ImmutableList<Int>) : AuthOtpIntent
    data class SetNotificationPermissionDenied(val isDenied: Boolean) : AuthOtpIntent
}

sealed interface AuthOtpEffect : UiEffect {
    data class ShowToast(val isSuccess: Boolean, val message: UiText) : AuthOtpEffect
    data object NavigateSuccessScreen : AuthOtpEffect
    data class ShowOtpNotification(val otpCode: String, val destination: String? = null) : AuthOtpEffect
}
