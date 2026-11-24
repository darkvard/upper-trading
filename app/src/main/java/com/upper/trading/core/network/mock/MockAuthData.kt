package com.upper.trading.core.network.mock

/**
 * Mock JSON payloads matching OpenAPI 3.0 Contract schemas.
 * Uses [MockAuthConstants] to avoid any hardcoded strings.
 */
object MockAuthData {

    val SIGN_IN_SUCCESS_JSON: String = """
    {
        "access_token": "${MockAuthConstants.SAMPLE_ACCESS_TOKEN}",
        "refresh_token": "${MockAuthConstants.SAMPLE_REFRESH_TOKEN}",
        "expires_in": 3600,
        "account_completion_state": {
            "is_phone_verified": true,
            "has_passcode": false,
            "is_profile_completed": false,
            "is_address_completed": false,
            "kyc_tier": "TIER_0_UNVERIFIED"
        }
    }
    """.trimIndent()

    val SIGN_UP_SUCCESS_JSON: String = """
    {
        "access_token": "${MockAuthConstants.SAMPLE_PENDING_ACCESS_TOKEN}",
        "refresh_token": "${MockAuthConstants.SAMPLE_PENDING_REFRESH_TOKEN}",
        "expires_in": 3600,
        "account_completion_state": {
            "is_phone_verified": false,
            "has_passcode": false,
            "is_profile_completed": false,
            "is_address_completed": false,
            "kyc_tier": "TIER_0_UNVERIFIED"
        }
    }
    """.trimIndent()

    val ERROR_CREDENTIALS_JSON: String = """
    {
        "code": "${MockAuthConstants.CODE_INVALID_CREDENTIALS}",
        "message": "${MockAuthConstants.MSG_INVALID_CREDENTIALS}",
        "trace_id": "trace-mock-401"
    }
    """.trimIndent()

    val ERROR_OTP_JSON: String = """
    {
        "code": "${MockAuthConstants.CODE_INVALID_OTP}",
        "message": "${MockAuthConstants.MSG_INVALID_OTP}",
        "trace_id": "trace-otp-400"
    }
    """.trimIndent()

    val OTP_VERIFY_SUCCESS_JSON: String = """
    {
        "verified": true,
        "action_token": "${MockAuthConstants.SAMPLE_ACTION_TOKEN}"
    }
    """.trimIndent()

    val BIOMETRIC_CHALLENGE_JSON: String = """
    {
        "challenge_nonce": "${MockAuthConstants.SAMPLE_BIOMETRIC_NONCE}",
        "expires_in_seconds": 60
    }
    """.trimIndent()
}
