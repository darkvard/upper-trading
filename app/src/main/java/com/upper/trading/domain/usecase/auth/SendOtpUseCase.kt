package com.upper.trading.domain.usecase.auth

import com.upper.trading.domain.model.OtpChannel
import com.upper.trading.domain.model.OtpPurpose
import com.upper.trading.domain.repository.AuthRepository
import javax.inject.Inject

class SendOtpUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(
        channel: OtpChannel,
        destination: String,
        purpose: OtpPurpose
    ): Result<Unit> {
        return authRepository.sendOtp(channel, destination, purpose)
    }
}
