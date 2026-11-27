package com.upper.trading.domain.storage

import kotlinx.coroutines.flow.Flow

interface AppDataStorage {
    val isOnboardingCompleted: Flow<Boolean>
    suspend fun setOnboardingCompleted(isCompleted: Boolean)

    val isDarkMode: Flow<Boolean?>
    suspend fun setDarkMode(isDarkMode: Boolean)

    val hasDeniedNotificationPermission: Flow<Boolean>
    suspend fun setNotificationPermissionDenied(denied: Boolean)
}