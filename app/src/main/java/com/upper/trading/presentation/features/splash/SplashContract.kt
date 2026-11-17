package com.upper.trading.presentation.features.splash

import com.upper.trading.presentation.base.UiEffect
import com.upper.trading.presentation.base.UiState


data class SplashState(
    val isOnboardingComplete: Boolean = false
) : UiState

sealed interface SplashEffect : UiEffect {
    data object NavigateOnboardingTheme : SplashEffect
    data object NavigateHomeTrade : SplashEffect
}