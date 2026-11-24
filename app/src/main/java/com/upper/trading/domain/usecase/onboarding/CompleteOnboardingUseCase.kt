package com.upper.trading.domain.usecase.onboarding

import com.upper.trading.domain.storage.AppDataStorage
import javax.inject.Inject

class CompleteOnboardingUseCase @Inject constructor(
    private val dataStorage: AppDataStorage
) {

    suspend operator fun invoke() {
        return dataStorage.setOnboardingCompleted(true)
    }
}