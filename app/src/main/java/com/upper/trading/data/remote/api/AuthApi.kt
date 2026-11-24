package com.upper.trading.data.remote.api

import com.upper.trading.data.remote.dto.auth.AuthTokenResponseDto
import com.upper.trading.data.remote.dto.auth.ForgotPasswordRequestDto
import com.upper.trading.data.remote.dto.auth.OAuthSignInRequestDto
import com.upper.trading.data.remote.dto.auth.OtpSendRequestDto
import com.upper.trading.data.remote.dto.auth.OtpVerifyRequestDto
import com.upper.trading.data.remote.dto.auth.OtpVerifyResponseDto
import com.upper.trading.data.remote.dto.auth.RefreshTokenRequestDto
import com.upper.trading.data.remote.dto.auth.ResetPasswordRequestDto
import com.upper.trading.data.remote.dto.auth.SignInRequestDto
import com.upper.trading.data.remote.dto.auth.SignUpRequestDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface AuthApi {

    @POST("auth/sign-in")
    suspend fun signIn(@Body request: SignInRequestDto): AuthTokenResponseDto

    @POST("auth/sign-up")
    suspend fun signUp(@Body request: SignUpRequestDto): AuthTokenResponseDto

    @POST("auth/oauth")
    suspend fun oauthSignIn(@Body request: OAuthSignInRequestDto): AuthTokenResponseDto

    @POST("auth/otp/send")
    suspend fun sendOtp(@Body request: OtpSendRequestDto): Response<Unit>

    @POST("auth/otp/verify")
    suspend fun verifyOtp(@Body request: OtpVerifyRequestDto): OtpVerifyResponseDto

    @POST("auth/password/forgot")
    suspend fun forgotPassword(@Body request: ForgotPasswordRequestDto): Response<Unit>

    @POST("auth/password/reset")
    suspend fun resetPassword(@Body request: ResetPasswordRequestDto): Response<Unit>

    @POST("auth/token/refresh")
    suspend fun refreshToken(@Body request: RefreshTokenRequestDto): AuthTokenResponseDto

    @POST("auth/sign-out")
    suspend fun signOut(@Header("Authorization") bearerToken: String): Response<Unit>
}
