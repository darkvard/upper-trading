package com.upper.trading.presentation.features.auth.signin

import androidx.compose.runtime.Immutable
import com.upper.trading.presentation.base.UiEffect
import com.upper.trading.presentation.base.UiIntent
import com.upper.trading.presentation.base.UiState

enum class AuthTab { SIGN_IN, SIGN_UP }
enum class AuthInputMode { EMAIL, PHONE }

@Immutable
data class AuthSignInState(
    // Tab & input mode
    val authTab: AuthTab = AuthTab.SIGN_IN,
    val inputMode: AuthInputMode = AuthInputMode.EMAIL,

    // Form inputs
    val email: String = "",
    val phoneNumber: String = "",
    val countryCode: String = "+84",
    val password: String = "",
    val confirmPassword: String = "",
    val isPasswordHidden: Boolean = true,
    val isConfirmPasswordHidden: Boolean = true,
    val isAgreeTerms: Boolean = false,

    // Validation & error states
    val emailError: String? = null,
    val phoneError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null,
    val generalError: String? = null,

    // UI flags
    val isLoading: Boolean = false,
    val isBiometricAvailable: Boolean = false
) : UiState


sealed interface AuthSignInIntent : UiIntent {
    // Tab and mode switching
    data class ChangeTab(val tab: AuthTab) : AuthSignInIntent
    data class ChangeInputMode(val mode: AuthInputMode) : AuthSignInIntent

    // Form text changes
    data class OnEmailChanged(val email: String) : AuthSignInIntent
    data class OnPhoneChanged(val phone: String) : AuthSignInIntent
    data class OnCountryCodeChanged(val code: String) : AuthSignInIntent
    data class OnPasswordChanged(val password: String) : AuthSignInIntent
    data class OnConfirmPasswordChanged(val confirmPassword: String) : AuthSignInIntent

    // Form actions
    data object TogglePasswordVisibility : AuthSignInIntent
    data object ToggleConfirmPasswordVisibility : AuthSignInIntent
    data class ToggleAgreeTerms(val isChecked: Boolean) : AuthSignInIntent

    // Submission actions
    data object Submit : AuthSignInIntent
    data object SignInWithGoogle : AuthSignInIntent
    data object SignInWithApple : AuthSignInIntent
    data object SignInWithBiometric : AuthSignInIntent
    data object ForgotPasswordClick : AuthSignInIntent

    // Navigation triggers
    data object BackClick : AuthSignInIntent
    data object CloseClick : AuthSignInIntent
}


sealed interface AuthSignInEffect : UiEffect {
    data class NavigateOTPScreen(val destination: String) : AuthSignInEffect
    data object NavigateCreatedSuccessScreen : AuthSignInEffect
    data object NavigateHome : AuthSignInEffect
    data object NavigateForgotPassword : AuthSignInEffect
    data object NavigateBack : AuthSignInEffect

    // System interactions
    data class ShowToast(val message: String) : AuthSignInEffect
    data object LaunchBiometricPrompt : AuthSignInEffect
}
