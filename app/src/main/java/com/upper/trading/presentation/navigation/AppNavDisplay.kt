package com.upper.trading.presentation.navigation

import androidx.compose.animation.ContentTransform
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.upper.trading.presentation.features.auth.otp.AuthOtpScreen
import com.upper.trading.presentation.features.auth.signin.AuthSignInRoute
import com.upper.trading.presentation.features.card.AddCardScreen
import com.upper.trading.presentation.features.card.CardVerifyScreen
import com.upper.trading.presentation.features.kyc.account.AccountAddressScreen
import com.upper.trading.presentation.features.kyc.account.AccountCountryScreen
import com.upper.trading.presentation.features.kyc.account.AccountPersonalScreen
import com.upper.trading.presentation.features.kyc.document.DocumentScanScreen
import com.upper.trading.presentation.features.kyc.processing.KycProcessingScreen
import com.upper.trading.presentation.features.kyc.selfie.SelfieScreen
import com.upper.trading.presentation.features.onboarding.OnboardingIntroRoute
import com.upper.trading.presentation.features.onboarding.OnboardingRegistrationRoute
import com.upper.trading.presentation.features.onboarding.OnboardingThemeRoute
import com.upper.trading.presentation.features.splash.SplashRoute
import com.upper.trading.presentation.features.trade.TradeRoute


private const val NAV_METADATA_PROGRESS_STEP = "is_progress_step"
private const val NAV_METADATA_SPLASH = "is_splash"

val StepMetadata = mapOf(NAV_METADATA_PROGRESS_STEP to true)
private val SplashMetadata = mapOf(NAV_METADATA_SPLASH to true)

inline fun <reified K : AppNavKey> EntryProviderScope<AppNavKey>.stepEntry(
    noinline content: @Composable (K) -> Unit
) {
    entry<K>(
        metadata = StepMetadata,
        content = content
    )
}

@Composable
fun AppNavDisplay(
    navigator: Navigator<AppNavKey> = rememberAppNavigator(startDestination = AuthNavKey.SignIn())  // todo default = SplashNavKey
) {
    NavDisplay(
        backStack = navigator.backStack,
        onBack = { navigator.pop() },
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        transitionSpec = {
            val isFromSplash = initialState.metadata[NAV_METADATA_SPLASH] == true
            val isStepTransition = initialState.metadata[NAV_METADATA_PROGRESS_STEP] == true &&
                    targetState.metadata[NAV_METADATA_PROGRESS_STEP] == true

            if (isFromSplash || isStepTransition) {
                ContentTransform(
                    targetContentEnter = EnterTransition.None,
                    initialContentExit = ExitTransition.None
                )
            } else {
                ContentTransform(
                    targetContentEnter = fadeIn(tween(300)),
                    initialContentExit = fadeOut(tween(300))
                )
            }
        },
        entryProvider = entryProvider {
            splashGraph(navigator)
            onboardingGraph(navigator)
            homeGraph(navigator)
            authGraph(navigator)
            profileGraph(navigator)
            kycGraph(navigator)
            cardGraph(navigator)
        }
    )
}

fun EntryProviderScope<AppNavKey>.homeGraph(navigator: Navigator<AppNavKey>) {
    entry<MainNavKey.Trade> {
        TradeRoute()
    }
}

fun EntryProviderScope<AppNavKey>.splashGraph(navigator: Navigator<AppNavKey>) {
    entry<SplashNavKey>(metadata = SplashMetadata) {
        SplashRoute(
            onNavigateOnboardingTheme = {
                navigator.replace(OnboardingNavKey.Theme)
            },
            onNavigateMainTrade = {
                navigator.replace(MainNavKey.Trade)
            }
        )
    }
}

