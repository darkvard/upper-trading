package com.upper.trading.presentation.features.onboarding

import androidx.lifecycle.viewModelScope
import com.upper.trading.domain.usecase.onboarding.CompleteOnboardingUseCase
import com.upper.trading.domain.usecase.onboarding.GetDarkModeUseCase
import com.upper.trading.domain.usecase.onboarding.GetIntroSlidesUseCase
import com.upper.trading.domain.usecase.onboarding.SetDarkModeUseCase
import com.upper.trading.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val completeOnboardingUseCase: CompleteOnboardingUseCase,
    private val setDarkModeUseCase: SetDarkModeUseCase,
    private val getDarkModeUseCase: GetDarkModeUseCase,
    private val getIntroSlidesUseCase: GetIntroSlidesUseCase,
) : BaseViewModel<OnboardingState, OnboardingIntent, OnboardingEffect>(
    initialState = OnboardingState()
) {

    init {
        loadIntroSlides()
        observeTheme()
    }

    private fun loadIntroSlides() {
        setState { copy(introSlides = getIntroSlidesUseCase().toPersistentList()) }
    }

    private fun observeTheme() {
        getDarkModeUseCase().onEach { isDark ->
            setState { copy(isDarkTheme = isDark) }
        }.launchIn(viewModelScope)
    }

    override fun onIntent(intent: OnboardingIntent) {
        when (intent) {
            is OnboardingIntent.UpdateSlideIntro -> {
                setState { copy(currentSlideIndex = intent.page) }
            }

            is OnboardingIntent.SelectTheme -> {
                setState { copy(isDarkTheme = intent.isDark) }
            }

            OnboardingIntent.SkipOnboardingIntro -> {
                viewModelScope.launch {
                    setDarkModeUseCase(currentState.isDarkTheme)
                    completeOnboardingUseCase()
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
                sendEffect(OnboardingEffect.NavigateOnboardingRegistration)
            }

            OnboardingIntent.SelectSignIn -> {
                viewModelScope.launch {
                    completeOnboardingUseCase()
                    sendEffect(OnboardingEffect.NavigateAuthSignIn)
                }
            }

            OnboardingIntent.SelectSignUp -> {
                viewModelScope.launch {
                    completeOnboardingUseCase()
                    sendEffect(OnboardingEffect.NavigateAuthSignUp)
                }
            }
        }
    }
}
