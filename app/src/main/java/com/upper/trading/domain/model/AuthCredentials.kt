package com.upper.trading.domain.model

data class AuthResult(
    val accessToken: String,
    val refreshToken: String,
    val userId: String = "",
    val email: String? = null
)

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