fun EntryProviderScope<AppNavKey>.onboardingGraph(navigator: Navigator<AppNavKey>) {
    entry<OnboardingNavKey.Theme> {
        OnboardingThemeRoute(
            onNavigateToIntro = {
                navigator.push(OnboardingNavKey.Intro)
            },
            onNavigateToHomeTrade = {
                navigator.replaceAll(MainNavKey.Trade)
            }
        )
    }
    entry<OnboardingNavKey.Intro> {
        OnboardingIntroRoute(
            onNavigateToRegistration = {
                navigator.replaceAll(OnboardingNavKey.Registration)
            },
            onNavigateToHomeTrade = {
                navigator.replaceAll(MainNavKey.Trade)
            }
        )
    }
    entry<OnboardingNavKey.Registration> {
        OnboardingRegistrationRoute(
            onNavigateToSignIn = {
                navigator.push(AuthNavKey.SignIn(isSignUp = false, showProgressBar = true))
            },
            onNavigateToSignUp = {
                navigator.push(AuthNavKey.SignIn(isSignUp = true, showProgressBar = true))
            },
            onNavigateToHomeTrade = {
                navigator.replaceAll(MainNavKey.Trade)
            }
        )
    }
}

fun EntryProviderScope<AppNavKey>.authGraph(navigator: Navigator<AppNavKey>) {
    entry<AuthNavKey.SignIn>(
        metadata = { key -> mapOf(NAV_METADATA_PROGRESS_STEP to key.showProgressBar) }
    ) { entry ->
        AuthSignInRoute(
            isSignUp = entry.isSignUp,
            showProgressBar = entry.showProgressBar,
            onNavigateBack = { navigator.pop() },
            onNavigateToOtp = { destination ->
                navigator.replaceAll(
                    AuthNavKey.Otp(
                        destination = destination,
                        fromOnboarding = entry.showProgressBar
                    )
                )
            },
            onNavigateHome = {
                navigator.replaceAll(MainNavKey.Trade)
            },
            onNavigateForgotPassword = {},
            onNavigateCreatedSuccess = {}
        )
    }

    entry<AuthNavKey.Otp>(
        metadata = { key -> mapOf(NAV_METADATA_PROGRESS_STEP to key.fromOnboarding) }
    ) { entry ->
        AuthOtpScreen(
            destination = entry.destination,
            progressBarState = if (entry.fromOnboarding) {
                RegistrationFlowStep.OTP.toProgressBarState()
            } else {
                null
            },
            onBackClick = { navigator.pop() },
            onCloseClick = { navigator.replaceAll(MainNavKey.Trade) },
            onNextClick = {
                if (entry.fromOnboarding) {
                    navigator.push(ProfileNavKey.AccountSetup(isStandalone = false))
                } else {
                    navigator.replaceAll(MainNavKey.Trade)
                }
            }
        )
    }
}

fun EntryProviderScope<AppNavKey>.profileGraph(navigator: Navigator<AppNavKey>) {
    stepEntry<ProfileNavKey.AccountSetup> { entry ->
        val progress = if (entry.isStandalone) {
            StandaloneAccountSetupFlowStep.PERSONAL_INFO.toProgressBarState()
        } else {
            RegistrationFlowStep.ACCOUNT_PERSONAL.toProgressBarState()
        }
        AccountPersonalScreen(
            progressBarState = progress,
            onBackClick = { navigator.pop() },
            onCloseClick = { navigator.replaceAll(MainNavKey.Trade) },
            onNextClick = { navigator.push(ProfileNavKey.AccountAddress(isStandalone = entry.isStandalone)) }
        )
    }
    stepEntry<ProfileNavKey.AccountAddress> { entry ->
        val progress = if (entry.isStandalone) {
            StandaloneAccountSetupFlowStep.HOME_ADDRESS.toProgressBarState()
        } else {
            RegistrationFlowStep.ACCOUNT_ADDRESS.toProgressBarState()
        }
        AccountAddressScreen(
            progressBarState = progress,
            onBackClick = { navigator.pop() },
            onCloseClick = { navigator.replaceAll(MainNavKey.Trade) },
            onNextClick = { navigator.push(ProfileNavKey.AccountCountry(isStandalone = entry.isStandalone)) }
        )
    }
    stepEntry<ProfileNavKey.AccountCountry> { entry ->
        val progress = if (entry.isStandalone) {
            StandaloneAccountSetupFlowStep.COUNTRY.toProgressBarState()
        } else {
            RegistrationFlowStep.ACCOUNT_COUNTRY.toProgressBarState()
        }
        AccountCountryScreen(
            progressBarState = progress,
            onBackClick = { navigator.pop() },
            onCloseClick = { navigator.replaceAll(MainNavKey.Trade) },
            onNextClick = {
                if (entry.isStandalone) {
                    navigator.replaceAll(MainNavKey.Trade)
                } else {
                    navigator.push(KycNavKey.Selfie(isStandalone = false))
                }
            }
        )
    }
}

