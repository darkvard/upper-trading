package com.upper.trading.presentation.features.auth.signin

import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.biometric.BiometricManager
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Fingerprint
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.upper.trading.R
import com.upper.trading.core.biometric.BiometricAuthManager
import com.upper.trading.core.biometric.BiometricPromptHelper
import com.upper.trading.core.utils.ObserveAsEvents
import com.upper.trading.core.utils.ThemePreviews
import com.upper.trading.core.utils.TradingPreview
import com.upper.trading.core.utils.UiText
import com.upper.trading.core.utils.findFragmentActivity
import com.upper.trading.presentation.components.bottomsheet.country.CountryBottomSheet
import com.upper.trading.presentation.components.dialog.AppDialogData
import com.upper.trading.presentation.components.dialog.DialogType
import com.upper.trading.presentation.components.dialog.LocalAppDialog
import com.upper.trading.presentation.components.layout.ProgressBarActionLayout
import com.upper.trading.presentation.components.snackbar.LocalAppSnackbar
import com.upper.trading.presentation.components.tab.AppSegmentedControl
import com.upper.trading.presentation.features.auth.signin.components.AuthSignInFingerprint
import com.upper.trading.presentation.features.auth.signin.components.AuthSignInForm
import com.upper.trading.presentation.features.auth.signin.components.AuthSignUpForm
import com.upper.trading.presentation.features.auth.signin.components.AuthSocialSection
import com.upper.trading.presentation.features.auth.signin.components.rememberSocialAuthHandler
import com.upper.trading.presentation.navigation.AuthNavKey
import com.upper.trading.presentation.navigation.RegistrationFlowStep
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

@Immutable
data class AuthSignInStyle(
    val paddingHorizontal: Dp = 16.dp,
    val itemSpacing: Dp = 24.dp,
    val segmentPadding: Dp = 12.dp,
    val segmentPaddingTop: Dp = 4.dp,
    val segmentPaddingTopWithProgress: Dp = 24.dp,
    val fingerprintPaddingTop: Dp = 24.dp,
)

object AuthSignInDefaults {
    val style: AuthSignInStyle = AuthSignInStyle()
}

@Immutable
private class SignInInputStates(
    val email: TextFieldState,
    val phone: TextFieldState,
    val password: TextFieldState
)

@Immutable
private class SignUpInputStates(
    val email: TextFieldState,
    val phone: TextFieldState,
    val password: TextFieldState,
    val confirmPassword: TextFieldState
)

@Composable
fun AuthSignInRoute(
    onNavigateBack: () -> Unit,
    onNavigateToOtp: (otpKey: AuthNavKey.Otp) -> Unit,
    onNavigateHome: () -> Unit,
    onNavigateForgotPassword: () -> Unit,
    onNavigateCreatedSuccess: () -> Unit,
    modifier: Modifier = Modifier,
    isSignUp: Boolean = false,
    showProgressBar: Boolean = true,
    viewModel: AuthSignInViewModel = hiltViewModel(
        creationCallback = { factory: AuthSignInViewModel.Factory ->
            factory.create(
                isSignUp = isSignUp,
                fromOnboarding = showProgressBar
            )
        }
    )
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbar = LocalAppSnackbar.current
    val context = LocalContext.current
    val activity = remember(context) { context.findFragmentActivity() }
    val socialAuthHandler = rememberSocialAuthHandler(onIntent = viewModel::onIntent)

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        viewModel.onIntent(AuthSignInIntent.CheckBiometricOnResume)
    }

    ObserveAsEvents(events = viewModel.effect) { effect ->
        when (effect) {
            is AuthSignInEffect.ShowToast -> {
                if (effect.isError) {
                    snackbar.showError(title = effect.message)
                } else {
                    snackbar.showSuccess(title = effect.message)
                }
            }

            AuthSignInEffect.LaunchGoogleSignIn -> socialAuthHandler.launchGoogleSignIn()
            AuthSignInEffect.LaunchAppleSignIn -> socialAuthHandler.launchAppleSignIn()
            AuthSignInEffect.NavigateToBiometricEnrollment -> {
                activity?.let { fragmentActivity ->
                    navigateToBiometricEnrollment(fragmentActivity)
                }
            }
            is AuthSignInEffect.LaunchBiometricPrompt -> {
                activity?.let { fragmentActivity ->
                    launchBiometricPrompt(
                        activity = fragmentActivity,
                        cryptoObject = effect.cryptoObject,
                        challengeNonce = effect.challengeNonce,
                        onSuccess = { signatureString ->
                            viewModel.onIntent(
                                AuthSignInIntent.OnBiometricSuccess(
                                    signatureString = signatureString,
                                    challengeNonce = effect.challengeNonce
                                )
                            )
                        },
                        onError = { errorMessage ->
                            viewModel.onIntent(AuthSignInIntent.OnBiometricFailed(errorMessage))
                        }
                    )
                }
            }
            AuthSignInEffect.NavigateBack -> onNavigateBack()
            is AuthSignInEffect.NavigateOTPScreen -> onNavigateToOtp(effect.otpKey)
            AuthSignInEffect.NavigateHome -> onNavigateHome()
            AuthSignInEffect.NavigateForgotPassword -> onNavigateForgotPassword()
            AuthSignInEffect.NavigateCreatedSuccessScreen -> onNavigateCreatedSuccess()
        }
    }

    AuthSignInScreen(
        state = state,
        onIntent = viewModel::onIntent,
        modifier = modifier
    )
}

