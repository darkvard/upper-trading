package com.upper.trading.core.network.engine

import com.upper.trading.core.network.mock.MockAuthConstants
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class AuthSimulationDispatcherTest {

    private lateinit var dispatcher: AuthSimulationDispatcher

    @Before
    fun setUp() {
        dispatcher = AuthSimulationDispatcher()
    }

    @Test
    fun `dispatch signIn with valid credentials returns 200 and tokens`() {
        val request = Request.Builder()
            .url("http://localhost:8080/api/v1/auth/sign-in")
            .post("""{"identifier":"valid@example.com","password":"ValidPass123"}""".toRequestBody("application/json".toMediaType()))
            .build()

        val result = dispatcher.dispatch(request)

        assertNotNull(result)
        assertEquals(200, result!!.statusCode)
        assertTrue(result.jsonBody.contains(MockAuthConstants.SAMPLE_ACCESS_TOKEN))
        assertTrue(result.jsonBody.contains(MockAuthConstants.SAMPLE_REFRESH_TOKEN))
    }

    @Test
    fun `dispatch signIn with trigger error email returns 401 INVALID_CREDENTIALS`() {
        val request = Request.Builder()
            .url("http://localhost:8080/api/v1/auth/sign-in")
            .post("""{"identifier":"${MockAuthConstants.TRIGGER_ERROR_EMAIL}","password":"ValidPass123"}""".toRequestBody("application/json".toMediaType()))
            .build()

        val result = dispatcher.dispatch(request)

        assertNotNull(result)
        assertEquals(401, result!!.statusCode)
        assertTrue(result.jsonBody.contains(MockAuthConstants.CODE_INVALID_CREDENTIALS))
    }

    @Test
    fun `dispatch signUp returns 201 Created`() {
        val request = Request.Builder()
            .url("http://localhost:8080/api/v1/auth/sign-up")
            .post("""{"identifier":"newuser@example.com","password":"NewPassword123"}""".toRequestBody("application/json".toMediaType()))
            .build()

        val result = dispatcher.dispatch(request)

        assertNotNull(result)
        assertEquals(201, result!!.statusCode)
        assertTrue(result.jsonBody.contains(MockAuthConstants.SAMPLE_PENDING_ACCESS_TOKEN))
    }

    @Test
    fun `dispatch verifyOtp with trigger error code returns 400 INVALID_OTP`() {
        val request = Request.Builder()
            .url("http://localhost:8080/api/v1/auth/otp/verify")
            .post("""{"otp_code":"${MockAuthConstants.TRIGGER_ERROR_OTP}"}""".toRequestBody("application/json".toMediaType()))
            .build()

        val result = dispatcher.dispatch(request)

        assertNotNull(result)
        assertEquals(400, result!!.statusCode)
        assertTrue(result.jsonBody.contains(MockAuthConstants.CODE_INVALID_OTP))
    }

    @Test
    fun `dispatch verifyOtp with valid code returns 200 and action_token`() {
        val request = Request.Builder()
            .url("http://localhost:8080/api/v1/auth/otp/verify")
            .post("""{"otp_code":"${MockAuthConstants.SAMPLE_VALID_OTP}"}""".toRequestBody("application/json".toMediaType()))
            .build()

        val result = dispatcher.dispatch(request)

        assertNotNull(result)
        assertEquals(200, result!!.statusCode)
        assertTrue(result.jsonBody.contains(MockAuthConstants.SAMPLE_ACTION_TOKEN))
    }
}
