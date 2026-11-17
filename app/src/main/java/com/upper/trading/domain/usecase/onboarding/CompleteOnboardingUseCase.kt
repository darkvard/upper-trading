package com.upper.trading.domain.usecase.onboarding

import com.upper.trading.domain.storage.AppDataSource
import javax.inject.Inject

class CompleteOnboardingUseCase @Inject constructor(
    private val dataStorage: AppDataSource
) {

    suspend operator fun invoke() {
        return dataStorage.setOnboardingCompleted(true)
    }
}