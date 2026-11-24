package com.upper.trading.data.local.datasource

import com.upper.trading.domain.storage.UserSessionDataStorage
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthLocalDataSourceImpl @Inject constructor(
    private val userSessionDataStorage: UserSessionDataStorage
) : AuthLocalDataSource {

    override val accessToken: Flow<String> = userSessionDataStorage.accessToken
    override val refreshToken: Flow<String> = userSessionDataStorage.refreshToken

    override suspend fun saveTokens(accessToken: String, refreshToken: String) {
        userSessionDataStorage.setAccessToken(accessToken)
        userSessionDataStorage.setRefreshToken(refreshToken)
    }

    override suspend fun clearSession() {
        userSessionDataStorage.clearSession()
    }
}
