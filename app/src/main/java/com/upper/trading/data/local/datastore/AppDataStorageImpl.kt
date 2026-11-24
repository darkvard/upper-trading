package com.upper.trading.data.local.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import com.upper.trading.di.qualifier.AppDataStore
import com.upper.trading.domain.storage.AppDataStorage
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AppDataStorageImpl @Inject constructor(
    @param:AppDataStore private val dataStore: DataStore<Preferences>,
) : AppDataStorage {

    override val isOnboardingCompleted: Flow<Boolean>
        get() = dataStore.getPreference(PreferencesKeys.ONBOARDING_COMPLETED, false)

    override suspend fun setOnboardingCompleted(isCompleted: Boolean) {
        dataStore.setPreference(PreferencesKeys.ONBOARDING_COMPLETED, isCompleted)
    }

    override val isDarkMode: Flow<Boolean?>
        get() = dataStore.getPreference(PreferencesKeys.THEME_DARK_MODE)

    override suspend fun setDarkMode(isDarkMode: Boolean) {
        dataStore.setPreference(PreferencesKeys.THEME_DARK_MODE, isDarkMode)
    }

    private object PreferencesKeys {
        val ONBOARDING_COMPLETED = booleanPreferencesKey("is_onboarding_completed")

        val THEME_DARK_MODE = booleanPreferencesKey("is_dark_mode")
    }


}