package com.upper.trading.presentation.features.auth.signin

import androidx.lifecycle.viewModelScope
import com.upper.trading.R
import com.upper.trading.core.utils.UiText
import com.upper.trading.domain.model.SignInCredentials
import com.upper.trading.domain.model.SignUpCredentials
import com.upper.trading.domain.usecase.auth.SignInUseCase
import com.upper.trading.domain.usecase.auth.SignUpUseCase
import com.upper.trading.domain.validator.UserCredentialValidator
import com.upper.trading.presentation.base.BaseViewModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * AuthSignInViewModel manages authentication state and actions for Sign In and Sign Up.
 *
 * NOTE ON ASSISTED INJECTION:
 * When injecting multiple assisted parameters with the same type signature (e.g. multiple Booleans like
 * `isSignUp: Boolean` and `showProgressBar: Boolean`, or multiple Strings), Dagger Hilt requires explicit
 * identifier keys: `@Assisted("isSignUp")` and `@Assisted("showProgressBar")` in both the constructor
 * and the [Factory] interface.
 * Failure to provide unique keys for identical types causes a `[Dagger/DuplicateAssistedTypes]` compile-time error
 * and prevents Navigation / Hilt from mapping arguments incorrectly.
 */
@HiltViewModel(assistedFactory = AuthSignInViewModel.Factory::class)
class AuthSignInViewModel @AssistedInject constructor(
    @Assisted("isSignUp") private val isSignUp: Boolean,
    @Assisted("fromOnboarding") private val fromOnboarding: Boolean,
    private val validator: UserCredentialValidator,
    private val signInUseCase: SignInUseCase,
    private val signUpUseCase: SignUpUseCase
) : BaseViewModel<AuthSignInState, AuthSignInIntent, AuthSignInEffect>(
    AuthSignInState(
        authTab = if (isSignUp) AuthTab.SIGN_UP else AuthTab.SIGN_IN
    )
) {
    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("isSignUp") isSignUp: Boolean,
            @Assisted("fromOnboarding") fromOnboarding: Boolean
        ): AuthSignInViewModel
    }

    private var emailValidationJob: Job? = null
    private var phoneValidationJob: Job? = null
    private var passwordValidationJob: Job? = null
    private var confirmPasswordValidationJob: Job? = null

    companion object {
        private const val TYPING_DEBOUNCE_MS = 600L
    }

    override fun onIntent(intent: AuthSignInIntent) {
        when (intent) {
            is AuthSignInIntent.ChangeTab -> {
                cancelAllValidationJobs()
                val fromTab = currentState.authTab
                val toTab = intent.tab
                if (fromTab == toTab) return

                setState {
                    copy(
                        authTab = toTab,
                        generalError = null
                    )
                }
            }

            is AuthSignInIntent.ChangeInputMode -> {
                cancelJobs(emailValidationJob, phoneValidationJob)
                setState {
                    if (authTab == AuthTab.SIGN_IN) {
                        copy(
                            signInForm = signInForm.copy(
                                inputMode = intent.mode,
                                emailError = null,
                                phoneError = null
                            ),
                            generalError = null
                        )
                    } else {
                        copy(
                            signUpForm = signUpForm.copy(
                                inputMode = intent.mode,
                                emailError = null,
                                phoneError = null
                            ),
                            generalError = null
                        )
                    }
                }
            }

            is AuthSignInIntent.OnEmailChanged -> {
                if (currentState.authTab == AuthTab.SIGN_IN) {
                    setState { copy(signInForm = signInForm.copy(email = intent.email)) }
                } else {
                    setState { copy(signUpForm = signUpForm.copy(email = intent.email)) }
                }
                emailValidationJob = emailValidationJob.launchDebounced(TYPING_DEBOUNCE_MS) {
                    val result = validator.validateEmail(intent.email, isSubmit = false)
                    setState {
                        if (authTab == AuthTab.SIGN_IN) {
                            copy(signInForm = signInForm.copy(emailError = result.errorMessage))
                        } else {
                            copy(signUpForm = signUpForm.copy(emailError = result.errorMessage))
                        }
                    }
                }
            }

            is AuthSignInIntent.OnPhoneChanged -> {
                if (currentState.authTab == AuthTab.SIGN_IN) {
                    setState { copy(signInForm = signInForm.copy(phoneNumber = intent.phone)) }
                } else {
                    setState { copy(signUpForm = signUpForm.copy(phoneNumber = intent.phone)) }
                }
                phoneValidationJob = phoneValidationJob.launchDebounced(TYPING_DEBOUNCE_MS) {
                    val result = validator.validatePhone(intent.phone, isSubmit = false)
                    setState {
                        if (authTab == AuthTab.SIGN_IN) {
                            copy(signInForm = signInForm.copy(phoneError = result.errorMessage))
                        } else {
                            copy(signUpForm = signUpForm.copy(phoneError = result.errorMessage))
                        }
                    }
                }
            }

            is AuthSignInIntent.OnCountryCodeChanged -> {
                setState {
                    if (authTab == AuthTab.SIGN_IN) {
                        copy(signInForm = signInForm.copy(countryCode = intent.code))
                    } else {
                        copy(signUpForm = signUpForm.copy(countryCode = intent.code))
                    }
                }
            }

            is AuthSignInIntent.SelectCountry -> {
                setState {
                    if (authTab == AuthTab.SIGN_IN) {
                        copy(
                            signInForm = signInForm.copy(
                                countryCode = intent.code,
                                countryFlagRes = intent.flagRes,
                                isCountryPickerOpen = false
                            )
                        )
                    } else {
                        copy(
                            signUpForm = signUpForm.copy(
                                countryCode = intent.code,
                                countryFlagRes = intent.flagRes,
                                isCountryPickerOpen = false
                            )
                        )
                    }
                }
            }

            is AuthSignInIntent.OnPasswordChanged -> {
                if (currentState.authTab == AuthTab.SIGN_IN) {
                    setState { copy(signInForm = signInForm.copy(password = intent.password)) }
                    passwordValidationJob = passwordValidationJob.launchDebounced(TYPING_DEBOUNCE_MS) {
                        val result = validator.validatePassword(intent.password, isSubmit = false)
                        setState { copy(signInForm = signInForm.copy(passwordError = result.errorMessage)) }
                    }
                } else {
                    setState { copy(signUpForm = signUpForm.copy(password = intent.password)) }
                    passwordValidationJob = passwordValidationJob.launchDebounced(TYPING_DEBOUNCE_MS) {
                        val result = validator.validatePassword(intent.password, isSubmit = false)
                        setState { copy(signUpForm = signUpForm.copy(passwordError = result.errorMessage)) }
                    }
                    // Re-validate confirm password if it was already filled
                    if (currentState.signUpForm.confirmPassword.isNotEmpty()) {
                        confirmPasswordValidationJob = confirmPasswordValidationJob.launchDebounced(TYPING_DEBOUNCE_MS) {
                            val result = validator.validateConfirmPassword(
                                password = intent.password,
                                confirmPassword = currentState.signUpForm.confirmPassword,
                                isSubmit = false
                            )
                            setState { copy(signUpForm = signUpForm.copy(confirmPasswordError = result.errorMessage)) }
                        }
                    }
                }
            }

            is AuthSignInIntent.OnConfirmPasswordChanged -> {
                setState { copy(signUpForm = signUpForm.copy(confirmPassword = intent.confirmPassword)) }
                confirmPasswordValidationJob = confirmPasswordValidationJob.launchDebounced(TYPING_DEBOUNCE_MS) {
                    val result = validator.validateConfirmPassword(
                        password = currentState.signUpForm.password,
                        confirmPassword = intent.confirmPassword,
                        isSubmit = false
                    )
                    setState { copy(signUpForm = signUpForm.copy(confirmPasswordError = result.errorMessage)) }
                }
            }

            AuthSignInIntent.TogglePasswordVisibility -> {
                setState {
                    if (authTab == AuthTab.SIGN_IN) {
                        copy(signInForm = signInForm.copy(isPasswordHidden = !signInForm.isPasswordHidden))
                    } else {
                        copy(signUpForm = signUpForm.copy(isPasswordHidden = !signUpForm.isPasswordHidden))
                    }
                }
            }

            AuthSignInIntent.ToggleConfirmPasswordVisibility -> {
                setState {
                    copy(signUpForm = signUpForm.copy(isConfirmPasswordHidden = !signUpForm.isConfirmPasswordHidden))
                }
            }

            AuthSignInIntent.ToggleCountryPopupVisibility -> {
                setState {
                    if (authTab == AuthTab.SIGN_IN) {
                        copy(signInForm = signInForm.copy(isCountryPickerOpen = !signInForm.isCountryPickerOpen))
                    } else {
                        copy(signUpForm = signUpForm.copy(isCountryPickerOpen = !signUpForm.isCountryPickerOpen))
                    }
                }
            }

            is AuthSignInIntent.ToggleAgreeTerms -> {
                setState {
                    copy(
                        signUpForm = signUpForm.copy(isAgreeTerms = intent.isChecked),
                        generalError = null
                    )
                }
            }

            AuthSignInIntent.BackClick -> {
                sendEffect(AuthSignInEffect.NavigateBack)
            }

            AuthSignInIntent.CloseClick -> {
                sendEffect(AuthSignInEffect.NavigateHome)
            }

            AuthSignInIntent.ForgotPasswordClick -> {
                sendEffect(AuthSignInEffect.NavigateForgotPassword)
            }

            AuthSignInIntent.Submit -> {
                cancelAllValidationJobs()
                val state = currentState
                when (state.authTab) {
                    AuthTab.SIGN_IN -> handleSignIn(state.signInForm)
                    AuthTab.SIGN_UP -> handleSignUp(state.signUpForm)
                }
            }

            AuthSignInIntent.SignInWithGoogle -> {
                sendEffect(AuthSignInEffect.ShowToast(UiText.StringResource(R.string.msg_google_sign_in)))
            }

            AuthSignInIntent.SignInWithApple -> {
                sendEffect(AuthSignInEffect.ShowToast(UiText.StringResource(R.string.msg_apple_sign_in)))
            }

            AuthSignInIntent.SignInWithBiometric -> {
                sendEffect(AuthSignInEffect.LaunchBiometricPrompt)
            }
        }
    }

    private fun cancelAllValidationJobs() {
        cancelJobs(
            emailValidationJob,
            phoneValidationJob,
            passwordValidationJob,
            confirmPasswordValidationJob
        )
    }

    private fun handleSignIn(form: SignInFormState) {
        val validation = validator.validateSignIn(
            isEmailMode = form.inputMode == AuthInputMode.EMAIL,
            email = form.email,
            phone = form.phoneNumber,
            password = form.password
        )
        if (validation.hasError) {
            setState {
                copy(
                    signInForm = signInForm.copy(
                        emailError = validation.emailError,
                        phoneError = validation.phoneError,
                        passwordError = validation.passwordError
                    )
                )
            }
            return
        }

        viewModelScope.launch {
            setState {
                copy(
                    isLoading = true,
                    generalError = null,
                    signInForm = signInForm.copy(
                        emailError = null,
                        phoneError = null,
                        passwordError = null
                    )
                )
            }
            signInUseCase(form.toCredentials())
                .onSuccess {
                    setState { copy(isLoading = false) }
                    sendEffect(AuthSignInEffect.ShowToast(UiText.StringResource(R.string.msg_sign_in_success)))
                    if (fromOnboarding) {
                        val destination = if (form.inputMode == AuthInputMode.EMAIL) {
                            form.email
                        } else {
                            "${form.countryCode} ${form.phoneNumber}"
                        }
                        sendEffect(AuthSignInEffect.NavigateOTPScreen(destination))
                    } else {
                        sendEffect(AuthSignInEffect.NavigateHome)
                    }
                }
                .onFailure { error ->
                    setState {
                        copy(
                            isLoading = false,
                            generalError = UiText.DynamicString(error.message ?: "Sign in failed")
                        )
                    }
                }
        }
    }

    private fun handleSignUp(form: SignUpFormState) {
        val validation = validator.validateSignUp(
            isEmailMode = form.inputMode == AuthInputMode.EMAIL,
            email = form.email,
            phone = form.phoneNumber,
            password = form.password,
            confirmPassword = form.confirmPassword,
            isAgreeTerms = form.isAgreeTerms
        )
        if (validation.hasError) {
            validation.termsError?.let { sendEffect(AuthSignInEffect.ShowToast(it)) }
            setState {
                copy(
                    signUpForm = signUpForm.copy(
                        emailError = validation.emailError,
                        phoneError = validation.phoneError,
                        passwordError = validation.passwordError,
                        confirmPasswordError = validation.confirmPasswordError
                    )
                )
            }
            return
        }

        viewModelScope.launch {
            setState {
                copy(
                    isLoading = true,
                    generalError = null,
                    signUpForm = signUpForm.copy(
                        emailError = null,
                        phoneError = null,
                        passwordError = null,
                        confirmPasswordError = null
                    )
                )
            }
            signUpUseCase(form.toCredentials())
                .onSuccess { destination ->
                    val isEmail = form.inputMode == AuthInputMode.EMAIL
                    setState {
                        copy(
                            isLoading = false,
                            // If signed up with email: switch tab to Sign In and autofill registered email
                            authTab = if (isEmail) AuthTab.SIGN_IN else authTab,
                            signInForm = signInForm.copy(
                                inputMode = form.inputMode,
                                email = form.email,
                                phoneNumber = form.phoneNumber,
                                countryCode = form.countryCode,
                                countryFlagRes = form.countryFlagRes,
                                emailError = null,
                                phoneError = null,
                                passwordError = null
                            )
                        )
                    }
                    sendEffect(AuthSignInEffect.ShowToast(UiText.StringResource(R.string.msg_sign_up_success)))
                    if (!isEmail) {
                        // Phone sign up requires OTP verification (account is pending OTP)
                        sendEffect(AuthSignInEffect.NavigateOTPScreen(destination))
                    }
                }
                .onFailure { error ->
                    setState {
                        copy(
                            isLoading = false,
                            generalError = UiText.DynamicString(error.message ?: "Sign up failed")
                        )
                    }
                }
        }
    }

    private fun SignInFormState.toCredentials(): SignInCredentials {
        return if (inputMode == AuthInputMode.EMAIL) {
            SignInCredentials.Email(email = email.trim(), password = password)
        } else {
            SignInCredentials.Phone(
                countryCode = countryCode,
                phone = phoneNumber.trim(),
                password = password
            )
        }
    }

    private fun SignUpFormState.toCredentials(): SignUpCredentials {
        return if (inputMode == AuthInputMode.EMAIL) {
            SignUpCredentials.Email(email = email.trim(), password = password)
        } else {
            SignUpCredentials.Phone(
                countryCode = countryCode,
                phone = phoneNumber.trim(),
                password = password
            )
        }
    }
}
