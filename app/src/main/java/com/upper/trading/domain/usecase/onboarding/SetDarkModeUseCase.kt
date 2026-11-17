package com.upper.trading.domain.usecase.onboarding

import com.upper.trading.domain.storage.AppDataSource
import javax.inject.Inject

class SetDarkModeUseCase  @Inject constructor(
    private val dataStorage: AppDataSource
) {

    suspend operator fun invoke(isDarkMode: Boolean) {
        dataStorage.setDarkMode(isDarkMode)
    }
}