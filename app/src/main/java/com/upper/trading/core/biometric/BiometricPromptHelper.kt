package com.upper.trading.core.biometric

import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import java.security.Signature

/**
 * Helper utility to construct and launch [BiometricPrompt] on the UI thread.
 */
object BiometricPromptHelper {

    /**
     * Launches the system biometric dialog bound to a [BiometricPrompt.CryptoObject].
     *
     * @param activity The host [FragmentActivity] required by AndroidX Biometric.
     * @param cryptoObject Cryptographic wrapper containing the unauthenticated [Signature].
     * @param title Title displayed in the biometric prompt.
     * @param subtitle Subtitle/description explaining the purpose.
     * @param negativeButtonText Text for the cancel/fallback button.
     * @param onSuccess Invoked when biometric authentication succeeds with the unlocked [Signature].
     * @param onError Invoked on unrecoverable errors (e.g., canceled by user, hardware unavailable).
     * @param onFailed Invoked when a biometric is rejected (e.g., unrecognized fingerprint).
     */
    fun showBiometricPrompt(
        activity: FragmentActivity,
        cryptoObject: BiometricPrompt.CryptoObject,
        title: String,
        subtitle: String,
        negativeButtonText: String,
        onSuccess: (unlockedSignature: Signature?) -> Unit,
        onError: (errorCode: Int, errString: CharSequence) -> Unit,
        onFailed: () -> Unit
    ) {
        val executor = ContextCompat.getMainExecutor(activity)

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(title)
            .setSubtitle(subtitle)
            .setNegativeButtonText(negativeButtonText)
            .setAllowedAuthenticators(BIOMETRIC_STRONG)
            .build()

        val biometricPrompt = BiometricPrompt(
            activity,
            executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    onSuccess(result.cryptoObject?.signature)
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    onError(errorCode, errString)
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    onFailed()
                }
            }
        )

        biometricPrompt.authenticate(promptInfo, cryptoObject)
    }
}
