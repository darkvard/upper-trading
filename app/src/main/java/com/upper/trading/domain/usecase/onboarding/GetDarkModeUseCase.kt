package com.upper.trading.domain.usecase.onboarding

import com.upper.trading.domain.storage.AppDataStorage
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetDarkModeUseCase @Inject constructor(
    private val dataStorage: AppDataStorage
) {
    operator fun invoke(): Flow<Boolean?> {
        return dataStorage.isDarkMode
    }

}