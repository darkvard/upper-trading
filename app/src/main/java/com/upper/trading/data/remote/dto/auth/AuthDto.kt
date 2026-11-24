package com.upper.trading.data.remote.dto.auth

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SignInRequestDto(
    @SerialName("login_type") val loginType: String,
    @SerialName("identifier") val identifier: String,
    @SerialName("password") val password: String,
    @SerialName("device_id") val deviceId: String,
    @SerialName("device_name") val deviceName: String? = null
)

@Serializable
data class SignUpRequestDto(
    @SerialName("login_type") val loginType: String,
    @SerialName("identifier") val identifier: String,
    @SerialName("password") val password: String
)

@Serializable
data class OAuthSignInRequestDto(
    @SerialName("provider") val provider: String,
    @SerialName("id_token") val idToken: String,
    @SerialName("device_id") val deviceId: String
)

@Serializable
data class AuthTokenResponseDto(
    @SerialName("access_token") val accessToken: String,
    @SerialName("refresh_token") val refreshToken: String,
    @SerialName("expires_in") val expiresIn: Int = 3600,
    @SerialName("account_completion_state") val accountCompletionState: AccountCompletionStateDto? = null
)

@Serializable
data class AccountCompletionStateDto(
    @SerialName("is_phone_verified") val isPhoneVerified: Boolean = false,
    @SerialName("has_passcode") val hasPasscode: Boolean = false,
    @SerialName("is_profile_completed") val isProfileCompleted: Boolean = false,
    @SerialName("is_address_completed") val isAddressCompleted: Boolean = false,
    @SerialName("kyc_tier") val kycTier: String = "TIER_0_UNVERIFIED"
)

@Serializable
data class OtpSendRequestDto(
    @SerialName("channel") val channel: String,
    @SerialName("destination") val destination: String,
    @SerialName("purpose") val purpose: String
)

@Serializable
data class OtpVerifyRequestDto(
    @SerialName("channel") val channel: String,
    @SerialName("destination") val destination: String,
    @SerialName("purpose") val purpose: String,
    @SerialName("otp_code") val otpCode: String
)

@Serializable
data class OtpVerifyResponseDto(
    @SerialName("verified") val verified: Boolean,
    @SerialName("action_token") val actionToken: String? = null
)

@Serializable
data class ForgotPasswordRequestDto(
    @SerialName("identifier") val identifier: String
)

@Serializable
data class ResetPasswordRequestDto(
    @SerialName("action_token") val actionToken: String,
    @SerialName("new_password") val newPassword: String
)

@Serializable
data class RefreshTokenRequestDto(
    @SerialName("refresh_token") val refreshToken: String
)
