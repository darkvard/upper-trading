package com.upper.trading.presentation.features.auth.signin

import androidx.lifecycle.viewModelScope
import com.upper.trading.R
import com.upper.trading.core.utils.UiText
import com.upper.trading.core.utils.UiText.StringResource
import com.upper.trading.domain.model.Country
import com.upper.trading.domain.model.CountryPickerData
import com.upper.trading.domain.model.SignInCredentials
import com.upper.trading.domain.model.SignUpCredentials
import com.upper.trading.domain.usecase.auth.SignInUseCase
import com.upper.trading.domain.usecase.auth.SignUpUseCase
import com.upper.trading.domain.usecase.country.GetDefaultCountryUseCase
import com.upper.trading.domain.usecase.country.GetGroupedCountriesUseCase
import com.upper.trading.domain.usecase.country.GetPopularCountriesUseCase
import com.upper.trading.domain.validator.UserCredentialValidator
import com.upper.trading.presentation.base.BaseViewModel
import com.upper.trading.presentation.features.auth.signin.AuthSignInEffect.ShowToast
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toImmutableMap
import kotlinx.collections.immutable.toPersistentMap
import kotlinx.coroutines.Job
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
    private val signUpUseCase: SignUpUseCase,
    private val getPopularCountries: GetPopularCountriesUseCase,
    private val getGroupedCountries: GetGroupedCountriesUseCase,
    private val getDefaultCountry: GetDefaultCountryUseCase
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

    private var cachedGroupedCountries: Map<Char, List<Country>> = emptyMap()

    init {
        setupInitialCountries()
    }

    private fun setupInitialCountries() {
        viewModelScope.launch {
            val defaultCountry = getDefaultCountry()
            val groupedMap = getGroupedCountries().toImmutableMap()
            val popularList = getPopularCountries().toImmutableList()

            cachedGroupedCountries = groupedMap

            setState {
                copy(
                    countryPicker = CountryPickerData(
                        popularCountries = popularList,
                        groupedCountries = groupedMap
                    ),
                    signInForm = signInForm.copy(country = defaultCountry),
                    signUpForm = signUpForm.copy(country = defaultCountry)
                )
            }
        }
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
                        generalError = null,
                        signInForm = signInForm.copy(hasSubmitted = false),
                        signUpForm = signUpForm.copy(hasSubmitted = false)
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
                val isSignIn = currentState.authTab == AuthTab.SIGN_IN
                val hasSubmitted =
                    if (isSignIn) currentState.signInForm.hasSubmitted else currentState.signUpForm.hasSubmitted
                if (isSignIn) {
                    setState { copy(signInForm = signInForm.copy(email = intent.email)) }
                } else {
                    setState { copy(signUpForm = signUpForm.copy(email = intent.email)) }
                }
                emailValidationJob = emailValidationJob.launchDebounced(TYPING_DEBOUNCE_MS) {
                    val result = validator.validateEmail(intent.email, isSubmit = hasSubmitted)
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
                val isSignIn = currentState.authTab == AuthTab.SIGN_IN
                val hasSubmitted =
                    if (isSignIn) currentState.signInForm.hasSubmitted else currentState.signUpForm.hasSubmitted
                if (isSignIn) {
                    setState { copy(signInForm = signInForm.copy(phoneNumber = intent.phone)) }
                } else {
                    setState { copy(signUpForm = signUpForm.copy(phoneNumber = intent.phone)) }
                }
                phoneValidationJob = phoneValidationJob.launchDebounced(TYPING_DEBOUNCE_MS) {
                    val result = validator.validatePhone(intent.phone, isSubmit = hasSubmitted)
                    setState {
                        if (authTab == AuthTab.SIGN_IN) {
                            copy(signInForm = signInForm.copy(phoneError = result.errorMessage))
                        } else {
                            copy(signUpForm = signUpForm.copy(phoneError = result.errorMessage))
                        }
                    }
                }
            }

            is AuthSignInIntent.OnPasswordChanged -> {
                if (currentState.authTab == AuthTab.SIGN_IN) {
                    val hasSubmitted = currentState.signInForm.hasSubmitted
                    setState { copy(signInForm = signInForm.copy(password = intent.password)) }
                    passwordValidationJob =
                        passwordValidationJob.launchDebounced(TYPING_DEBOUNCE_MS) {
                            val result =
                                validator.validatePassword(intent.password, isSubmit = hasSubmitted)
                            setState { copy(signInForm = signInForm.copy(passwordError = result.errorMessage)) }
                        }
                } else {
                    val hasSubmitted = currentState.signUpForm.hasSubmitted
                    setState { copy(signUpForm = signUpForm.copy(password = intent.password)) }
                    passwordValidationJob =
                        passwordValidationJob.launchDebounced(TYPING_DEBOUNCE_MS) {
                            val result =
                                validator.validatePassword(intent.password, isSubmit = hasSubmitted)
                            setState { copy(signUpForm = signUpForm.copy(passwordError = result.errorMessage)) }
                        }
                    // Re-validate confirm password if it was already filled or if form was submitted
                    val confirmPassword = currentState.signUpForm.confirmPassword
                    if (confirmPassword.isNotEmpty() || hasSubmitted) {
                        confirmPasswordValidationJob =
                            confirmPasswordValidationJob.launchDebounced(TYPING_DEBOUNCE_MS) {
                                val result = validator.validateConfirmPassword(
                                    password = intent.password,
                                    confirmPassword = confirmPassword,
                                    isSubmit = hasSubmitted
                                )
                                setState { copy(signUpForm = signUpForm.copy(confirmPasswordError = result.errorMessage)) }
                            }
                    }
                }
            }

            is AuthSignInIntent.OnConfirmPasswordChanged -> {
                val hasSubmitted = currentState.signUpForm.hasSubmitted
                setState { copy(signUpForm = signUpForm.copy(confirmPassword = intent.confirmPassword)) }
                confirmPasswordValidationJob =
                    confirmPasswordValidationJob.launchDebounced(TYPING_DEBOUNCE_MS) {
                        val result = validator.validateConfirmPassword(
                            password = currentState.signUpForm.password,
                            confirmPassword = intent.confirmPassword,
                            isSubmit = hasSubmitted
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

            is AuthSignInIntent.OnSearchCountry -> {
                viewModelScope.launch {
                    val filtered = getGroupedCountries(intent.query)
                    setState {
                        copy(countryPicker = countryPicker.copy(groupedCountries = filtered.toPersistentMap()))
                    }
                }
            }

            is AuthSignInIntent.SelectedCountry -> {
                setState {
                    if (authTab == AuthTab.SIGN_IN) {
                        copy(signInForm = signInForm.copy(country = intent.country))
                    } else {
                        copy(signUpForm = signUpForm.copy(country = intent.country))
                    }
                }
            }

            AuthSignInIntent.ToggleCountryBottomSheetVisibility -> {
                setState {
                    val isOpen = !isCurrentFormOpenCountryBottomSheet
                    val resetPicker = if (!isOpen) {
                        countryPicker.copy(groupedCountries = cachedGroupedCountries.toPersistentMap())
                    } else countryPicker

                    if (authTab == AuthTab.SIGN_IN) {
                        copy(
                            signInForm = signInForm.copy(isCountryPickerOpen = isOpen),
                            countryPicker = resetPicker
                        )
                    } else {
                        copy(
                            signUpForm = signUpForm.copy(isCountryPickerOpen = isOpen),
                            countryPicker = resetPicker
                        )
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
                sendEffect(AuthSignInEffect.LaunchGoogleSignIn)
            }

            is AuthSignInIntent.OnGoogleSignInSuccess -> {
                handleGoogleSignIn(intent.idToken)
            }

            is AuthSignInIntent.OnGoogleSignInFailed -> {
                setState { copy(isLoading = false, generalError = intent.error) }
                sendEffect(ShowToast(intent.error, isError = true))
            }

            AuthSignInIntent.SignInWithApple -> {
                sendEffect(ShowToast(StringResource(R.string.msg_apple_sign_in)))
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
                        hasSubmitted = true,
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
                    sendEffect(AuthSignInEffect.ShowToast(UiText.StringResource(R.string.msg_sign_in_success), isError = false))
                    if (fromOnboarding) {
                        val destination = if (form.inputMode == AuthInputMode.EMAIL) {
                            form.email
                        } else {
                            "${form.country.code} ${form.phoneNumber}"
                        }
                        sendEffect(AuthSignInEffect.NavigateOTPScreen(destination))
                    } else {
                        sendEffect(AuthSignInEffect.NavigateHome)
                    }
                }
                .onFailure { error ->
                    val errorMsg = UiText.DynamicString(error.message ?: "Sign in failed")
                    setState {
                        copy(
                            isLoading = false,
                            generalError = errorMsg
                        )
                    }
                    sendEffect(ShowToast(message = errorMsg, isError = true))
                }
        }
    }

    private fun handleGoogleSignIn(idToken: String) {
        viewModelScope.launch {
            setState { copy(isLoading = true, generalError = null) }
            signInUseCase(SignInCredentials.Google(idToken))
                .onSuccess {
                    setState { copy(isLoading = false) }
                    sendEffect(ShowToast(UiText.StringResource(R.string.msg_sign_in_success), isError = false))
                    sendEffect(AuthSignInEffect.NavigateHome)
                }
                .onFailure { error ->
                    val errorMsg = UiText.DynamicString(error.message ?: "Google Sign In failed")
                    setState {
                        copy(
                            isLoading = false,
                            generalError = errorMsg
                        )
                    }
                    sendEffect(AuthSignInEffect.ShowToast(message = errorMsg, isError = true))
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
            validation.termsError?.let { sendEffect(AuthSignInEffect.ShowToast(it, isError = true)) }
            setState {
                copy(
                    signUpForm = signUpForm.copy(
                        hasSubmitted = true,
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
                .onSuccess {
                    val isEmail = form.inputMode == AuthInputMode.EMAIL
                    val destination = if (isEmail) {
                        form.email
                    } else {
                        "${form.country.code} ${form.phoneNumber}"
                    }
                    setState {
                        copy(
                            isLoading = false,
                            // If signed up with email: switch tab to Sign In and autofill registered email
                            authTab = if (isEmail) AuthTab.SIGN_IN else authTab,
                            signInForm = signInForm.copy(
                                inputMode = form.inputMode,
                                email = form.email,
                                phoneNumber = form.phoneNumber,
                                country = form.country,
                                emailError = null,
                                phoneError = null,
                                passwordError = null
                            )
                        )
                    }
                    sendEffect(ShowToast(UiText.StringResource(R.string.msg_sign_up_success), isError = false))
                    if (!isEmail) {
                        // Phone sign up requires OTP verification (account is pending OTP)
                        sendEffect(AuthSignInEffect.NavigateOTPScreen(destination))
                    }
                }
                .onFailure { error ->
                    val errorMsg = UiText.DynamicString(error.message ?: "Sign up failed")
                    setState {
                        copy(
                            isLoading = false,
                            generalError = errorMsg
                        )
                    }
                    sendEffect(ShowToast(message = errorMsg, isError = true))
                }
        }
    }

    private fun SignInFormState.toCredentials(): SignInCredentials {
        return if (inputMode == AuthInputMode.EMAIL) {
            SignInCredentials.Email(email = email.trim(), password = password)
        } else {
            SignInCredentials.Phone(
                countryDialCode = country.code,
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
                countryDialCode = country.code,
                phone = phoneNumber.trim(),
                password = password
            )
        }
    }
}
