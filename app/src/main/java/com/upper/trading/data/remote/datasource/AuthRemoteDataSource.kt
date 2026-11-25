package com.upper.trading.data.remote.datasource

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

interface AuthRemoteDataSource {
    suspend fun signIn(request: SignInRequestDto): AuthTokenResponseDto
    suspend fun signUp(request: SignUpRequestDto): AuthTokenResponseDto
    suspend fun oauthSignIn(request: OAuthSignInRequestDto): AuthTokenResponseDto
    suspend fun getBiometricChallenge(request: BiometricChallengeRequestDto): BiometricChallengeResponseDto
    suspend fun verifyBiometric(request: BiometricVerifyRequestDto): AuthTokenResponseDto
    suspend fun sendOtp(request: OtpSendRequestDto)
    suspend fun verifyOtp(request: OtpVerifyRequestDto): OtpVerifyResponseDto
    suspend fun forgotPassword(request: ForgotPasswordRequestDto)
    suspend fun resetPassword(request: ResetPasswordRequestDto)
    suspend fun refreshToken(request: RefreshTokenRequestDto): AuthTokenResponseDto
    suspend fun signOut(token: String)
}
