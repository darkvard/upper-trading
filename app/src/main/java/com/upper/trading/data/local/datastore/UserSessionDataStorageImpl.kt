package com.upper.trading.data.local.datastore

import android.content.Context
import android.provider.Settings
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.upper.trading.data.local.datastore.UserSessionDataStorageImpl.PreferencesKeys.ACCESS_TOKEN
import com.upper.trading.data.local.datastore.UserSessionDataStorageImpl.PreferencesKeys.REFRESH_TOKEN
import com.upper.trading.di.qualifier.IoDispatcher
import com.upper.trading.di.qualifier.UserSessionDataStore
import com.upper.trading.domain.storage.UserSessionDataStorage
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import javax.inject.Inject

class UserSessionDataStorageImpl @Inject constructor(
    @param:UserSessionDataStore private val dataStore: DataStore<Preferences>,
    @param:ApplicationContext private val context: Context,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : UserSessionDataStorage {

    override val accessToken: Flow<String>
        get() = dataStore.getPreference(ACCESS_TOKEN, "").flowOn(ioDispatcher)

    override suspend fun setAccessToken(token: String) = withContext(ioDispatcher) {
        dataStore.setPreference(ACCESS_TOKEN, token)
    }

    override val refreshToken: Flow<String>
        get() = dataStore.getPreference(REFRESH_TOKEN, "").flowOn(ioDispatcher)

    override suspend fun setRefreshToken(token: String) = withContext(ioDispatcher) {
        dataStore.setPreference(REFRESH_TOKEN, token)
    }

    override val isBalanceHidden: Flow<Boolean>
        get() = dataStore.getPreference(PreferencesKeys.BALANCE_HIDDEN, false).flowOn(ioDispatcher)

    override suspend fun setBalanceHidden(isHidden: Boolean) = withContext(ioDispatcher) {
        dataStore.setPreference(PreferencesKeys.BALANCE_HIDDEN, isHidden)
    }

    override fun getDeviceId(): String {
        return Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID).orEmpty()
    }

    override val isBiometricEnabled: Flow<Boolean>
        get() = dataStore.getPreference(PreferencesKeys.BIOMETRIC_ENABLED, false).flowOn(ioDispatcher)

    override suspend fun setBiometricEnabled(isEnabled: Boolean) = withContext(ioDispatcher) {
        dataStore.setPreference(PreferencesKeys.BIOMETRIC_ENABLED, isEnabled)
    }

    override suspend fun clearSession() {
        withContext(ioDispatcher) {
            dataStore.edit { preferences ->
                preferences.remove(ACCESS_TOKEN)
                preferences.remove(REFRESH_TOKEN)
            }
        }
    }

    private object PreferencesKeys {
        val ACCESS_TOKEN = stringPreferencesKey("access_token")
        val REFRESH_TOKEN = stringPreferencesKey("refresh_token")
        val BALANCE_HIDDEN = booleanPreferencesKey("is_balance_hidden")
        val BIOMETRIC_ENABLED = booleanPreferencesKey("is_biometric_enabled")
    }
}