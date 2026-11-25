package com.upper.trading.data.remote.datasource

import com.upper.trading.core.network.util.safeApiCall
import com.upper.trading.data.remote.api.AuthApi
import com.upper.trading.data.remote.dto.auth.AuthTokenResponseDto
import com.upper.trading.data.remote.dto.auth.BiometricChallengeRequestDto
import com.upper.trading.data.remote.dto.auth.BiometricChallengeResponseDto
import com.upper.trading.data.remote.dto.auth.BiometricVerifyRequestDto
import com.upper.trading.data.remote.dto.auth.ForgotPasswordRequestDto
import com.upper.trading.data.remote.dto.auth.OAuthSignInRequestDto
import com.upper.trading.data.remote.dto.auth.OtpSendRequestDto
import com.upper.trading.data.remote.dto.auth.OtpVerifyRequestDto
import com.upper.trading.data.remote.dto.auth.OtpVerifyResponseDto
import com.upper.trading.data.remote.dto.auth.RefreshTokenRequestDto
import com.upper.trading.data.remote.dto.auth.ResetPasswordRequestDto
import com.upper.trading.data.remote.dto.auth.SignInRequestDto
import com.upper.trading.data.remote.dto.auth.SignUpRequestDto
import kotlinx.serialization.json.Json
import retrofit2.HttpException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRemoteDataSourceImpl @Inject constructor(
    private val authApi: AuthApi,
    private val json: Json
) : AuthRemoteDataSource {

    override suspend fun signIn(request: SignInRequestDto): AuthTokenResponseDto = safeApiCall(json) {
        authApi.signIn(request)
    }

    override suspend fun signUp(request: SignUpRequestDto): AuthTokenResponseDto = safeApiCall(json) {
        authApi.signUp(request)
    }

    override suspend fun oauthSignIn(request: OAuthSignInRequestDto): AuthTokenResponseDto = safeApiCall(json) {
        authApi.oauthSignIn(request)
    }

    override suspend fun getBiometricChallenge(request: BiometricChallengeRequestDto): BiometricChallengeResponseDto = safeApiCall(json) {
        authApi.getBiometricChallenge(request)
    }

    override suspend fun verifyBiometric(request: BiometricVerifyRequestDto): AuthTokenResponseDto = safeApiCall(json) {
        authApi.verifyBiometric(request)
    }

    override suspend fun sendOtp(request: OtpSendRequestDto): Unit = safeApiCall(json) {
        val res = authApi.sendOtp(request)
        if (!res.isSuccessful) {
            throw HttpException(res)
        }
    }

    override suspend fun verifyOtp(request: OtpVerifyRequestDto): OtpVerifyResponseDto = safeApiCall(json) {
        authApi.verifyOtp(request)
    }

    override suspend fun forgotPassword(request: ForgotPasswordRequestDto): Unit = safeApiCall(json) {
        val res = authApi.forgotPassword(request)
        if (!res.isSuccessful) {
            throw HttpException(res)
        }
    }

    override suspend fun resetPassword(request: ResetPasswordRequestDto): Unit = safeApiCall(json) {
        val res = authApi.resetPassword(request)
        if (!res.isSuccessful) {
            throw HttpException(res)
        }
    }

    override suspend fun refreshToken(request: RefreshTokenRequestDto): AuthTokenResponseDto = safeApiCall(json) {
        authApi.refreshToken(request)
    }

    override suspend fun signOut(token: String): Unit = safeApiCall(json) {
        val res = authApi.signOut("Bearer $token")
        if (!res.isSuccessful) {
            throw HttpException(res)
        }
    }
}