fun EntryProviderScope<AppNavKey>.kycGraph(navigator: Navigator<AppNavKey>) {
    stepEntry<KycNavKey.Selfie> { entry ->
        val progress = if (entry.isStandalone) {
            StandaloneKycFlowStep.SELFIE.toProgressBarState()
        } else {
            RegistrationFlowStep.KYC_SELFIE.toProgressBarState()
        }
        SelfieScreen(
            progressBarState = progress,
            onBackClick = { navigator.pop() },
            onCloseClick = { navigator.replaceAll(MainNavKey.Trade) },
            onNextClick = { navigator.push(KycNavKey.DocumentScan(isStandalone = entry.isStandalone)) }
        )
    }
    stepEntry<KycNavKey.DocumentScan> { entry ->
        val progress = if (entry.isStandalone) {
            StandaloneKycFlowStep.DOCUMENT.toProgressBarState()
        } else {
            RegistrationFlowStep.KYC_DOCUMENT.toProgressBarState()
        }
        DocumentScanScreen(
            progressBarState = progress,
            onBackClick = { navigator.pop() },
            onCloseClick = { navigator.replaceAll(MainNavKey.Trade) },
            onNextClick = { navigator.push(KycNavKey.Processing(isStandalone = entry.isStandalone)) }
        )
    }
    stepEntry<KycNavKey.Processing> { entry ->
        val progress = if (entry.isStandalone) {
            StandaloneKycFlowStep.PROCESSING.toProgressBarState()
        } else {
            RegistrationFlowStep.KYC_PROCESSING.toProgressBarState()
        }
        KycProcessingScreen(
            progressBarState = progress,
            onBackClick = { navigator.pop() },
            onCloseClick = { navigator.replaceAll(MainNavKey.Trade) },
            onNextClick = {
                if (entry.isStandalone) {
                    navigator.replaceAll(MainNavKey.Trade)
                } else {
                    navigator.push(CardNavKey.AddCard(isStandalone = false))
                }
            }
        )
    }
}

fun EntryProviderScope<AppNavKey>.cardGraph(navigator: Navigator<AppNavKey>) {
    stepEntry<CardNavKey.AddCard> { entry ->
        val progress = if (entry.isStandalone) {
            StandaloneCardFlowStep.CARD_INFO.toProgressBarState()
        } else {
            RegistrationFlowStep.CARD_INFO.toProgressBarState()
        }
        AddCardScreen(
            progressBarState = progress,
            onBackClick = { navigator.pop() },
            onCloseClick = { navigator.replaceAll(MainNavKey.Trade) },
            onNextClick = { navigator.push(CardNavKey.Verify(isStandalone = entry.isStandalone)) }
        )
    }
    stepEntry<CardNavKey.Verify> { entry ->
        val progress = if (entry.isStandalone) {
            StandaloneCardFlowStep.CARD_VERIFY.toProgressBarState()
        } else {
            RegistrationFlowStep.CARD_VERIFY.toProgressBarState()
        }
        CardVerifyScreen(
            progressBarState = progress,
            onBackClick = { navigator.pop() },
            onCloseClick = { navigator.replaceAll(MainNavKey.Trade) },
            onCompleteClick = { navigator.replaceAll(MainNavKey.Trade) }
        )
    }
}
