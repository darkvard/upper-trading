package com.upper.trading.presentation.features.auth.signin

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.runtime.Immutable
import com.upper.trading.R
import com.upper.trading.core.utils.UiText
import com.upper.trading.presentation.base.UiEffect
import com.upper.trading.presentation.base.UiIntent
import com.upper.trading.presentation.base.UiState

enum class AuthTab(@StringRes val titleRes: Int) {
    SIGN_IN(R.string.sign_in),
    SIGN_UP(R.string.sign_up)
}

enum class AuthInputMode { EMAIL, PHONE }

@Immutable
data class SignInFormState(
    val inputMode: AuthInputMode = AuthInputMode.EMAIL,
    val email: String = "",
    val phoneNumber: String = "",
    val countryCode: String = "+84",
    @DrawableRes val countryFlagRes: Int = R.drawable.ic_flag_vn,
    val isCountryPickerOpen: Boolean = false,
    val password: String = "",
    val isPasswordHidden: Boolean = true,
    val emailError: UiText? = null,
    val phoneError: UiText? = null,
    val passwordError: UiText? = null,
)

@Immutable
data class SignUpFormState(
    val inputMode: AuthInputMode = AuthInputMode.EMAIL,
    val email: String = "",
    val phoneNumber: String = "",
    val countryCode: String = "+84",
    @DrawableRes val countryFlagRes: Int = R.drawable.ic_flag_vn,
    val isCountryPickerOpen: Boolean = false,
    val password: String = "",
    val confirmPassword: String = "",
    val isPasswordHidden: Boolean = true,
    val isConfirmPasswordHidden: Boolean = true,
    val isAgreeTerms: Boolean = false,
    val emailError: UiText? = null,
    val phoneError: UiText? = null,
    val passwordError: UiText? = null,
    val confirmPasswordError: UiText? = null,
)

@Immutable
data class AuthSignInState(
    val authTab: AuthTab = AuthTab.SIGN_IN,
    val isLoading: Boolean = false,
    val isBiometricAvailable: Boolean = false,
    val generalError: UiText? = null,
    val signInForm: SignInFormState = SignInFormState(),
    val signUpForm: SignUpFormState = SignUpFormState()
) : UiState


sealed interface AuthSignInIntent : UiIntent {
    // Tab and mode switching
    data class ChangeTab(val tab: AuthTab) : AuthSignInIntent
    data class ChangeInputMode(val mode: AuthInputMode) : AuthSignInIntent

    // Form text changes
    data class OnEmailChanged(val email: String) : AuthSignInIntent
    data class OnPhoneChanged(val phone: String) : AuthSignInIntent
    data class OnCountryCodeChanged(val code: String) : AuthSignInIntent
    data class SelectCountry(val code: String, @DrawableRes val flagRes: Int) : AuthSignInIntent
    data class OnPasswordChanged(val password: String) : AuthSignInIntent
    data class OnConfirmPasswordChanged(val confirmPassword: String) : AuthSignInIntent

    // Form actions
    data object TogglePasswordVisibility : AuthSignInIntent
    data object ToggleConfirmPasswordVisibility : AuthSignInIntent
    data class ToggleAgreeTerms(val isChecked: Boolean) : AuthSignInIntent
    data object ToggleCountryPopupVisibility : AuthSignInIntent

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
    data class ShowToast(val message: UiText) : AuthSignInEffect
    data object LaunchBiometricPrompt : AuthSignInEffect
}
