package com.upper.trading.presentation.features.onboarding

import com.upper.trading.domain.model.IntroSlide
import com.upper.trading.presentation.base.UiEffect
import com.upper.trading.presentation.base.UiIntent
import com.upper.trading.presentation.base.UiState

data class OnboardingState(
    val currentSlideIndex: Int = 0,
    val isDarkTheme: Boolean = false,
    val introSlides: List<IntroSlide> = emptyList(),
): UiState

sealed interface OnboardingIntent : UiIntent {
    data class SelectTheme(val isDark: Boolean) : OnboardingIntent
    data object ConfirmTheme: OnboardingIntent
    data class UpdateSlideIntro(val page: Int) : OnboardingIntent
    data object SkipOnboardingIntro: OnboardingIntent
    data object CompleteIntro : OnboardingIntent
    data object SelectSignIn: OnboardingIntent
    data object SelectSignUp: OnboardingIntent
}

sealed interface OnboardingEffect: UiEffect {
    data object NavigateOnboardingIntro: OnboardingEffect
    data object NavigateOnboardingRegistration: OnboardingEffect
    data object NavigateHomeTrade: OnboardingEffect
    data object NavigateAuthSignIn: OnboardingEffect
    data object NavigateAuthSignUp: OnboardingEffect
}
