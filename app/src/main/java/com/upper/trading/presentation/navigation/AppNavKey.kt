package com.upper.trading.presentation.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface AppNavKey: NavKey

@Serializable
data object SplashNavKey: AppNavKey

@Serializable
sealed interface OnboardingNavKey: AppNavKey {
    @Serializable data object Intro: OnboardingNavKey
    @Serializable data object Theme: OnboardingNavKey
    @Serializable data object Registration: OnboardingNavKey
}

@Serializable
sealed interface AuthNavKey: AppNavKey {
    @Serializable data class SignIn(val isSignUp: Boolean): AuthNavKey
    @Serializable data class Otp(val destination: String): AuthNavKey
    @Serializable data object PinSetup: AuthNavKey
    @Serializable data object Welcome: AuthNavKey
}

@Serializable
sealed interface KycNavKey : AppNavKey {
    @Serializable data object AccountSetup : KycNavKey
    @Serializable data object DocumentScan : KycNavKey
    @Serializable data object Selfie : KycNavKey
    @Serializable data object Processing : KycNavKey
}

@Serializable
sealed interface CardNavKey : AppNavKey {
    @Serializable data object AddCard : CardNavKey
}

@Serializable
sealed interface MainNavKey : AppNavKey {
    @Serializable data object Analytics : MainNavKey
    @Serializable data object Wallet : MainNavKey
    @Serializable data object Trade : MainNavKey

    @Serializable
    data class TradeDetail(val symbol: String, val initialPrice: Double) : MainNavKey

    @Serializable
    data class ConfirmOrder(val symbol: String, val amount: Double, val price: Double) : MainNavKey

    @Serializable
    data object OrderSuccess : MainNavKey
}