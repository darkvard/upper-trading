package com.upper.trading.presentation.features.onboarding

import androidx.lifecycle.viewModelScope
import com.upper.trading.domain.usecase.onboarding.CompleteOnboardingUseCase
import com.upper.trading.domain.usecase.onboarding.GetIntroSlidesUseCase
import com.upper.trading.domain.usecase.onboarding.SetDarkModeUseCase
import com.upper.trading.presentation.base.BaseViewModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = OnboardingViewModel.Factory::class)
class OnboardingViewModel @AssistedInject constructor(
    @Assisted private val isSystemDarkTheme: Boolean,
    private val completeOnboardingUseCase: CompleteOnboardingUseCase,
    private val setDarkModeUseCase: SetDarkModeUseCase,
    private val getIntroSlidesUseCase: GetIntroSlidesUseCase,
) : BaseViewModel<OnboardingState, OnboardingIntent, OnboardingEffect>(
    initialState = OnboardingState(isDarkTheme = isSystemDarkTheme)
) {
    @AssistedFactory
    interface Factory {
        fun create(isSystemDarkTheme: Boolean = false): OnboardingViewModel
    }

    init {
        loadIntroSlides()
    }

    private fun loadIntroSlides() {
        setState { copy(introSlides = getIntroSlidesUseCase().toPersistentList()) }
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
