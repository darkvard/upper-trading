package com.upper.trading.data.local.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.upper.trading.data.local.datastore.UserSessionDataStorageImpl.PreferencesKeys.ACCESS_TOKEN
import com.upper.trading.data.local.datastore.UserSessionDataStorageImpl.PreferencesKeys.REFRESH_TOKEN
import com.upper.trading.di.qualifier.UserSessionDataStore
import com.upper.trading.domain.storage.UserSessionDataStorage
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UserSessionDataStorageImpl @Inject constructor(
    @param:UserSessionDataStore private val dataStore: DataStore<Preferences>,
) : UserSessionDataStorage {

    override val accessToken: Flow<String>
        get() = dataStore.getPreference(ACCESS_TOKEN, "")

    override suspend fun setAccessToken(token: String) {
        dataStore.setPreference(ACCESS_TOKEN, token)
    }

    override val refreshToken: Flow<String>
        get() = dataStore.getPreference(REFRESH_TOKEN, "")

    override suspend fun setRefreshToken(token: String) {
        dataStore.setPreference(REFRESH_TOKEN, token)
    }

    override val isBalanceHidden: Flow<Boolean>
        get() = dataStore.getPreference(PreferencesKeys.BALANCE_HIDDEN, false)

    override suspend fun setBalanceHidden(isHidden: Boolean) {
        dataStore.setPreference(PreferencesKeys.BALANCE_HIDDEN, isHidden)
    }

    override suspend fun clearSession() {
        dataStore.edit { it.clear() }
    }

    private object PreferencesKeys {
        val ACCESS_TOKEN = stringPreferencesKey("access_token")
        val REFRESH_TOKEN = stringPreferencesKey("refresh_token")
        val BALANCE_HIDDEN = booleanPreferencesKey("is_balance_hidden")
    }
}