package com.upper.trading.presentation.features.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.upper.trading.R
import com.upper.trading.core.utils.ObserveAsEvents
import com.upper.trading.core.utils.ThemePreviews
import com.upper.trading.core.utils.bounceClick
import com.upper.trading.presentation.features.onboarding.components.ThemeModeToggle
import com.upper.trading.presentation.theme.AppColor
import com.upper.trading.presentation.theme.TradingTheme

object OnboardingThemeDefault {
    val TextSkipPaddingTop = 24.dp
    val TextSkipPaddingEnd = 27.dp

    val ImageThemePaddingTop = 40.dp
    val ImageThemeSize = 358.dp

    val ImagePaddingHorizontal = 26.dp

    val TextNextPadding = 8.dp
    val TextNextCorner = 4.dp

    val TextNextHeight = 40.dp

    val BodyContentPadding = 24.dp
    val BodyContentPaddingTop = 74.dp
    val BodyContentPaddingBottom = 40.dp
    val BodyContentPaddingHorizontal = 16.dp
}

@Composable
fun OnboardingThemeRoute(
    onNavigateToIntro: () -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ObserveAsEvents(viewModel.effect) {
        when (it) {
            OnboardingEffect.NavigateOnboardingIntro -> {
                onNavigateToIntro()
            }
            else -> Unit
        }
    }

    TradingTheme(darkTheme = state.isDarkTheme) {
        OnboardingThemeScreen(
            state = state,
            onIntent = viewModel::onIntent
        )
    }
}

@Composable
internal fun OnboardingThemeScreen(
    state: OnboardingState,
    onIntent: (OnboardingIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .paint(painterResource(R.drawable.img_theme_background))
            .statusBarsPadding()
            .navigationBarsPadding(),
    ) {
        Text(
            text = stringResource(R.string.skip),
            style = MaterialTheme.typography.titleMedium,
            color = AppColor.primarySurface,
            modifier = Modifier
                .padding(
                    top = OnboardingThemeDefault.TextSkipPaddingTop,
                    end = OnboardingThemeDefault.TextSkipPaddingEnd
                )
                .align(Alignment.End)
                .clickable {
                    onIntent(OnboardingIntent.SkipOnboarding)
                }
        )

        Image(
            painter = painterResource(if (state.isDarkTheme) {
                R.drawable.img_theme_dark
            } else {
                R.drawable.img_theme
            }),
            contentDescription = null,
            modifier = Modifier
                .padding(
                    top = OnboardingThemeDefault.ImageThemePaddingTop,
                    start = OnboardingThemeDefault.ImagePaddingHorizontal,
                    end = OnboardingThemeDefault.ImagePaddingHorizontal
                )
                .size(OnboardingThemeDefault.ImageThemeSize)
        )

        Spacer(modifier = Modifier.weight(1f))

        ThemeBodyContent(state = state, onIntent = onIntent)
    }
}

@Composable
internal fun ThemeBodyContent(
    state: OnboardingState,
    onIntent: (OnboardingIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                start = OnboardingThemeDefault.BodyContentPaddingHorizontal,
                end = OnboardingThemeDefault.BodyContentPaddingHorizontal,
                top = OnboardingThemeDefault.BodyContentPaddingTop,
                bottom = OnboardingThemeDefault.BodyContentPaddingBottom
            ),
        verticalArrangement = Arrangement.spacedBy(OnboardingThemeDefault.BodyContentPadding)
    ) {
        ThemeModeToggle(
            isDarkTheme = state.isDarkTheme,
            onDarkThemeSelected = { onIntent(OnboardingIntent.SelectTheme(it)) },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Text(
            text = stringResource(R.string.select_theme_desc),
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(OnboardingThemeDefault.TextNextHeight)
                .bounceClick {
                    onIntent(OnboardingIntent.ConfirmTheme)
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
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center
            )
        }
    }
}

@ThemePreviews
@Composable
fun PreviewOnboardingThemeScreen() {
    TradingTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            OnboardingThemeScreen(
                state = OnboardingState(),
                onIntent = { }
            )
        }
    }
}
