package com.upper.trading.presentation.features.auth.signin

import com.upper.trading.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AuthSignInViewModel @Inject constructor() :
    BaseViewModel<AuthSignInState, AuthSignInIntent, AuthSignInEffect>(
        AuthSignInState()
    ) {

    override fun onIntent(intent: AuthSignInIntent) {
        when (intent) {
            is AuthSignInIntent.ChangeTab -> {
                setState { copy(authTab = intent.tab) }
            }

            is AuthSignInIntent.ChangeInputMode -> {
                setState { copy(inputMode = intent.mode) }
            }

            is AuthSignInIntent.OnEmailChanged -> {
                setState { copy(email = intent.email, emailError = null) }
            }

            is AuthSignInIntent.OnPhoneChanged -> {
                setState { copy(phoneNumber = intent.phone, phoneError = null) }
            }

            is AuthSignInIntent.OnCountryCodeChanged -> {
                setState { copy(countryCode = intent.code) }
            }

            is AuthSignInIntent.OnPasswordChanged -> {
                setState { copy(password = intent.password, passwordError = null) }
            }

            is AuthSignInIntent.OnConfirmPasswordChanged -> {
                setState {
                    copy(
                        confirmPassword = intent.confirmPassword,
                        confirmPasswordError = null
                    )
                }
            }

            AuthSignInIntent.TogglePasswordVisibility -> {
                setState { copy(isPasswordHidden = !currentState.isPasswordHidden) }
            }

            AuthSignInIntent.ToggleConfirmPasswordVisibility -> {
                setState { copy(isConfirmPasswordHidden = !currentState.isConfirmPasswordHidden) }
            }

            is AuthSignInIntent.ToggleAgreeTerms -> {
                setState { copy(isAgreeTerms = intent.isChecked) }
            }

            AuthSignInIntent.BackClick -> {
                sendEffect(AuthSignInEffect.NavigateBack)
            }

            AuthSignInIntent.CloseClick -> {
                sendEffect(AuthSignInEffect.NavigateBack)
            }

            AuthSignInIntent.ForgotPasswordClick -> {
                sendEffect(AuthSignInEffect.NavigateForgotPassword)
            }

            AuthSignInIntent.Submit -> {

            }

            AuthSignInIntent.SignInWithGoogle -> {

            }

            AuthSignInIntent.SignInWithApple -> {

            }

            AuthSignInIntent.SignInWithBiometric -> {
                sendEffect(AuthSignInEffect.LaunchBiometricPrompt)
            }
        }
    }
}
