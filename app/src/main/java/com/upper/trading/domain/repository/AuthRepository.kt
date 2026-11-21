package com.upper.trading.domain.repository

import com.upper.trading.domain.model.AuthResult
import com.upper.trading.domain.model.SignInCredentials
import com.upper.trading.domain.model.SignUpCredentials

interface AuthRepository {
    suspend fun signIn(credentials: SignInCredentials): Result<AuthResult>
    suspend fun signUp(credentials: SignUpCredentials): Result<String>
}
