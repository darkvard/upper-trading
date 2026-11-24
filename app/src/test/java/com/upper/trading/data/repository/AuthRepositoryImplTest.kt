package com.upper.trading.data.repository

import com.upper.trading.data.local.datasource.AuthLocalDataSource
import com.upper.trading.data.remote.datasource.AuthRemoteDataSource
import com.upper.trading.data.remote.dto.auth.AccountCompletionStateDto
import com.upper.trading.data.remote.dto.auth.AuthTokenResponseDto
import com.upper.trading.data.remote.dto.auth.ForgotPasswordRequestDto
import com.upper.trading.data.remote.dto.auth.OAuthSignInRequestDto
import com.upper.trading.data.remote.dto.auth.OtpSendRequestDto
import com.upper.trading.data.remote.dto.auth.OtpVerifyRequestDto
import com.upper.trading.data.remote.dto.auth.OtpVerifyResponseDto
import com.upper.trading.data.remote.dto.auth.RefreshTokenRequestDto
import com.upper.trading.data.remote.dto.auth.ResetPasswordRequestDto
import com.upper.trading.data.remote.dto.auth.SignInRequestDto
import com.upper.trading.data.remote.dto.auth.SignUpRequestDto
import com.upper.trading.domain.model.SignInCredentials
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class AuthRepositoryImplTest {

    private lateinit var fakeRemoteDataSource: FakeAuthRemoteDataSource
    private lateinit var fakeLocalDataSource: FakeAuthLocalDataSource
    private lateinit var repository: AuthRepositoryImpl

    @Before
    fun setUp() {
        fakeRemoteDataSource = FakeAuthRemoteDataSource()
        fakeLocalDataSource = FakeAuthLocalDataSource()
        repository = AuthRepositoryImpl(fakeRemoteDataSource, fakeLocalDataSource)
    }

    @Test
    fun `signIn stores tokens in local data source on success`() = runTest {
        val result = repository.signIn(SignInCredentials.Email("user@example.com", "Password@123"))

        assertTrue(result.isSuccess)
        val authResult = result.getOrNull()
        assertEquals("test_access_token", authResult?.accessToken)
        assertEquals("test_refresh_token", authResult?.refreshToken)

        // Verify tokens stored locally
        assertEquals("test_access_token", fakeLocalDataSource.savedAccessToken)
        assertEquals("test_refresh_token", fakeLocalDataSource.savedRefreshToken)
    }

    @Test
    fun `signOut clears session in local data source`() = runTest {
        fakeLocalDataSource.savedAccessToken = "existing_token"
        val result = repository.signOut()

        assertTrue(result.isSuccess)
        assertEquals("", fakeLocalDataSource.savedAccessToken)
        assertEquals("", fakeLocalDataSource.savedRefreshToken)
    }

    private class FakeAuthRemoteDataSource : AuthRemoteDataSource {
        override suspend fun signIn(request: SignInRequestDto): AuthTokenResponseDto {
            return AuthTokenResponseDto(
                accessToken = "test_access_token",
                refreshToken = "test_refresh_token",
                accountCompletionState = AccountCompletionStateDto(isPhoneVerified = true)
            )
        }

        override suspend fun signUp(request: SignUpRequestDto): AuthTokenResponseDto {
            return AuthTokenResponseDto(
                accessToken = "test_access_token",
                refreshToken = "test_refresh_token"
            )
        }

        override suspend fun oauthSignIn(request: OAuthSignInRequestDto): AuthTokenResponseDto {
            return AuthTokenResponseDto("oauth_token", "oauth_refresh")
        }

        override suspend fun sendOtp(request: OtpSendRequestDto) {}
        override suspend fun verifyOtp(request: OtpVerifyRequestDto) = OtpVerifyResponseDto(true, "action_123")
        override suspend fun forgotPassword(request: ForgotPasswordRequestDto) {}
        override suspend fun resetPassword(request: ResetPasswordRequestDto) {}
        override suspend fun refreshToken(request: RefreshTokenRequestDto) = AuthTokenResponseDto("new_token", "new_refresh")
        override suspend fun signOut(token: String) {}
    }

    private class FakeAuthLocalDataSource : AuthLocalDataSource {
        var savedAccessToken: String = ""
        var savedRefreshToken: String = ""
        private val _accessTokenFlow = MutableStateFlow("")
        private val _refreshTokenFlow = MutableStateFlow("")

        override val accessToken: Flow<String> get() = _accessTokenFlow
        override val refreshToken: Flow<String> get() = _refreshTokenFlow

        override suspend fun saveTokens(accessToken: String, refreshToken: String) {
            savedAccessToken = accessToken
            savedRefreshToken = refreshToken
            _accessTokenFlow.value = accessToken
            _refreshTokenFlow.value = refreshToken
        }

        override suspend fun clearSession() {
            savedAccessToken = ""
            savedRefreshToken = ""
            _accessTokenFlow.value = ""
            _refreshTokenFlow.value = ""
        }
    }
}
