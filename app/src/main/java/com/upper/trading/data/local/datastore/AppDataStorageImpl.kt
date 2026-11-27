package com.upper.trading.data.local.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import com.upper.trading.di.qualifier.AppDataStore
import com.upper.trading.di.qualifier.IoDispatcher
import com.upper.trading.domain.storage.AppDataStorage
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AppDataStorageImpl @Inject constructor(
    @param:AppDataStore private val dataStore: DataStore<Preferences>,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : AppDataStorage {

    override val isOnboardingCompleted: Flow<Boolean>
        get() = dataStore.getPreference(PreferencesKeys.ONBOARDING_COMPLETED, false).flowOn(ioDispatcher)

    override suspend fun setOnboardingCompleted(isCompleted: Boolean) = withContext(ioDispatcher) {
        dataStore.setPreference(PreferencesKeys.ONBOARDING_COMPLETED, isCompleted)
    }

    override val isDarkMode: Flow<Boolean?>
        get() = dataStore.getPreference(PreferencesKeys.THEME_DARK_MODE).flowOn(ioDispatcher)

    override suspend fun setDarkMode(isDarkMode: Boolean) = withContext(ioDispatcher) {
        dataStore.setPreference(PreferencesKeys.THEME_DARK_MODE, isDarkMode)
    }

    override val hasDeniedNotificationPermission: Flow<Boolean>
        get() = dataStore.getPreference(PreferencesKeys.NOTIFICATION_PERMISSION_DENIED, false).flowOn(ioDispatcher)

    override suspend fun setNotificationPermissionDenied(denied: Boolean) = withContext(ioDispatcher) {
        dataStore.setPreference(PreferencesKeys.NOTIFICATION_PERMISSION_DENIED, denied)
    }

    private object PreferencesKeys {
        val ONBOARDING_COMPLETED = booleanPreferencesKey("is_onboarding_completed")

        val THEME_DARK_MODE = booleanPreferencesKey("is_dark_mode")

        val NOTIFICATION_PERMISSION_DENIED = booleanPreferencesKey("has_denied_notification_permission")
    }
}