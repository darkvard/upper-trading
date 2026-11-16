package com.upper.trading.domain.storage

import kotlinx.coroutines.flow.Flow

interface UserPreferencesDataSource {
    val isOnboardingCompleted: Flow<Boolean>

    suspend fun setOnboardingCompleted(isCompleted: Boolean)
}