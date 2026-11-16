package com.upper.trading.domain.usecase.onboarding

import com.upper.trading.domain.storage.UserPreferencesDataSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class IsOnboardingCompleteUseCase @Inject constructor(
    private val dataStorage: UserPreferencesDataSource
) {

    operator fun invoke(): Flow<Boolean> {
        return dataStorage.isOnboardingCompleted
    }
}