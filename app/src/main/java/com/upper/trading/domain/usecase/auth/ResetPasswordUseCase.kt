package com.upper.trading.domain.usecase.auth

import com.upper.trading.domain.repository.AuthRepository
import javax.inject.Inject

class ResetPasswordUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(actionToken: String, newPassword: String): Result<Unit> {
        return authRepository.resetPassword(actionToken, newPassword)
    }
}
