package com.upper.trading.domain.usecase.onboarding

import com.upper.trading.domain.storage.UserPreferencesDataSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CompleteOnboardingUseCase @Inject constructor(
    private val dataStorage: UserPreferencesDataSource
) {

    suspend operator fun invoke() {
        return dataStorage.setOnboardingCompleted(true)
    }
}