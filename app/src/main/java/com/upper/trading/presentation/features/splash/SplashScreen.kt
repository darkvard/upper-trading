package com.upper.trading.presentation.features.splash

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.upper.trading.R
import com.upper.trading.core.utils.ObserveAsEvents
import com.upper.trading.core.utils.ThemePreviews
import com.upper.trading.core.utils.showToast

object SplashDefault {
    val ImageLogoHeight = 74.dp
    val ImageLogoWidth = 228.dp
}

@Composable
fun SplashRoute(
    onNavigateOnboardingTheme: () -> Unit,
    onNavigateMainTrade: () -> Unit,
    viewModel: SplashViewModel = hiltViewModel()
) {
    ObserveAsEvents(viewModel.effect) {
        when (it) {
            is SplashEffect.NavigateOnboardingTheme -> {
                onNavigateOnboardingTheme()
            }
            is SplashEffect.NavigateMainTrade -> {
                onNavigateMainTrade()
            }
        }
    }

    SplashScreen()
}

@Composable
fun SplashScreen(
    modifiter: Modifier = Modifier
) {
    Box(
        modifier = modifiter
            .fillMaxSize()
            .background(colorResource(id = R.color.splash_background))

    ) {
        Image(
            painterResource(id = R.drawable.img_splash),
            contentDescription = null,
            modifier = modifiter.fillMaxSize()
        )

        Image(
            painterResource(id = R.drawable.img_logo),
            contentDescription = null,
            modifier = modifiter
                .align(Alignment.Center)
                .width(SplashDefault.ImageLogoWidth)
                .height(SplashDefault.ImageLogoHeight)

        )
    }
}

@ThemePreviews
@Composable
fun PreviewSplashScreen() {
    SplashScreen()
}