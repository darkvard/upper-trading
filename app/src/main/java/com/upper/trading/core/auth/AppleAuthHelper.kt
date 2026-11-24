package com.upper.trading.core.auth

import android.app.Activity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.OAuthCredential
import com.google.firebase.auth.OAuthProvider
import com.upper.trading.core.network.config.NetworkConfig
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class AppleAuthHelper {

    private val auth: FirebaseAuth
        get() = FirebaseAuth.getInstance()

    suspend fun signIn(activity: Activity): Result<String> {
        val provider = OAuthProvider.newBuilder("apple.com").apply {
            scopes = listOf("email", "name")
        }.build()

        return suspendCancellableCoroutine { continuation ->
            try {
                val pending = auth.pendingAuthResult
                if (pending != null) {
                    pending.addOnSuccessListener { authResult ->
                        val oAuthCredential = authResult.credential as? OAuthCredential
                        val idToken = oAuthCredential?.idToken
                        if (idToken != null) {
                            if (continuation.isActive) continuation.resume(Result.success(idToken))
                        } else {
                            if (continuation.isActive) continuation.resume(Result.failure(IllegalStateException("No Apple ID Token returned")))
                        }
                    }.addOnFailureListener { error ->
                        handleFailure(error, continuation)
                    }
                } else {
                    auth.startActivityForSignInWithProvider(activity, provider)
                        .addOnSuccessListener { authResult ->
                            val oAuthCredential = authResult.credential as? OAuthCredential
                            val idToken = oAuthCredential?.idToken
                            if (idToken != null) {
                                if (continuation.isActive) continuation.resume(Result.success(idToken))
                            } else {
                                if (continuation.isActive) continuation.resume(Result.failure(IllegalStateException("No Apple ID Token returned")))
                            }
                        }
                        .addOnFailureListener { error ->
                            handleFailure(error, continuation)
                        }
                }
            } catch (e: Exception) {
                handleFailure(e, continuation)
            }
        }
    }

    private fun handleFailure(
        error: Exception,
        continuation: CancellableContinuation<Result<String>>
    ) {
        if (!continuation.isActive) return
        val errorMessage = error.message.orEmpty()
        if (error is CancellationException || errorMessage.contains("cancel", ignoreCase = true)) {
            continuation.resume(Result.failure(CancellationException("Sign-in was cancelled")))
        } else if (NetworkConfig.isMockEnabled) {
            continuation.resume(Result.success("mock_apple_id_token_${System.currentTimeMillis()}"))
        } else {
            continuation.resume(Result.failure(error))
        }
    }
}
