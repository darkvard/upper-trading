package com.upper.trading.presentation.features.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.upper.trading.R
import com.upper.trading.core.utils.ObserveAsEvents
import com.upper.trading.core.utils.ThemePreviews
import com.upper.trading.core.utils.bounceClick
import com.upper.trading.domain.model.IntroSlide
import com.upper.trading.presentation.components.layout.ImageActionLayout
import com.upper.trading.presentation.features.onboarding.components.HorizontalPagerIndicator
import com.upper.trading.presentation.theme.AppColor
import com.upper.trading.presentation.theme.TradingTheme

@Composable
fun OnboardingIntroRoute(
    onNavigateToAuthSignIn: () -> Unit,
    onNavigateToMainTrade: () -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ObserveAsEvents(viewModel.effect) { effect ->
        when (effect) {
            OnboardingEffect.NavigateAuthSignIn -> {
                onNavigateToAuthSignIn()
            }

            OnboardingEffect.NavigateHomeTrade -> {
                onNavigateToMainTrade()
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
    val currentSlide = state.introSlides.getOrNull(state.currentSlideIndex) ?: return

    ImageActionLayout(
        imagePainter = painterResource(currentSlide.imageRes),
        actionText = stringResource(R.string.skip),
        onActionClick = { onIntent(OnboardingIntent.SkipOnboarding) },
        modifier = modifier
    ) {
        HorizontalPagerIndicator(
            pageCount = state.introSlides.size,
            currentPage = state.currentSlideIndex
        )

        Text(
            text = stringResource(currentSlide.titleRes),
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(OnboardingThemeDefault.TextNextHeight)
                .bounceClick {
                    if (state.currentSlideIndex < state.introSlides.size - 1) {
                        onIntent(OnboardingIntent.UpdateSlideIntro(state.currentSlideIndex + 1))
                    } else {
                        onIntent(OnboardingIntent.CompleteIntro)
                    }
                }
                .background(
                    color = AppColor.primarySurface,
                    shape = RoundedCornerShape(OnboardingThemeDefault.TextNextCorner)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(R.string.next),
                color = Color.White,
                style = MaterialTheme.typography.titleLarge
            )
        }
    }
}

@ThemePreviews
@Composable
fun PreviewOnboardingIntroScreen() {
    val mockSlides = listOf(
        IntroSlide(R.drawable.img_onboarding_trusted, R.string.intro_desc_1),
        IntroSlide(R.drawable.img_onboarding_any_currency, R.string.intro_desc_2),
        IntroSlide(R.drawable.img_onboarding_invest_and_earn, R.string.intro_desc_3)
    )

    TradingTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            OnboardingIntroScreen(
                state = OnboardingState(
                    currentSlideIndex = 0,
                    introSlides = mockSlides
                ),
                onIntent = {}
            )
        }
    }
}
