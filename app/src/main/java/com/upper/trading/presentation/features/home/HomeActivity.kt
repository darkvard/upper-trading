package com.upper.trading.presentation.features.home

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.fragment.app.FragmentActivity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.upper.trading.presentation.components.dialog.AppDialogHost
import com.upper.trading.presentation.components.dialog.LocalAppDialog
import com.upper.trading.presentation.components.snackbar.AppSnackbarHost
import com.upper.trading.presentation.components.snackbar.LocalAppSnackbar
import com.upper.trading.presentation.components.state.LocalAppState
import com.upper.trading.presentation.components.state.rememberAppState
import com.upper.trading.presentation.navigation.AppNavDisplay
import com.upper.trading.presentation.theme.TradingTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * [HomeActivity] extends [FragmentActivity] instead of [androidx.activity.ComponentActivity].
 *
 * WHY FragmentActivity?
 * AndroidX Biometric (`androidx.biometric:biometric`) explicitly requires a [FragmentActivity]
 * to host and manage the lifecycle and dialog callbacks of [androidx.biometric.BiometricPrompt].
 * Standard [androidx.activity.ComponentActivity] does not provide the FragmentManager required
 * internally by BiometricPrompt.
 * (Note: If using CredentialManager with Passkeys/FIDO2, ComponentActivity is sufficient,
 * but for the native system biometric dialogs [BiometricPrompt], FragmentActivity is mandatory).
 */
@AndroidEntryPoint
class HomeActivity : FragmentActivity() {

    private val viewModel: HomeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val state by viewModel.state.collectAsStateWithLifecycle()
            val isDarkMode = state.isDarkMode ?: isSystemInDarkTheme()
            val appState = rememberAppState()

            TradingTheme(darkTheme = isDarkMode) {
                CompositionLocalProvider(
                    LocalAppState provides appState,
                    LocalAppSnackbar provides appState.snackbar,
                    LocalAppDialog provides appState.dialog
                ) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            AppNavDisplay()

                            AppSnackbarHost(
                                hostState = appState.snackbar,
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .navigationBarsPadding()
                                    .padding(bottom = 20.dp, start = 16.dp, end = 16.dp)
                            )

                            AppDialogHost(
                                state = appState.dialog
                            )
                        }
                    }
                }
            }
        }
    }
}