package com.upper.trading.presentation.navigation

import com.upper.trading.presentation.components.layout.ProgressBarState

/**
 * Defines the standard 10-step registration and account onboarding flow:
 *
 * 1. [SIGN_UP]: Account Registration (Sign Up / Sign In) -> Step 1/10
 * 2. [OTP]: OTP Verification -> Step 2/10
 * 3. [ACCOUNT_PERSONAL]: Account Setup - Personal Information (Name, Username, DOB) -> Step 3/10
 * 4. [ACCOUNT_ADDRESS]: Account Setup - Residential Address (Address Line, City, Zip code) -> Step 4/10
 * 5. [ACCOUNT_COUNTRY]: Account Setup - Country of Residence -> Step 5/10
 * 6. [KYC_SELFIE]: Identity Verification - Portrait Capture (Selfie/Camera) -> Step 6/10
 * 7. [KYC_DOCUMENT]: Identity Verification - Identity Document Scan (ID/Passport) -> Step 7/10
 * 8. [KYC_PROCESSING]: Identity Verification - Document Processing & Review -> Step 8/10
 * 9. [CARD_INFO]: Payment Card - Enter Card Details -> Step 9/10
 * 10. [CARD_VERIFY]: Payment Card - Card Verification & Setup Completion -> Step 10/10
 */
enum class RegistrationFlowStep(val step: Float, val total: Float = 10f) {
    SIGN_UP(1f),
    OTP(2f),
    ACCOUNT_PERSONAL(3f),
    ACCOUNT_ADDRESS(4f),
    ACCOUNT_COUNTRY(5f),
    KYC_SELFIE(6f),
    KYC_DOCUMENT(7f),
    KYC_PROCESSING(8f),
    CARD_INFO(9f),
    CARD_VERIFY(10f);

    fun toProgressBarState() = ProgressBarState(current = step, total = total)
}

/**
 * Defines the standalone 3-step Account Setup flow (e.g. from User Profile):
 * (Email is skipped because it was already verified during registration)
 *
 * 1. [PERSONAL_INFO]: Personal Information (Name, Username, DOB) -> Step 1/3
 * 2. [HOME_ADDRESS]: Residential Address (Address Line, City, Postcode) -> Step 2/3
 * 3. [COUNTRY]: Country of Residence -> Step 3/3
 */
enum class StandaloneAccountSetupFlowStep(val step: Float, val total: Float = 3f) {
    PERSONAL_INFO(1f),
    HOME_ADDRESS(2f),
    COUNTRY(3f);

    fun toProgressBarState() = ProgressBarState(current = step, total = total)
}

/**
 * Defines the standalone 2-step Email Verification flow (e.g. Add/Change Email from Profile/Security):
 *
 * 1. [EMAIL]: Enter email address -> Step 1/2
 * 2. [OTP]: Verify OTP code sent to email -> Step 2/2
 */
enum class VerifyEmailFlowStep(val step: Float, val total: Float = 2f) {
    EMAIL(1f),
    OTP(2f);

    fun toProgressBarState() = ProgressBarState(current = step, total = total)
}

/**
 * Defines the standalone 2-step Phone Verification flow (e.g. Add/Change Phone from Profile/Security):
 *
 * 1. [PHONE]: Enter phone number -> Step 1/2
 * 2. [OTP]: Verify OTP code sent to phone -> Step 2/2
 */
enum class VerifyPhoneFlowStep(val step: Float, val total: Float = 2f) {
    PHONE(1f),
    OTP(2f);

    fun toProgressBarState() = ProgressBarState(current = step, total = total)
}

/**
 * Defines the standalone 3-step KYC verification flow (e.g. from User Profile):
 *
 * 1. [SELFIE]: Identity Verification - Portrait Capture (Selfie) -> Step 1/3
 * 2. [DOCUMENT]: Identity Verification - Identity Document Scan -> Step 2/3
 * 3. [PROCESSING]: Identity Verification - Processing & Review -> Step 3/3
 */
enum class StandaloneKycFlowStep(val step: Float, val total: Float = 3f) {
    SELFIE(1f),
    DOCUMENT(2f),
    PROCESSING(3f);

    fun toProgressBarState() = ProgressBarState(current = step, total = total)
}

/**
 * Defines the standalone 2-step Payment Card management flow (e.g. from Wallet / Profile):
 *
 * 1. [CARD_INFO]: Payment Card - Enter Card Details -> Step 1/2
 * 2. [CARD_VERIFY]: Payment Card - Card Verification & Completion -> Step 2/2
 */
enum class StandaloneCardFlowStep(val step: Float, val total: Float = 2f) {
    CARD_INFO(1f),
    CARD_VERIFY(2f);

    fun toProgressBarState() = ProgressBarState(current = step, total = total)
}
