package com.upper.trading.presentation.features.auth.otp

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.upper.trading.R
import com.upper.trading.core.network.mock.MockAuthConstants
import com.upper.trading.core.utils.UiText
import com.upper.trading.domain.model.OtpChannel
import com.upper.trading.domain.model.OtpPurpose
import com.upper.trading.domain.usecase.auth.SendOtpUseCase
import com.upper.trading.domain.usecase.auth.VerifyOtpUseCase
import com.upper.trading.domain.usecase.notification.HasDeniedNotificationPermissionUseCase
import com.upper.trading.domain.usecase.notification.SetNotificationPermissionDeniedUseCase
import com.upper.trading.presentation.base.BaseViewModel
import com.upper.trading.presentation.navigation.AuthNavKey
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val ASSISTED_QUALIFIER = "ASSISTED_QUALIFIER"
private const val RESEND_COUNTDOWN = 30

@HiltViewModel(assistedFactory = AuthOtpViewModel.Factory::class)
class AuthOtpViewModel @AssistedInject constructor(
    @Assisted(ASSISTED_QUALIFIER) private val navKeyOtp: AuthNavKey.Otp,
    private val verifyOtpUseCase: VerifyOtpUseCase,
    private val sendOtpUseCase: SendOtpUseCase,
    private val hasDeniedNotificationPermissionUseCase: HasDeniedNotificationPermissionUseCase,
    private val setNotificationPermissionDeniedUseCase: SetNotificationPermissionDeniedUseCase,
) : BaseViewModel<AuthOtpUiState, AuthOtpIntent, AuthOtpEffect>(
    initialState = AuthOtpUiState.from(navKeyOtp)
) {
    @AssistedFactory
    interface Factory {

        fun create(@Assisted(ASSISTED_QUALIFIER) navKeyOtp: AuthNavKey.Otp): AuthOtpViewModel
    }

    private var countdownJob: Job? = null

    init {
        viewModelScope.launch {
            hasDeniedNotificationPermissionUseCase().collect { isDenied ->
                setState { copy(hasDeniedNotificationPermission = isDenied) }
            }
        }
    }

    override fun onIntent(intent: AuthOtpIntent) {
        when (intent) {
            AuthOtpIntent.SendOtp, AuthOtpIntent.ResendOtp -> sendOtp()
            AuthOtpIntent.VerifyOtp -> verifyOtp()
            is AuthOtpIntent.OnDigitsChange -> {
                setState {
                    copy(
                        digitInput = intent.digits,
                        isValid = null
                    )
                }
            }
            is AuthOtpIntent.SetNotificationPermissionDenied -> {
                viewModelScope.launch {
                    setNotificationPermissionDeniedUseCase(intent.isDenied)
                }
            }
        }
    }

    private fun verifyOtp() {
        val currentState = state.value
        val otpCode = currentState.digitInput.joinToString("")
        if (otpCode.length != currentState.digitCount || currentState.isLoading) return

        viewModelScope.launch {
            setState { copy(isLoading = true) }

            val channel = if (currentState.isEmailChannel) OtpChannel.Email else OtpChannel.Sms
            val result = verifyOtpUseCase(
                channel = channel,
                destination = currentState.destination,
                purpose = OtpPurpose.SIGN_UP,
                code = otpCode
            )

            result.fold(
                onSuccess = {
                    setState { copy(isValid = true, isLoading = false) }
                    sendEffect(
                        AuthOtpEffect.ShowToast(
                            isSuccess = true,
                            message = UiText.StringResource(R.string.otp_verified_success)
                        )
                    )
                    sendEffect(AuthOtpEffect.NavigateSuccessScreen)
                },
                onFailure = { error ->
                    setState { copy(isValid = false, isLoading = false) }
                    val errorMessage = error.message?.takeIf { it.isNotBlank() }
                        ?.let { UiText.DynamicString(it) }
                        ?: UiText.StringResource(R.string.error_invalid_otp)
                    sendEffect(
                        AuthOtpEffect.ShowToast(
                            isSuccess = false,
                            message = errorMessage
                        )
                    )
                }
            )
        }
    }

    private fun sendOtp() {
        val state = currentState
        if (!state.canSendOtp) return
        val currentDestination = state.destination
        val isEmail = state.isEmailChannel
        val isFirstTime = !state.hasSentOtp

        viewModelScope.launch {
            setState { copy(isResending = true) }

            val channel = if (isEmail) OtpChannel.Email else OtpChannel.Sms
            val result = sendOtpUseCase(
                channel = channel,
                destination = currentDestination,
                purpose = OtpPurpose.SIGN_UP
            )

            result.fold(
                onSuccess = {
                    setState {
                        copy(
                            isResending = false,
                            hasSentOtp = true,
                            digitInput = persistentListOf(),
                            isValid = null
                        )
                    }
                    sendEffect(
                        AuthOtpEffect.ShowToast(
                            isSuccess = true,
                            message = UiText.StringResource(
                                if (isFirstTime) R.string.otp_sent_success else R.string.otp_resent_success
                            )
                        )
                    )
                    Log.d(
                        "MockOtp",
                        "===> [DEV OTP DISPATCHED]: ${MockAuthConstants.currentActiveOtp} (Bypass Code: ${MockAuthConstants.DEV_BYPASS_OTP}) <==="
                    )
                    sendEffect(AuthOtpEffect.ShowOtpNotification(MockAuthConstants.currentActiveOtp, currentState.destination))
                    startResendCountdown(RESEND_COUNTDOWN)
                },
                onFailure = { error ->
                    setState { copy(isResending = false) }
                    val errorMessage = error.message?.takeIf { it.isNotBlank() }
                        ?.let { UiText.DynamicString(it) }
                        ?: UiText.StringResource(R.string.error_resend_otp_failed)
                    sendEffect(
                        AuthOtpEffect.ShowToast(
                            isSuccess = false,
                            message = errorMessage
                        )
                    )
                }
            )
        }
    }

    private fun startResendCountdown(seconds: Int = RESEND_COUNTDOWN) {
        countdownJob?.cancel()
        countdownJob = viewModelScope.launch {
            for (remaining in seconds downTo 0) {
                setState { copy(resendCountdown = remaining) }
                if (remaining > 0) delay(1000L)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        countdownJob?.cancel()
    }
}
