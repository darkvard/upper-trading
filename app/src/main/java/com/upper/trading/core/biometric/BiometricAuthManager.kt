package com.upper.trading.core.biometric

import android.content.Context
import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricPrompt
import dagger.hilt.android.qualifiers.ApplicationContext
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.security.PrivateKey
import java.security.Signature
import javax.inject.Inject
import javax.inject.Singleton

enum class BiometricStatus {
    READY,
    NOT_ENROLLED,
    NO_HARDWARE,
    HARDWARE_UNAVAILABLE,
    SECURITY_UPDATE_REQUIRED
}

/**
 * [BiometricAuthManager] handles hardware-backed cryptographic key generation and validation
 * using the Android Keystore system.
 *
 * ## Security Architecture:
 * 1. **Hardware-Backed Keystore (TEE / StrongBox)**:
 *    Cryptographic keys are generated and stored inside the device's secure hardware enclave
 *    (Trusted Execution Environment or StrongBox Keymaster). Private key material NEVER enters
 *    standard application memory (RAM) and cannot be extracted, even on rooted devices.
 *
 * 2. **Hardware-Enforced User Authentication**:
 *    Keys are marked with [KeyGenParameterSpec.Builder.setUserAuthenticationRequired] (true).
 *    The hardware TEE refuses to sign any data with the private key until Android OS confirms
 *    a successful biometric authentication event for the associated [BiometricPrompt.CryptoObject].
 *
 * 3. **Anti-Tampering & Enrollment Invalidation**:
 *    [KeyGenParameterSpec.Builder.setInvalidatedByBiometricEnrollment] (true) ensures that if an
 *    adversary gains physical access and registers a new fingerprint in device Settings, the existing
 *    key is permanently invalidated, preventing unauthorized access.
 */
@Singleton
class BiometricAuthManager @Inject constructor(
    @param:ApplicationContext private val context: Context
) {
    private val keyStore: KeyStore = KeyStore.getInstance(ANDROID_KEYSTORE).apply {
        load(null)
    }

    /**
     * Inspects the current device's biometric sensor capabilities and user enrollment state.
     *
     * @return [BiometricStatus.READY] if Class 3 biometric authentication can proceed immediately,
     *         [BiometricStatus.NOT_ENROLLED] if hardware exists but no biometrics are enrolled,
     *         or [BiometricStatus.NO_HARDWARE] if unsupported.
     */
    fun getBiometricStatus(): BiometricStatus {
        val manager = BiometricManager.from(context)
        return when (manager.canAuthenticate(BIOMETRIC_STRONG)) {
            BiometricManager.BIOMETRIC_SUCCESS -> BiometricStatus.READY
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> BiometricStatus.NOT_ENROLLED
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> BiometricStatus.NO_HARDWARE
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> BiometricStatus.HARDWARE_UNAVAILABLE
            BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED -> BiometricStatus.SECURITY_UPDATE_REQUIRED
            else -> BiometricStatus.NO_HARDWARE
        }
    }

    /**
     * Checks if the host device possesses Class 3 (Strong) biometric hardware
     * (e.g., Fingerprint scanner, 3D IR Face Unlock) and has at least one enrolled biometric.
     *
     * @return `true` if biometric authentication can be performed immediately.
     */
    fun canAuthenticate(): Boolean = getBiometricStatus() == BiometricStatus.READY

    /**
     * Generates an Elliptic Curve (EC) KeyPair inside the hardware-backed Android Keystore.
     *
     * The private key is configured for digital signing using ECDSA (`SHA256withECDSA`).
     * It requires user biometric authentication every time a signature operation is performed.
     *
     * @return `true` if the key was created successfully or already exists.
     */
    fun generateSecretKey(): Boolean {
        return try {
            if (!keyStore.containsAlias(KEY_ALIAS)) {
                val keyPairGenerator = KeyPairGenerator.getInstance(
                    KeyProperties.KEY_ALGORITHM_EC,
                    ANDROID_KEYSTORE
                )
                val builder = KeyGenParameterSpec.Builder(
                    KEY_ALIAS,
                    KeyProperties.PURPOSE_SIGN or KeyProperties.PURPOSE_VERIFY
                )
                    .setDigests(KeyProperties.DIGEST_SHA256)
                    // Hardware TEE locks the key until valid biometric authentication occurs
                    .setUserAuthenticationRequired(true)
                    // Invalidate key if user enrolls a new fingerprint in system Settings
                    .setInvalidatedByBiometricEnrollment(true)

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    // Set timeout duration to 0 so the key is unlocked strictly for this single CryptoObject operation
                    builder.setUserAuthenticationParameters(
                        0,
                        KeyProperties.AUTH_BIOMETRIC_STRONG
                    )
                }

                keyPairGenerator.initialize(builder.build())
                keyPairGenerator.generateKeyPair()
            }
            true
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Creates and initializes a [BiometricPrompt.CryptoObject] wrapping a [Signature] instance.
     *
     * The [Signature] is initialized in `initSign` mode with the Keystore's [PrivateKey].
     * Passing this [CryptoObject] to [BiometricPrompt.authenticate] instructs Android to unlock
     * the private key upon biometric success, allowing the app to sign the server's challenge nonce.
     *
     * @return An unauthenticated [BiometricPrompt.CryptoObject], or `null` if key retrieval fails.
     */
    fun createCryptoObject(): BiometricPrompt.CryptoObject? {
        return try {
            if (!keyStore.containsAlias(KEY_ALIAS)) {
                val generated = generateSecretKey()
                if (!generated) return null
            }

            val privateKey = keyStore.getKey(KEY_ALIAS, null) as? PrivateKey ?: return null
            val signature = Signature.getInstance(SIGNATURE_ALGORITHM).apply {
                initSign(privateKey)
            }
            BiometricPrompt.CryptoObject(signature)
        } catch (e: android.security.keystore.KeyPermanentlyInvalidatedException) {
            // Key was permanently invalidated due to new biometric enrollment in system Settings.
            // Purge the invalidated key so a fresh key pair can be generated upon next primary sign-in.
            try {
                keyStore.deleteEntry(KEY_ALIAS)
            } catch (_: Exception) {}
            null
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Deletes the biometric key pair from the Android Keystore.
     * Useful when resetting user biometric enrollment or signing out.
     */
    fun deleteKey(): Boolean {
        return try {
            if (keyStore.containsAlias(KEY_ALIAS)) {
                keyStore.deleteEntry(KEY_ALIAS)
            }
            true
        } catch (e: Exception) {
            false
        }
    }

    companion object {
        private const val ANDROID_KEYSTORE = "AndroidKeyStore"
        private const val KEY_ALIAS = "upper_trading_biometric_key"
        private const val SIGNATURE_ALGORITHM = "SHA256withECDSA"

        /**
         * Signs the given [challengeNonce] with the hardware-unlocked [Signature] and returns the Base64 string.
         *
         * Encapsulates digital signing and encoding logic cleanly away from UI/Presentation layer.
         *
         * @param signature The authenticated [Signature] object returned from [BiometricPrompt.AuthenticationResult].
         * @param challengeNonce The server challenge nonce to sign.
         * @return Base64 encoded digital signature string (`SHA256withECDSA`), or null if signing fails.
         */
        fun signChallenge(signature: Signature?, challengeNonce: String): String? {
            return try {
                signature?.let { sig ->
                    sig.update(challengeNonce.toByteArray(Charsets.UTF_8))
                    val signatureBytes = sig.sign()
                    android.util.Base64.encodeToString(signatureBytes, android.util.Base64.NO_WRAP)
                }
            } catch (e: Exception) {
                null
            }
        }
    }
}
