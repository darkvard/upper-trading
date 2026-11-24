package com.upper.trading.presentation.features.auth.signin.components

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import com.upper.trading.core.auth.AppleAuthHelper
import com.upper.trading.core.auth.GoogleAuthHelper
import com.upper.trading.core.utils.UiText
import com.upper.trading.core.utils.findActivity
import com.upper.trading.presentation.features.auth.signin.AuthSignInIntent
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun rememberSocialAuthHandler(
    onIntent: (AuthSignInIntent) -> Unit,
): SocialAuthHandler {
    val context = LocalContext.current
    val activity = remember(context) { context.findActivity() }
    val googleAuthHelper = remember(context) { GoogleAuthHelper(context) }
    val appleAuthHelper = remember { AppleAuthHelper() }
    val scope = rememberCoroutineScope()

    DisposableEffect(activity) {
        if (activity != null) {
            googleAuthHelper.updateContext(activity)
        }
        onDispose {
            googleAuthHelper.clearContext()
        }
    }

    return remember(activity, onIntent) {
        SocialAuthHandler(
            activity = activity,
            googleAuthHelper = googleAuthHelper,
            appleAuthHelper = appleAuthHelper,
            scope = scope,
            onIntent = onIntent,
        )
    }
}

class SocialAuthHandler(
    private val activity: Activity?,
    private val googleAuthHelper: GoogleAuthHelper,
    private val appleAuthHelper: AppleAuthHelper,
    private val scope: CoroutineScope,
    private val onIntent: (AuthSignInIntent) -> Unit,
) {
    fun launchGoogleSignIn() {
        if (activity == null) return
        scope.launch {
            googleAuthHelper.signIn(activity)
                .onSuccess { idToken ->
                    onIntent(AuthSignInIntent.OnGoogleSignInSuccess(idToken))
                }
                .onFailure { error ->
                    if (error !is CancellationException) {
                        onIntent(
                            AuthSignInIntent.OnGoogleSignInFailed(
                                UiText.DynamicString(error.message ?: "Google sign in failed")
                            )
                        )
                    }
                }
        }
    }

    fun launchAppleSignIn() {
        if (activity == null) return
        scope.launch {
            appleAuthHelper.signIn(activity)
                .onSuccess { idToken ->
                    onIntent(AuthSignInIntent.OnAppleSignInSuccess(idToken))
                }
                .onFailure { error ->
                    if (error !is CancellationException) {
                        onIntent(
                            AuthSignInIntent.OnAppleSignInFailed(
                                UiText.DynamicString(error.message ?: "Apple sign in failed")
                            )
                        )
                    }
                }
        }
    }
}
