package com.upper.trading.data.repository

import com.upper.trading.domain.model.AuthResult
import com.upper.trading.domain.model.SignInCredentials
import com.upper.trading.domain.model.SignUpCredentials
import com.upper.trading.domain.repository.AuthRepository
import kotlinx.coroutines.delay
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor() : AuthRepository {

    override suspend fun signIn(credentials: SignInCredentials): Result<AuthResult> {
        delay(1000)

        return Result.success(
            AuthResult(
                accessToken = "mock_access_token_${UUID.randomUUID()}",
                refreshToken = "mock_refresh_token_${UUID.randomUUID()}",
                userId = "usr_${System.currentTimeMillis()}"
            )
        )
    }

    override suspend fun signUp(credentials: SignUpCredentials): Result<String> {
        delay(1000)

        val destination = when (credentials) {
            is SignUpCredentials.Email -> credentials.email
            is SignUpCredentials.Phone -> "${credentials.countryDialCode}${credentials.phone}"
        }
        return Result.success(destination)
    }
}
