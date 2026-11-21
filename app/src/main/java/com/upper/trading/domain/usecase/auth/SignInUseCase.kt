package com.upper.trading.domain.usecase.auth

import com.upper.trading.domain.model.AuthResult
import com.upper.trading.domain.model.SignInCredentials
import com.upper.trading.domain.repository.AuthRepository
import com.upper.trading.domain.storage.UserSessionDataStorage
import javax.inject.Inject

class SignInUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val userSessionDataStorage: UserSessionDataStorage
) {
    suspend operator fun invoke(credentials: SignInCredentials): Result<AuthResult> {
        return authRepository.signIn(credentials).onSuccess { authResult ->
            userSessionDataStorage.setAccessToken(authResult.accessToken)
            userSessionDataStorage.setRefreshToken(authResult.refreshToken)
        }
    }
}
