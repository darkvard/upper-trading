package com.upper.trading.domain.storage

import kotlinx.coroutines.flow.Flow

interface UserSessionDataStorage {
    val accessToken : Flow<String>

    suspend fun setAccessToken(token: String)

    val refreshToken: Flow<String>

    suspend fun setRefreshToken(token: String)

    val isBalanceHidden: Flow<Boolean>

    suspend fun setBalanceHidden(isHidden: Boolean)

    suspend fun clearSession()
}