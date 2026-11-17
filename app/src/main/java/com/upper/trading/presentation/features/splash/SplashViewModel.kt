package com.upper.trading.presentation.features.splash

import androidx.lifecycle.viewModelScope
import com.upper.trading.domain.usecase.onboarding.IsOnboardingCompleteUseCase
import com.upper.trading.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val isOnboardingCompleteUseCase: IsOnboardingCompleteUseCase
) : BaseViewModel<SplashState, Nothing, SplashEffect>(SplashState()) {


    init {
        startSplashCountdown()
    }

    private fun startSplashCountdown() {
        viewModelScope.launch {
            val isCompleted = isOnboardingCompleteUseCase().first()
            setState {
                copy(isOnboardingComplete = isCompleted)
            }

            delay(1000)

            if (isCompleted) {
                sendEffect(SplashEffect.NavigateHomeTrade)
            } else {
                sendEffect(SplashEffect.NavigateOnboardingTheme)
            }

        }
    }

    override fun onIntent(intent: Nothing) {

    }

}