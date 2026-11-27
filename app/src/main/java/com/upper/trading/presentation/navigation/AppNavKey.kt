package com.upper.trading.presentation.navigation

import androidx.navigation3.runtime.NavKey
import com.upper.trading.presentation.features.auth.signin.AuthInputMode
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
    @Serializable data class SignIn(
        val isSignUp: Boolean = false,
        val showProgressBar: Boolean = true
    ): AuthNavKey

    @Serializable
    data class Otp(
        val inputMode: AuthInputMode = AuthInputMode.EMAIL,
        val email: String = "",
        val phoneNumber: String = "",
        val dialCode: Int = 0,
        val fromOnboarding: Boolean = true
    ): AuthNavKey

    @Serializable data object PinSetup: AuthNavKey
    @Serializable data object Welcome: AuthNavKey
}

@Serializable
sealed interface ProfileNavKey : AppNavKey {
    @Serializable data class AccountSetup(val isStandalone: Boolean = false) : ProfileNavKey
    @Serializable data class AccountAddress(val isStandalone: Boolean = false) : ProfileNavKey
    @Serializable data class AccountCountry(val isStandalone: Boolean = false) : ProfileNavKey
}

@Serializable
sealed interface KycNavKey : AppNavKey {
    @Serializable data class Selfie(val isStandalone: Boolean = false) : KycNavKey
    @Serializable data class DocumentScan(val isStandalone: Boolean = false) : KycNavKey
    @Serializable data class Processing(val isStandalone: Boolean = false) : KycNavKey
}

@Serializable
sealed interface SecurityNavKey : AppNavKey {
    @Serializable data class AddEmail(val isStandalone: Boolean = true) : SecurityNavKey
    @Serializable data class VerifyEmailOtp(val email: String) : SecurityNavKey
    @Serializable data class AddPhone(val isStandalone: Boolean = true) : SecurityNavKey
    @Serializable data class VerifyPhoneOtp(val phone: String) : SecurityNavKey
}

@Serializable
sealed interface CardNavKey : AppNavKey {
    @Serializable data class AddCard(val isStandalone: Boolean = false) : CardNavKey
    @Serializable data class Verify(val isStandalone: Boolean = false) : CardNavKey
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