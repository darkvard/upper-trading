package com.upper.trading.data.storage


import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

internal fun <T> DataStore<Preferences>.getPreference(
    key: Preferences.Key<T>,
    defaultValue: T,
): Flow<T> = data
    .catch { exception ->
        if (exception is IOException) {
            emit(emptyPreferences())
        } else {
            throw exception
        }
    }
    .map { preferences ->
        preferences[key] ?: defaultValue
    }

internal  fun <T> DataStore<Preferences>.getPreference(
    key: Preferences.Key<T>,
): Flow<T?> = data
    .catch { exception ->
        if (exception is IOException) {
            emit(emptyPreferences())
        } else {
            throw exception
        }
    }
    .map { preferences ->
        preferences[key]
    }

internal  suspend fun <T> DataStore<Preferences>.setPreference(
    key: Preferences.Key<T>,
    value: T,
) {
    edit { preferences ->
        preferences[key] = value
    }
}