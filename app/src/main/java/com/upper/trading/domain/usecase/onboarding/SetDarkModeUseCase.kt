package com.upper.trading.domain.usecase.onboarding

import com.upper.trading.domain.storage.AppDataStorage
import javax.inject.Inject

class SetDarkModeUseCase  @Inject constructor(
    private val dataStorage: AppDataStorage
) {

    suspend operator fun invoke(isDarkMode: Boolean) {
        dataStorage.setDarkMode(isDarkMode)
    }
}