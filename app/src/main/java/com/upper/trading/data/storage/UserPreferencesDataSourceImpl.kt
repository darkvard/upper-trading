package com.upper.trading.data.storage

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import com.upper.trading.domain.storage.UserPreferencesDataSource
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

class UserPreferencesDataSourceImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) : UserPreferencesDataSource {

    override val isOnboardingCompleted: Flow<Boolean>
        get() = dataStore.data
            .catch { handleException(it) }
            .map { it[PreferencesKeys.ONBOARDING_COMPLETED] ?: false }

    override suspend fun setOnboardingCompleted(isCompleted: Boolean) {
        dataStore.edit {
            it[PreferencesKeys.ONBOARDING_COMPLETED] = isCompleted
        }
    }

    private object PreferencesKeys {
        val ONBOARDING_COMPLETED = booleanPreferencesKey("is_onboarding_completed")
    }

    private suspend fun FlowCollector<Preferences>.handleException(exception: Throwable) {
        if (exception is IOException) {
            emit(emptyPreferences())
        } else {
            throw exception
        }
    }
}