package com.upper.trading.presentation.features.home

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
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
import com.upper.trading.presentation.components.snackbar.AppSnackbarHost
import com.upper.trading.presentation.components.snackbar.LocalAppSnackbar
import com.upper.trading.presentation.components.snackbar.rememberAppSnackbarHostState
import com.upper.trading.presentation.navigation.AppNavDisplay
import com.upper.trading.presentation.theme.TradingTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeActivity : ComponentActivity() {

    private val viewModel: HomeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val state by viewModel.state.collectAsStateWithLifecycle()
            val isDarkMode = state.isDarkMode ?: isSystemInDarkTheme()
            val snackbarHostState = rememberAppSnackbarHostState()

            TradingTheme(darkTheme = isDarkMode) {
                CompositionLocalProvider(LocalAppSnackbar provides snackbarHostState) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            AppNavDisplay()

                            AppSnackbarHost(
                                hostState = snackbarHostState,
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .navigationBarsPadding()
                                    .padding(bottom = 20.dp, start = 16.dp, end = 16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}