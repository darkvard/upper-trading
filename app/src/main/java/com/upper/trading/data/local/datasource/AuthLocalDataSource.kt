package com.upper.trading.data.local.datasource

import kotlinx.coroutines.flow.Flow

interface AuthLocalDataSource {
    val accessToken: Flow<String>
    val refreshToken: Flow<String>
    suspend fun saveTokens(accessToken: String, refreshToken: String)
    suspend fun clearSession()
}