@Composable
fun AuthSignInScreen(
    state: AuthSignInState,
    onIntent: (AuthSignInIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val appDialog = LocalAppDialog.current

    AuthSignInContent(
        state = state,
        onIntent = onIntent,
        modifier = modifier
    )

    LaunchedEffect(state.showBiometricEnrollDialog) {
        if (state.showBiometricEnrollDialog) {
            appDialog.show(
                AppDialogData(
                    type = DialogType.GUIDE,
                    icon = Icons.Rounded.Fingerprint,
                    title = UiText.StringResource(R.string.biometric_enroll_dialog_title),
                    description = UiText.StringResource(R.string.biometric_enroll_dialog_message),
                    steps = persistentListOf(
                        UiText.StringResource(R.string.biometric_guide_step_1),
                        UiText.StringResource(R.string.biometric_guide_step_2),
                        UiText.StringResource(R.string.biometric_guide_step_3)
                    ),
                    confirmName = UiText.StringResource(R.string.biometric_enroll_dialog_confirm),
                    dismissName = UiText.StringResource(R.string.cancel),
                    onConfirm = { onIntent(AuthSignInIntent.ConfirmBiometricEnrollment) },
                    onDismiss = { onIntent(AuthSignInIntent.DismissBiometricEnrollDialog) }
                )
            )
        } else {
            appDialog.dismiss()
        }
    }

    if (state.isCurrentFormOpenCountryBottomSheet) {
        SelectCountryBottomSheet(
            state = state,
            onIntent = onIntent,
            modifier = modifier
        )
    }
}

@Composable
private fun AuthSignInContent(
    state: AuthSignInState,
    onIntent: (AuthSignInIntent) -> Unit,
    modifier: Modifier = Modifier,
    scrollState: ScrollState = rememberScrollState(),
    style: AuthSignInStyle = AuthSignInDefaults.style
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val coroutineScope = rememberCoroutineScope()

    val progressBarState = remember(state.isShowProgressBar) {
        if (state.isShowProgressBar) {
            RegistrationFlowStep.SIGN_UP.toProgressBarState()
        } else {
            null
        }
    }

    val signInInputStates = rememberSignInInputStates(formState = state.signInForm, onIntent = onIntent)
    val signUpInputStates = rememberSignUpInputStates(formState = state.signUpForm, onIntent = onIntent)

    val handleIntent: (AuthSignInIntent) -> Unit = remember(
        onIntent, keyboardController, focusManager, scrollState, coroutineScope
    ) {
        { intent ->
            if (intent is AuthSignInIntent.Submit) {
                keyboardController?.hide()
                focusManager.clearFocus()
                coroutineScope.launch {
                    delay(150)
                    scrollState.animateScrollTo(0)
                    scrollState.scrollTo(0)
                }
            }
            onIntent(intent)
        }
    }

    ProgressBarActionLayout(
        progressBarState = progressBarState,
        onBackClick = { onIntent(AuthSignInIntent.BackClick) },
        onCloseClick = if (state.isShowProgressBar) {
            { onIntent(AuthSignInIntent.CloseClick) }
        } else null,
        modifier = modifier
    ) {
        AuthSignInSection(
            state = state,
            onIntent = handleIntent,
            signInInputStates = signInInputStates,
            signUpInputStates = signUpInputStates,
            hasProgressBar = progressBarState != null,
            scrollState = scrollState,
            style = style,
            modifier = Modifier.weight(1f)
        )
    }
}


@Composable
private fun rememberSignInInputStates(
    formState: SignInFormState,
    onIntent: (AuthSignInIntent) -> Unit
): SignInInputStates {
    val emailState = rememberTextFieldState(initialText = formState.email)
    val phoneState = rememberTextFieldState(initialText = formState.phoneNumber)
    val passwordState = rememberTextFieldState(initialText = formState.password)

    val currentFormState by rememberUpdatedState(formState)

    LaunchedEffect(emailState) {
        snapshotFlow { emailState.text.toString() }
            .collect { text ->
                if (text != currentFormState.email) onIntent(AuthSignInIntent.OnEmailChanged(text))
            }
    }

    LaunchedEffect(phoneState) {
        snapshotFlow { phoneState.text.toString() }
            .collect { text ->
                if (text != currentFormState.phoneNumber) onIntent(AuthSignInIntent.OnPhoneChanged(text))
            }
    }

    LaunchedEffect(passwordState) {
        snapshotFlow { passwordState.text.toString() }
            .collect { text ->
                if (text != currentFormState.password) onIntent(AuthSignInIntent.OnPasswordChanged(text))
            }
    }

    // Sync autofilled credentials into SignIn TextFieldState only when sign-up succeeds
    LaunchedEffect(formState.email) {
        if (emailState.text.toString() != formState.email) {
            emailState.edit { replace(0, length, formState.email) }
        }
    }
    LaunchedEffect(formState.phoneNumber) {
        if (phoneState.text.toString() != formState.phoneNumber) {
            phoneState.edit { replace(0, length, formState.phoneNumber) }
        }
    }

    return remember(emailState, phoneState, passwordState) {
        SignInInputStates(
            email = emailState,
            phone = phoneState,
            password = passwordState
        )
    }
}

@Composable
private fun rememberSignUpInputStates(
    formState: SignUpFormState,
    onIntent: (AuthSignInIntent) -> Unit
): SignUpInputStates {
    val emailState = rememberTextFieldState(initialText = formState.email)
    val phoneState = rememberTextFieldState(initialText = formState.phoneNumber)
    val passwordState = rememberTextFieldState(initialText = formState.password)
    val confirmPasswordState = rememberTextFieldState(initialText = formState.confirmPassword)

    val currentFormState by rememberUpdatedState(formState)

    LaunchedEffect(emailState) {
        snapshotFlow { emailState.text.toString() }
            .collect { text ->
                if (text != currentFormState.email) onIntent(AuthSignInIntent.OnEmailChanged(text))
            }
    }

    LaunchedEffect(phoneState) {
        snapshotFlow { phoneState.text.toString() }
            .collect { text ->
                if (text != currentFormState.phoneNumber) onIntent(AuthSignInIntent.OnPhoneChanged(text))
            }
    }

    LaunchedEffect(passwordState) {
        snapshotFlow { passwordState.text.toString() }
            .collect { text ->
                if (text != currentFormState.password) onIntent(AuthSignInIntent.OnPasswordChanged(text))
            }
    }

    LaunchedEffect(confirmPasswordState) {
        snapshotFlow { confirmPasswordState.text.toString() }
            .collect { text ->
                if (text != currentFormState.confirmPassword) {
                    onIntent(AuthSignInIntent.OnConfirmPasswordChanged(text))
                }
            }
    }

    return remember(emailState, phoneState, passwordState, confirmPasswordState) {
        SignUpInputStates(
            email = emailState,
            phone = phoneState,
            password = passwordState,
            confirmPassword = confirmPasswordState
        )
    }
}

@Composable
private fun AuthSignInSection(
    state: AuthSignInState,
    onIntent: (AuthSignInIntent) -> Unit,
    signInInputStates: SignInInputStates,
    signUpInputStates: SignUpInputStates,
    hasProgressBar: Boolean,
    scrollState: ScrollState,
    style: AuthSignInStyle,
    modifier: Modifier = Modifier
) {
    val tabs = AuthTab.entries.toPersistentList()
    val tabLabels = tabs.associateWith { stringResource(it.titleRes) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .imePadding()
            .navigationBarsPadding()
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(style.itemSpacing),
    ) {
        AppSegmentedControl(
            items = tabs,
            selectedItem = state.authTab,
            onItemSelected = { onIntent(AuthSignInIntent.ChangeTab(it)) },
            itemLabel = { tab -> tabLabels[tab].orEmpty() },
            modifier = Modifier.padding(
                top = if (hasProgressBar) {
                    style.segmentPaddingTopWithProgress
                } else {
                    style.segmentPaddingTop
                },
                start = style.segmentPadding,
                end = style.segmentPadding
            )
        )

        Text(
            text = stringResource(state.authTab.titleRes),
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(horizontal = style.paddingHorizontal)
        )

        AnimatedContent(
            targetState = state.authTab,
            transitionSpec = {
                (fadeIn(animationSpec = tween(220)) togetherWith fadeOut(
                    animationSpec = tween(180)
                )) using null
            },
            label = "AuthSignModeTransition"
        ) { tab ->
            when (tab) {
                AuthTab.SIGN_IN -> {
                    AuthSignInForm(
                        formState = state.signInForm,
                        isLoading = state.isLoading,
                        onIntent = onIntent,
                        emailState = signInInputStates.email,
                        phoneState = signInInputStates.phone,
                        passwordState = signInInputStates.password,
                        modifier = Modifier.padding(horizontal = style.paddingHorizontal)
                    )
                }

                AuthTab.SIGN_UP -> {
                    AuthSignUpForm(
                        formState = state.signUpForm,
                        isLoading = state.isLoading,
                        onIntent = onIntent,
                        emailState = signUpInputStates.email,
                        phoneState = signUpInputStates.phone,
                        passwordState = signUpInputStates.password,
                        confirmPasswordState = signUpInputStates.confirmPassword,
                        modifier = Modifier.padding(horizontal = style.paddingHorizontal)
                    )
                }
            }
        }

        AuthSocialSection(
            onIntent = onIntent,
            modifier = Modifier.padding(horizontal = style.paddingHorizontal)
        )

        AuthSignInFingerprint(
            onIntent = onIntent,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = style.fingerprintPaddingTop)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class, FlowPreview::class)
@Composable
private fun SelectCountryBottomSheet(
    state: AuthSignInState,
    onIntent: (AuthSignInIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val bottomSheetState = rememberBottomSheetState(
        initialValue = SheetValue.Hidden,
        enabledValues = setOf(SheetValue.Hidden, SheetValue.Expanded)
    )
    val searchQueryState = rememberTextFieldState()

    LaunchedEffect(searchQueryState) {
        snapshotFlow { searchQueryState.text.toString() }
            .distinctUntilChanged()
            .debounce(300.milliseconds)
            .collect {
                onIntent(AuthSignInIntent.OnSearchCountry(it))
            }
    }

    CountryBottomSheet(
        countries = state.countryPicker,
        selectedCountry = state.currentCountrySelected,
        searchQueryState = searchQueryState,
        onSelectCountry = { country ->
            onIntent(AuthSignInIntent.SelectedCountry(country))
        },
        onDismiss = {
            onIntent(AuthSignInIntent.ToggleCountryBottomSheetVisibility)
        },
        sheetState = bottomSheetState,
        modifier = modifier
    )
}

private fun navigateToBiometricEnrollment(activity: FragmentActivity) {
    val intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        Intent(Settings.ACTION_BIOMETRIC_ENROLL).apply {
            putExtra(
                Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                BiometricManager.Authenticators.BIOMETRIC_STRONG
            )
        }
    } else {
        Intent(Settings.ACTION_SECURITY_SETTINGS)
    }
    try {
        activity.startActivity(intent)
    } catch (e: Exception) {
        activity.startActivity(Intent(Settings.ACTION_SETTINGS))
    }
}

private fun launchBiometricPrompt(
    activity: FragmentActivity,
    cryptoObject: androidx.biometric.BiometricPrompt.CryptoObject,
    challengeNonce: String,
    onSuccess: (signatureString: String) -> Unit,
    onError: (errorMessage: UiText) -> Unit
) {
    BiometricPromptHelper.showBiometricPrompt(
        activity = activity,
        cryptoObject = cryptoObject,
        title = activity.getString(R.string.biometric_prompt_title),
        subtitle = activity.getString(R.string.biometric_prompt_subtitle),
        negativeButtonText = activity.getString(R.string.cancel),
        onSuccess = { signature ->
            val base64Signature = BiometricAuthManager.signChallenge(signature, challengeNonce).orEmpty()
            onSuccess(base64Signature)
        },
        onError = { _, errString ->
            onError(UiText.DynamicString(errString.toString()))
        },
        onFailed = {}
    )
}

@ThemePreviews
@Composable
fun PreviewAuthSignInScreen() {
    TradingPreview {
        AuthSignInScreen(
            state = AuthSignInState().copy(isShowProgressBar = false),
            onIntent = {},
        )
    }
}

@ThemePreviews
@Composable
fun PreviewAuthSignUpScreen() {
    TradingPreview {
        AuthSignInScreen(
            state = AuthSignInState(
                authTab = AuthTab.SIGN_UP,
                signUpForm = SignUpFormState(inputMode = AuthInputMode.PHONE),
                isShowProgressBar = true
            ),
            onIntent = {},
        )
    }
}
