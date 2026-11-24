package com.upper.trading.domain.model

data class AuthResult(
    val accessToken: String,
    val refreshToken: String,
    val userId: String = "",
    val email: String? = null,
    val accountCompletionState: AccountCompletionState? = null
)

data class AccountCompletionState(
    val isPhoneVerified: Boolean = false,
    val hasPasscode: Boolean = false,
    val isProfileCompleted: Boolean = false,
    val isAddressCompleted: Boolean = false,
    val kycTier: String = "TIER_0_UNVERIFIED"
)

sealed interface OtpChannel {
    data object Sms : OtpChannel
    data object Email : OtpChannel
}

enum class OtpPurpose {
    SIGN_UP, RESET_PASSWORD, CHANGE_PHONE, WITHDRAWAL
}

sealed interface SignInCredentials {
    data class Email(val email: String, val password: String) : SignInCredentials
    data class Phone(val countryDialCode: String, val phone: String, val password: String) : SignInCredentials
    data class Google(val idToken: String) : SignInCredentials
    data class Apple(val idToken: String) : SignInCredentials
    data object Biometric : SignInCredentials
}

sealed interface SignUpCredentials {
    data class Email(val email: String, val password: String) : SignUpCredentials
    data class Phone(val countryDialCode: String, val phone: String, val password: String) : SignUpCredentials
}
