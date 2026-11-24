package com.upper.trading.domain.usecase.auth

import com.upper.trading.domain.repository.AuthRepository
import javax.inject.Inject

class ForgotPasswordUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(identifier: String): Result<Unit> {
        return authRepository.forgotPassword(identifier)
    }
}
