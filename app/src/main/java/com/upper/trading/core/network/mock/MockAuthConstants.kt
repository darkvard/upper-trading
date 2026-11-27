package com.upper.trading.core.network.mock

/**
 * Constants used exclusively for mock contracts, sample accounts, and simulated error scenarios.
 */
object MockAuthConstants {

    // Sample Tokens
    const val SAMPLE_ACCESS_TOKEN = "mock_jwt_access_token_superapp_dev"
    const val SAMPLE_REFRESH_TOKEN = "mock_jwt_refresh_token_superapp_dev"
    const val SAMPLE_PENDING_ACCESS_TOKEN = "mock_jwt_access_token_pending_otp"
    const val SAMPLE_PENDING_REFRESH_TOKEN = "mock_jwt_refresh_token_pending_otp"
    const val SAMPLE_ACTION_TOKEN = "act_mock_token_9999"

    // Error Triggers (inputting these triggers simulated HTTP error responses from OpenAPI spec)
    const val TRIGGER_ERROR_EMAIL = "invalid@error.com"
    const val TRIGGER_ERROR_PASSWORD = "WrongPass"
    const val TRIGGER_ERROR_OTP = "000000"

    // Valid Test Data
    const val SAMPLE_VALID_OTP = "452189"
    const val DEV_BYPASS_OTP = "123456"
    const val SAMPLE_BIOMETRIC_NONCE = "9f86d081884c7d659a2feaa0c55ad015"

    // Dynamic Mock OTP for Notification & Verification Matching
    var currentActiveOtp: String = "849201"

    fun generateNewOtp(): String {
        currentActiveOtp = (100000..999999).random().toString()
        return currentActiveOtp
    }

    // Error Codes & Messages defined in OpenAPI 3.0 Contract
    const val CODE_INVALID_CREDENTIALS = "INVALID_CREDENTIALS"
    const val MSG_INVALID_CREDENTIALS = "Invalid username or password"

    const val CODE_INVALID_OTP = "INVALID_OTP"
    const val MSG_INVALID_OTP = "The OTP code entered is incorrect or has expired."
}
