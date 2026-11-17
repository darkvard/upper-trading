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
import com.upper.trading.presentation.features.onboarding.OnboardingIntroRoute
import com.upper.trading.presentation.features.onboarding.OnboardingThemeRoute
import com.upper.trading.presentation.features.splash.SplashRoute
import com.upper.trading.presentation.features.trade.TradeRoute


@Composable
fun AppNavDisplay(
    navigator: Navigator<AppNavKey> = rememberAppNavigator(startDestination = SplashNavKey)
) {
    NavDisplay(
        backStack = navigator.backStack,
        onBack = { navigator.pop() },
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        transitionSpec = {
            if (initialState.key == SplashNavKey) {
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
        }
    )
}

fun EntryProviderScope<AppNavKey>.homeGraph(navigator: Navigator<AppNavKey>) {
    entry<MainNavKey.Trade> {
        TradeRoute()
    }
}

fun EntryProviderScope<AppNavKey>.splashGraph(navigator: Navigator<AppNavKey>) {
    entry<SplashNavKey> {
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
            }
        )
    }
    entry<OnboardingNavKey.Intro> {
        OnboardingIntroRoute(
            onNavigateToAuthSignIn = {
                navigator.replaceAll(AuthNavKey.SignIn)
            },
            onNavigateToMainTrade = {
                navigator.replaceAll(MainNavKey.Trade)
            }
        )
    }
}