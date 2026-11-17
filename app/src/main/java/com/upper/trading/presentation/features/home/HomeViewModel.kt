package com.upper.trading.presentation.features.home

import androidx.lifecycle.viewModelScope
import com.upper.trading.domain.usecase.onboarding.GetDarkModeUseCase
import com.upper.trading.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getDarkModeUseCase: GetDarkModeUseCase
): BaseViewModel<MainState, Nothing, Nothing>(MainState()) {

    init {
        observerTheme()
    }

    private fun observerTheme() {
        getDarkModeUseCase().onEach { isDarkMode ->
            setState {
                copy(isDarkMode = isDarkMode)
            }
        }.launchIn(viewModelScope)
    }


    override fun onIntent(intent: Nothing) = Unit

}