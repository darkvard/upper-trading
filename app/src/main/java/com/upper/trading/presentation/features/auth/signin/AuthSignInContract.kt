package com.upper.trading.presentation.features.auth.signin

import androidx.annotation.StringRes
import androidx.compose.runtime.Immutable
import com.upper.trading.R
import com.upper.trading.core.utils.UiText
import com.upper.trading.domain.model.Country
import com.upper.trading.domain.model.CountryPickerData
import com.upper.trading.presentation.base.UiEffect
import com.upper.trading.presentation.base.UiIntent
import com.upper.trading.presentation.base.UiState

enum class AuthTab(@param:StringRes val titleRes: Int) {
    SIGN_IN(R.string.sign_in),
    SIGN_UP(R.string.sign_up)
}

enum class AuthInputMode { EMAIL, PHONE }

@Immutable
data class SignInFormState(
    val inputMode: AuthInputMode = AuthInputMode.EMAIL,
    val email: String = "",
    val phoneNumber: String = "",
    val country: Country = Country(),
    val isCountryPickerOpen: Boolean = false,       // dropdown trailingIcon
    val password: String = "",
    val isPasswordHidden: Boolean = true,
    val hasSubmitted: Boolean = false,
    val emailError: UiText? = null,
    val phoneError: UiText? = null,
    val passwordError: UiText? = null,
)

@Immutable
data class SignUpFormState(
    val inputMode: AuthInputMode = AuthInputMode.EMAIL,
    val email: String = "",
    val phoneNumber: String = "",
    val country: Country = Country(),
    val isCountryPickerOpen: Boolean = false,           // dropdown trailingIcon
    val password: String = "",
    val confirmPassword: String = "",
    val isPasswordHidden: Boolean = true,
    val isConfirmPasswordHidden: Boolean = true,
    val isAgreeTerms: Boolean = false,
    val hasSubmitted: Boolean = false,
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
    val countryPicker: CountryPickerData = CountryPickerData(),
    val signInForm: SignInFormState = SignInFormState(),
    val signUpForm: SignUpFormState = SignUpFormState()
) : UiState {

    val isCurrentFormOpenCountryBottomSheet: Boolean
        get() = if (authTab == AuthTab.SIGN_IN) {
            signInForm.isCountryPickerOpen
        } else {
            signUpForm.isCountryPickerOpen
        }

    val currentCountrySelected: Country
        get() = if (authTab == AuthTab.SIGN_IN) {
            signInForm.country
        } else {
            signUpForm.country
        }
}

sealed interface AuthSignInIntent : UiIntent {
    // Tab and mode switching
    data class ChangeTab(val tab: AuthTab) : AuthSignInIntent
    data class ChangeInputMode(val mode: AuthInputMode) : AuthSignInIntent

    // Form text changes
    data class OnEmailChanged(val email: String) : AuthSignInIntent
    data class OnPhoneChanged(val phone: String) : AuthSignInIntent
    data class SelectedCountry(val country: Country) : AuthSignInIntent
    data class OnPasswordChanged(val password: String) : AuthSignInIntent
    data class OnConfirmPasswordChanged(val confirmPassword: String) : AuthSignInIntent

    data class OnSearchCountry(val query: String): AuthSignInIntent

    // Form actions
    data object TogglePasswordVisibility : AuthSignInIntent
    data object ToggleConfirmPasswordVisibility : AuthSignInIntent
    data class ToggleAgreeTerms(val isChecked: Boolean) : AuthSignInIntent
    data object ToggleCountryBottomSheetVisibility : AuthSignInIntent

    // Submission actions
    data object Submit : AuthSignInIntent
    data object SignInWithGoogle : AuthSignInIntent
    data class OnGoogleSignInSuccess(val idToken: String) : AuthSignInIntent
    data class OnGoogleSignInFailed(val error: UiText) : AuthSignInIntent
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
    data class ShowToast(val message: UiText, val isError: Boolean = false) : AuthSignInEffect
    data object LaunchBiometricPrompt : AuthSignInEffect
    data object LaunchGoogleSignIn : AuthSignInEffect
}
