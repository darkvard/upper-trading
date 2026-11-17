package com.upper.trading.presentation.features.onboarding

import androidx.lifecycle.viewModelScope
import com.upper.trading.domain.usecase.onboarding.CompleteOnboardingUseCase
import com.upper.trading.domain.usecase.onboarding.SetDarkModeUseCase
import com.upper.trading.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val completeOnboardingUseCase: CompleteOnboardingUseCase,
    private val setDarkModeUseCase: SetDarkModeUseCase
) : BaseViewModel<OnboardingState, OnboardingIntent, OnboardingEffect>(
    initialState = OnboardingState()
) {

    override fun onIntent(intent: OnboardingIntent) {
        when (intent) {
            is OnboardingIntent.SwipeSlideIntro -> {
                setState { copy(currentSlideIndex = intent.page) }
            }

            is OnboardingIntent.SelectTheme -> {
                setState { copy(isDarkTheme = intent.isDark) }
            }

            OnboardingIntent.SkipOnboarding -> {
                viewModelScope.launch {
                    setDarkModeUseCase(currentState.isDarkTheme)
                    sendEffect(OnboardingEffect.NavigateHomeTrade)
                }
            }

            OnboardingIntent.ConfirmTheme -> {
                viewModelScope.launch {
                    setDarkModeUseCase(currentState.isDarkTheme)
                    sendEffect(OnboardingEffect.NavigateOnboardingIntro)
                }
            }

            OnboardingIntent.CompleteIntro -> {
                viewModelScope.launch {
                    completeOnboardingUseCase()
                    sendEffect(OnboardingEffect.NavigateAuthSignIn)
                }
            }
        }
    }



}