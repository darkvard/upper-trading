package com.upper.trading.domain.usecase.auth

import com.upper.trading.domain.model.AuthResult
import com.upper.trading.domain.model.SignUpCredentials
import com.upper.trading.domain.repository.AuthRepository
import javax.inject.Inject

class SignUpUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(credentials: SignUpCredentials): Result<AuthResult> {
        return authRepository.signUp(credentials)
    }
}
