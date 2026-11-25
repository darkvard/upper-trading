package com.upper.trading.domain.repository

import com.upper.trading.domain.model.AuthResult
import com.upper.trading.domain.model.OtpChannel
import com.upper.trading.domain.model.OtpPurpose
import com.upper.trading.domain.model.SignInCredentials
import com.upper.trading.domain.model.SignUpCredentials

interface AuthRepository {
    suspend fun signIn(credentials: SignInCredentials): Result<AuthResult>
    suspend fun getBiometricChallenge(deviceId: String? = null): Result<String>
    suspend fun isBiometricEnabled(): Boolean
    suspend fun setBiometricEnabled(isEnabled: Boolean): Result<Unit>
    fun getDeviceId(): String
    suspend fun signUp(credentials: SignUpCredentials): Result<AuthResult>
    suspend fun sendOtp(channel: OtpChannel, destination: String, purpose: OtpPurpose): Result<Unit>
    suspend fun verifyOtp(channel: OtpChannel, destination: String, purpose: OtpPurpose, code: String): Result<String>
    suspend fun forgotPassword(identifier: String): Result<Unit>
    suspend fun resetPassword(actionToken: String, newPassword: String): Result<Unit>
    suspend fun signOut(): Result<Unit>
}
