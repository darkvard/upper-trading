package com.upper.trading.domain.usecase.onboarding

import com.upper.trading.domain.storage.AppDataSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetDarkModeUseCase @Inject constructor(
    private val dataStorage: AppDataSource
) {
    operator fun invoke(): Flow<Boolean> {
        return dataStorage.isDarkMode
    }

}