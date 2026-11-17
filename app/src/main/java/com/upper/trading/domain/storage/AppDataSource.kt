package com.upper.trading.domain.storage

import kotlinx.coroutines.flow.Flow

interface AppDataSource {
    val isOnboardingCompleted: Flow<Boolean>
    suspend fun setOnboardingCompleted(isCompleted: Boolean)

    val isDarkMode: Flow<Boolean>
    suspend fun setDarkMode(isDarkMode: Boolean)
}