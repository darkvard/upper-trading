package com.upper.trading.core.auth

import android.app.Activity
import android.content.Context
import android.content.MutableContextWrapper
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.NoCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.upper.trading.BuildConfig
import java.security.SecureRandom
import java.util.Base64
import kotlinx.coroutines.CancellationException

class GoogleAuthHelper(context: Context) {

    private val contextWrapper = MutableContextWrapper(context)
    private val credentialManager = CredentialManager.create(contextWrapper)

    fun updateContext(activity: Activity) {
        contextWrapper.baseContext = activity
    }

    fun clearContext() {
        contextWrapper.baseContext = contextWrapper.applicationContext
    }

    suspend fun signIn(activity: Activity): Result<String> {
        updateContext(activity)
        val webClientId = BuildConfig.GOOGLE_WEB_CLIENT_ID
        if (webClientId.isBlank()) {
            return Result.failure(
                IllegalStateException("GOOGLE_WEB_CLIENT_ID is not configured in local.properties")
            )
        }

        val nonce = generateNonce()
        val signInWithGoogleOption = GetSignInWithGoogleOption.Builder(serverClientId = webClientId)
            .setNonce(nonce)
            .build()
        val buttonRequest = GetCredentialRequest.Builder()
            .addCredentialOption(signInWithGoogleOption)
            .build()

        return try {
            executeRequest(activity, buttonRequest)
        } catch (_: NoCredentialException) {
            val allAccountsOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(webClientId)
                .setAutoSelectEnabled(false)
                .setNonce(nonce)
                .build()

            val allAccountsRequest = GetCredentialRequest.Builder()
                .addCredentialOption(allAccountsOption)
                .build()

            try {
                executeRequest(activity, allAccountsRequest)
            } catch (fallbackError: Throwable) {
                mapError(fallbackError)
            }
        } catch (e: Throwable) {
            mapError(e)
        }
    }

    private suspend fun executeRequest(activity: Activity, request: GetCredentialRequest): Result<String> {
        val response = credentialManager.getCredential(
            request = request,
            context = activity
        )
        val credential = response.credential
        if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
            val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
            return Result.success(googleIdTokenCredential.idToken)
        }
        return Result.failure(IllegalStateException("Unsupported credential type: ${credential.type}"))
    }

    private fun mapError(e: Throwable): Result<String> {
        return if (e is GetCredentialCancellationException) {
            Result.failure(CancellationException("Sign-in was cancelled"))
        } else {
            Result.failure(e)
        }
    }

    private fun generateNonce(byteLength: Int = 32): String {
        val randomBytes = ByteArray(byteLength)
        SecureRandom().nextBytes(randomBytes)
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes)
    }
}
