package com.upper.trading.presentation.features.onboarding

import com.upper.trading.presentation.base.UiEffect
import com.upper.trading.presentation.base.UiIntent
import com.upper.trading.presentation.base.UiState

data class OnboardingState(
    val currentSlideIndex: Int = 0,
    val isDarkTheme: Boolean = false,
): UiState

sealed interface OnboardingIntent : UiIntent {
    data class SelectTheme(val isDark: Boolean) : OnboardingIntent
    data object ConfirmTheme: OnboardingIntent
    data class SwipeSlideIntro(val page: Int) : OnboardingIntent
    data object SkipOnboarding: OnboardingIntent
    data object CompleteIntro : OnboardingIntent
}

sealed interface OnboardingEffect: UiEffect {
    data object NavigateOnboardingIntro: OnboardingEffect
    data object NavigateAuthSignIn: OnboardingEffect
    data object NavigateHomeTrade: OnboardingEffect
}
