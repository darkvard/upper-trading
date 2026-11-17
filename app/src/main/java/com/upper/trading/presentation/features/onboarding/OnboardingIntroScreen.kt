package com.upper.trading.presentation.features.onboarding

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.upper.trading.core.utils.ObserveAsEvents
import com.upper.trading.core.utils.ThemePreviews
import com.upper.trading.core.utils.showToast
import com.upper.trading.presentation.theme.TradingTheme


@Composable
fun OnboardingIntroRoute(
    onNavigateToAuthSignIn: () -> Unit,
    onNavigateToMainTrade: () -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val state by viewModel.state.collectAsStateWithLifecycle()

    ObserveAsEvents(viewModel.effect) {
        when (it) {
            OnboardingEffect.NavigateAuthSignIn -> {
//                onNavigateToAuthSignIn()
                Log.d("check_onboarding", "[Intro screen]: NavigateAuthSignIn")
                context.showToast("Navigate to Auth")
            }

            OnboardingEffect.NavigateHomeTrade -> {
//                onNavigateToMainTrade()
                Log.d("check_onboarding", "[Intro screen]: NavigateMainTrade")
                context.showToast("Navigate to Trade")
            }


            else -> Unit
        }
    }

    OnboardingIntroScreen(
        state = state,
        onIntent = viewModel::onIntent
    )
}

@Composable
fun OnboardingIntroScreen(
    state: OnboardingState,
    onIntent: (OnboardingIntent) -> Unit,
    modifier: Modifier = Modifier,
) {

    var temp by rememberSaveable() { mutableIntStateOf(0) }

    Column(
        modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Intro ${state.currentSlideIndex}")
        Button(
            onClick = { onIntent(OnboardingIntent.SelectTheme(true)) }
        ) {
            Text("navigate to auth")
        }
        Button(
            onClick = { onIntent(OnboardingIntent.SkipOnboarding) }
        ) {
            Text("skip (to  main)")
        }
        Button(
            onClick = {
                if (temp < 3) {
                    temp++
                    onIntent(OnboardingIntent.SwipeSlideIntro(temp))
                } else {
                    onIntent(OnboardingIntent.CompleteIntro)
                }
            }
        ) {
            Text("count")
        }
    }
}

@ThemePreviews
@Composable
fun PreviewOnboardingIntroScreen() {
    TradingTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            OnboardingIntroScreen(
                state = OnboardingState(),
                onIntent = {}
            )
        }
    }
}