package com.upper.trading.presentation.features.auth.otp

import app.cash.turbine.test
import com.upper.trading.domain.model.AuthResult
import com.upper.trading.domain.model.OtpChannel
import com.upper.trading.domain.model.OtpPurpose
import com.upper.trading.domain.model.SignInCredentials
import com.upper.trading.domain.model.SignUpCredentials
import com.upper.trading.domain.repository.AuthRepository
import com.upper.trading.domain.storage.AppDataStorage
import com.upper.trading.domain.usecase.auth.SendOtpUseCase
import com.upper.trading.domain.usecase.auth.VerifyOtpUseCase
import com.upper.trading.domain.usecase.notification.HasDeniedNotificationPermissionUseCase
import com.upper.trading.domain.usecase.notification.SetNotificationPermissionDeniedUseCase
import com.upper.trading.presentation.features.auth.signin.AuthInputMode
import com.upper.trading.presentation.navigation.AuthNavKey
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AuthOtpViewModelTest {

    private val testDispatcher: TestDispatcher = StandardTestDispatcher()
    private lateinit var fakeAuthRepository: FakeAuthRepository
    private lateinit var fakeAppDataStorage: FakeAppDataStorage
    private lateinit var verifyOtpUseCase: VerifyOtpUseCase
    private lateinit var sendOtpUseCase: SendOtpUseCase
    private lateinit var hasDeniedNotificationPermissionUseCase: HasDeniedNotificationPermissionUseCase
    private lateinit var setNotificationPermissionDeniedUseCase: SetNotificationPermissionDeniedUseCase
    private lateinit var viewModel: AuthOtpViewModel

    private val defaultNavKey = AuthNavKey.Otp(
        inputMode = AuthInputMode.EMAIL,
        email = "test@example.com",
        fromOnboarding = true
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeAuthRepository = FakeAuthRepository()
        fakeAppDataStorage = FakeAppDataStorage()
        verifyOtpUseCase = VerifyOtpUseCase(fakeAuthRepository)
        sendOtpUseCase = SendOtpUseCase(fakeAuthRepository)
        hasDeniedNotificationPermissionUseCase = HasDeniedNotificationPermissionUseCase(fakeAppDataStorage)
        setNotificationPermissionDeniedUseCase = SetNotificationPermissionDeniedUseCase(fakeAppDataStorage)

        viewModel = AuthOtpViewModel(
            navKeyOtp = defaultNavKey,
            verifyOtpUseCase = verifyOtpUseCase,
            sendOtpUseCase = sendOtpUseCase,
            hasDeniedNotificationPermissionUseCase = hasDeniedNotificationPermissionUseCase,
            setNotificationPermissionDeniedUseCase = setNotificationPermissionDeniedUseCase
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `onDigitsChange updates state with digits and does not auto-verify`() = runTest(testDispatcher) {
        val input = persistentListOf(1, 2, 3, 4, 5, 6)
        viewModel.onIntent(AuthOtpIntent.OnDigitsChange(input))

        assertEquals(input, viewModel.state.value.digitInput)
        assertTrue(viewModel.state.value.isButtonEnabled)
        assertFalse(viewModel.state.value.isLoading)
        assertEquals(null, viewModel.state.value.isValid)
    }

    @Test
    fun `verifyOtp with valid code sets isValid true and emits success effect`() = runTest(testDispatcher) {
        fakeAuthRepository.verifyResult = Result.success("action_token_123")
        viewModel.onIntent(AuthOtpIntent.OnDigitsChange(persistentListOf(4, 5, 2, 1, 8, 9)))

        viewModel.effect.test {
            viewModel.onIntent(AuthOtpIntent.VerifyOtp)
            testDispatcher.scheduler.advanceUntilIdle()

            assertTrue(viewModel.state.value.isValid == true)
            assertFalse(viewModel.state.value.isLoading)

            val toastEffect = awaitItem() as AuthOtpEffect.ShowToast
            assertTrue(toastEffect.isSuccess)

            val navEffect = awaitItem()
            assertEquals(AuthOtpEffect.NavigateSuccessScreen, navEffect)
        }
    }

    @Test
    fun `verifyOtp with invalid code sets isValid false and emits error toast`() = runTest(testDispatcher) {
        fakeAuthRepository.verifyResult = Result.failure(Exception("The OTP code entered is incorrect or has expired."))
        viewModel.onIntent(AuthOtpIntent.OnDigitsChange(persistentListOf(0, 0, 0, 0, 0, 0)))

        viewModel.effect.test {
            viewModel.onIntent(AuthOtpIntent.VerifyOtp)
            testDispatcher.scheduler.advanceUntilIdle()

            assertTrue(viewModel.state.value.isValid == false)
            assertFalse(viewModel.state.value.isLoading)

            val toastEffect = awaitItem() as AuthOtpEffect.ShowToast
            assertFalse(toastEffect.isSuccess)
        }
    }

    @Test
    fun `first click sends code, sets hasSentOtp true and starts countdown timer`() = runTest(testDispatcher) {
        assertFalse(viewModel.state.value.hasSentOtp)
        assertTrue(viewModel.state.value.canSendOtp)

        fakeAuthRepository.sendResult = Result.success(Unit)

        viewModel.effect.test {
            viewModel.onIntent(AuthOtpIntent.ResendOtp)
            testDispatcher.scheduler.runCurrent()

            val toastEffect = awaitItem() as AuthOtpEffect.ShowToast
            assertTrue(toastEffect.isSuccess)
            assertEquals(com.upper.trading.core.utils.UiText.StringResource(com.upper.trading.R.string.otp_sent_success), toastEffect.message)

            val notifEffect = awaitItem() as AuthOtpEffect.ShowOtpNotification
            assertEquals(com.upper.trading.core.network.mock.MockAuthConstants.currentActiveOtp, notifEffect.otpCode)

            assertTrue(viewModel.state.value.hasSentOtp)
            assertEquals(persistentListOf<Int>(), viewModel.state.value.digitInput)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `subsequent resendOtp when timer reaches 0 sends code and emits resent toast`() = runTest(testDispatcher) {
        fakeAuthRepository.sendResult = Result.success(Unit)

        viewModel.effect.test {
            // First send
            viewModel.onIntent(AuthOtpIntent.ResendOtp)
            testDispatcher.scheduler.runCurrent()

            val firstToast = awaitItem() as AuthOtpEffect.ShowToast
            assertTrue(firstToast.isSuccess)
            val firstNotif = awaitItem() as AuthOtpEffect.ShowOtpNotification
            assertEquals(com.upper.trading.core.network.mock.MockAuthConstants.currentActiveOtp, firstNotif.otpCode)

            // Fast forward 31 seconds
            testDispatcher.scheduler.advanceTimeBy(31_000L)
            assertTrue(viewModel.state.value.canSendOtp)

            // Second send (resend)
            viewModel.onIntent(AuthOtpIntent.ResendOtp)
            testDispatcher.scheduler.runCurrent()

            val resendToast = awaitItem() as AuthOtpEffect.ShowToast
            assertTrue(resendToast.isSuccess)
            assertEquals(com.upper.trading.core.utils.UiText.StringResource(com.upper.trading.R.string.otp_resent_success), resendToast.message)

            val resendNotif = awaitItem() as AuthOtpEffect.ShowOtpNotification
            assertEquals(com.upper.trading.core.network.mock.MockAuthConstants.currentActiveOtp, resendNotif.otpCode)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `SetNotificationPermissionDenied updates storage and state`() = runTest(testDispatcher) {
        testDispatcher.scheduler.runCurrent()
        assertFalse(viewModel.state.value.hasDeniedNotificationPermission)

        viewModel.onIntent(AuthOtpIntent.SetNotificationPermissionDenied(true))
        testDispatcher.scheduler.runCurrent()

        assertTrue(viewModel.state.value.hasDeniedNotificationPermission)
    }

    private class FakeAppDataStorage : AppDataStorage {
        private val _onboardingCompleted = MutableStateFlow(false)
        override val isOnboardingCompleted: Flow<Boolean> = _onboardingCompleted
        override suspend fun setOnboardingCompleted(isCompleted: Boolean) {
            _onboardingCompleted.value = isCompleted
        }

        private val _darkMode = MutableStateFlow<Boolean?>(null)
        override val isDarkMode: Flow<Boolean?> = _darkMode
        override suspend fun setDarkMode(isDarkMode: Boolean) {
            _darkMode.value = isDarkMode
        }

        private val _notificationPermissionDenied = MutableStateFlow(false)
        override val hasDeniedNotificationPermission: Flow<Boolean> = _notificationPermissionDenied
        override suspend fun setNotificationPermissionDenied(denied: Boolean) {
            _notificationPermissionDenied.value = denied
        }
    }

    private class FakeAuthRepository : AuthRepository {
        var verifyResult: Result<String> = Result.success("mock_token")
        var sendResult: Result<Unit> = Result.success(Unit)

        override suspend fun verifyOtp(channel: OtpChannel, destination: String, purpose: OtpPurpose, code: String): Result<String> {
            return verifyResult
        }

        override suspend fun sendOtp(channel: OtpChannel, destination: String, purpose: OtpPurpose): Result<Unit> {
            return sendResult
        }

        override suspend fun signIn(credentials: SignInCredentials): Result<AuthResult> = Result.success(AuthResult("t", "r"))
        override suspend fun getBiometricChallenge(deviceId: String?): Result<String> = Result.success("b")
        override suspend fun isBiometricEnabled(): Boolean = false
        override suspend fun setBiometricEnabled(isEnabled: Boolean): Result<Unit> = Result.success(Unit)
        override fun getDeviceId(): String = "dev"
        override suspend fun signUp(credentials: SignUpCredentials): Result<AuthResult> = Result.success(AuthResult("t", "r"))
        override suspend fun forgotPassword(identifier: String): Result<Unit> = Result.success(Unit)
        override suspend fun resetPassword(actionToken: String, newPassword: String): Result<Unit> = Result.success(Unit)
        override suspend fun signOut(): Result<Unit> = Result.success(Unit)
    }
}
