package com.upper.trading.core.network.engine

import com.upper.trading.core.network.mock.MockAuthConstants
import com.upper.trading.core.network.mock.MockAuthData
import okhttp3.Request
import okio.Buffer
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Dispatcher that maps API endpoints to simulated responses based on [MockAuthData].
 * Engine dependency — no 'Mock' prefix as per naming rules.
 */
@Singleton
class AuthSimulationDispatcher @Inject constructor() {

    fun dispatch(request: Request): SimulationResult? {
        val path = request.url.encodedPath
        val method = request.method

        if (!path.contains("/auth")) return null

        val bodyString = runCatching {
            val buffer = Buffer()
            request.body?.writeTo(buffer)
            buffer.readUtf8()
        }.getOrDefault("")

        return when {
            path.endsWith("/auth/sign-in") && method == "POST" -> {
                if (bodyString.contains(MockAuthConstants.TRIGGER_ERROR_EMAIL) ||
                    bodyString.contains(MockAuthConstants.TRIGGER_ERROR_PASSWORD)
                ) {
                    SimulationResult(401, MockAuthData.ERROR_CREDENTIALS_JSON)
                } else {
                    SimulationResult(200, MockAuthData.SIGN_IN_SUCCESS_JSON)
                }
            }

            path.endsWith("/auth/sign-up") && method == "POST" -> {
                SimulationResult(201, MockAuthData.SIGN_UP_SUCCESS_JSON)
            }

            path.endsWith("/auth/oauth") && method == "POST" -> {
                SimulationResult(200, MockAuthData.SIGN_IN_SUCCESS_JSON)
            }

            path.endsWith("/auth/biometric/challenge") && method == "POST" -> {
                SimulationResult(200, MockAuthData.BIOMETRIC_CHALLENGE_JSON)
            }

            path.endsWith("/auth/biometric/verify") && method == "POST" -> {
                SimulationResult(200, MockAuthData.SIGN_IN_SUCCESS_JSON)
            }

            path.endsWith("/auth/otp/send") && method == "POST" -> {
                SimulationResult(200, """{"message":"OTP dispatched successfully"}""")
            }

            path.endsWith("/auth/otp/verify") && method == "POST" -> {
                if (bodyString.contains(MockAuthConstants.TRIGGER_ERROR_OTP)) {
                    SimulationResult(400, MockAuthData.ERROR_OTP_JSON)
                } else {
                    SimulationResult(200, MockAuthData.OTP_VERIFY_SUCCESS_JSON)
                }
            }

            path.endsWith("/auth/password/forgot") && method == "POST" -> {
                SimulationResult(200, """{"message":"Password reset OTP sent"}""")
            }

            path.endsWith("/auth/password/reset") && method == "POST" -> {
                SimulationResult(200, """{"message":"Password updated successfully"}""")
            }

            path.endsWith("/auth/token/refresh") && method == "POST" -> {
                SimulationResult(200, MockAuthData.SIGN_IN_SUCCESS_JSON)
            }

            path.endsWith("/auth/sign-out") && method == "POST" -> {
                SimulationResult(204, "")
            }

            else -> null
        }
    }
}
