package com.upper.trading.data.repository

import com.upper.trading.data.local.datasource.AuthLocalDataSource
import com.upper.trading.data.remote.datasource.AuthRemoteDataSource
import com.upper.trading.data.remote.dto.auth.AuthTokenResponseDto
import com.upper.trading.data.remote.dto.auth.ForgotPasswordRequestDto
import com.upper.trading.data.remote.dto.auth.OAuthSignInRequestDto
import com.upper.trading.data.remote.dto.auth.OtpSendRequestDto
import com.upper.trading.data.remote.dto.auth.OtpVerifyRequestDto
import com.upper.trading.data.remote.dto.auth.ResetPasswordRequestDto
import com.upper.trading.data.remote.dto.auth.SignInRequestDto
import com.upper.trading.data.remote.dto.auth.SignUpRequestDto
import com.upper.trading.domain.model.AccountCompletionState
import com.upper.trading.domain.model.AuthResult
import com.upper.trading.domain.model.OtpChannel
import com.upper.trading.domain.model.OtpPurpose
import com.upper.trading.domain.model.SignInCredentials
import com.upper.trading.domain.model.SignUpCredentials
import com.upper.trading.domain.repository.AuthRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val remoteDataSource: AuthRemoteDataSource,
    private val localDataSource: AuthLocalDataSource
) : AuthRepository {

    override suspend fun signIn(credentials: SignInCredentials): Result<AuthResult> = runCatching {
        val response = when (credentials) {
            is SignInCredentials.Email -> remoteDataSource.signIn(
                SignInRequestDto(
                    loginType = "EMAIL",
                    identifier = credentials.email,
                    password = credentials.password,
                    deviceId = "android-device-mock"
                )
            )
            is SignInCredentials.Phone -> remoteDataSource.signIn(
                SignInRequestDto(
                    loginType = "PHONE",
                    identifier = "${credentials.countryDialCode}${credentials.phone}",
                    password = credentials.password,
                    deviceId = "android-device-mock"
                )
            )
            is SignInCredentials.Google -> remoteDataSource.oauthSignIn(
                OAuthSignInRequestDto(
                    provider = "GOOGLE",
                    idToken = credentials.idToken,
                    deviceId = "android-device-mock"
                )
            )
            is SignInCredentials.Apple -> remoteDataSource.oauthSignIn(
                OAuthSignInRequestDto(
                    provider = "APPLE",
                    idToken = credentials.idToken,
                    deviceId = "android-device-mock"
                )
            )
            SignInCredentials.Biometric -> error("Biometric challenge required")
        }

        saveSessionAndMap(response)
    }

    override suspend fun signUp(credentials: SignUpCredentials): Result<AuthResult> = runCatching {
        val request = when (credentials) {
            is SignUpCredentials.Email -> SignUpRequestDto(
                loginType = "EMAIL",
                identifier = credentials.email,
                password = credentials.password
            )
            is SignUpCredentials.Phone -> SignUpRequestDto(
                loginType = "PHONE",
                identifier = "${credentials.countryDialCode}${credentials.phone}",
                password = credentials.password
            )
        }
        val response = remoteDataSource.signUp(request)
        saveSessionAndMap(response)
    }

    override suspend fun sendOtp(channel: OtpChannel, destination: String, purpose: OtpPurpose): Result<Unit> = runCatching {
        remoteDataSource.sendOtp(
            OtpSendRequestDto(
                channel = if (channel is OtpChannel.Sms) "SMS" else "EMAIL",
                destination = destination,
                purpose = purpose.name
            )
        )
    }

    override suspend fun verifyOtp(channel: OtpChannel, destination: String, purpose: OtpPurpose, code: String): Result<String> = runCatching {
        val res = remoteDataSource.verifyOtp(
            OtpVerifyRequestDto(
                channel = if (channel is OtpChannel.Sms) "SMS" else "EMAIL",
                destination = destination,
                purpose = purpose.name,
                otpCode = code
            )
        )
        res.actionToken.orEmpty()
    }

    override suspend fun forgotPassword(identifier: String): Result<Unit> = runCatching {
        remoteDataSource.forgotPassword(ForgotPasswordRequestDto(identifier))
    }

    override suspend fun resetPassword(actionToken: String, newPassword: String): Result<Unit> = runCatching {
        remoteDataSource.resetPassword(ResetPasswordRequestDto(actionToken, newPassword))
    }

    override suspend fun signOut(): Result<Unit> = runCatching {
        val token = localDataSource.accessToken.first()
        if (token.isNotEmpty()) {
            runCatching { remoteDataSource.signOut(token) }
        }
        localDataSource.clearSession()
    }

    private suspend fun saveSessionAndMap(response: AuthTokenResponseDto): AuthResult {
        localDataSource.saveTokens(response.accessToken, response.refreshToken)
        return AuthResult(
            accessToken = response.accessToken,
            refreshToken = response.refreshToken,
            accountCompletionState = response.accountCompletionState?.let {
                AccountCompletionState(
                    isPhoneVerified = it.isPhoneVerified,
                    hasPasscode = it.hasPasscode,
                    isProfileCompleted = it.isProfileCompleted,
                    isAddressCompleted = it.isAddressCompleted,
                    kycTier = it.kycTier
                )
            }
        )
    }
}
