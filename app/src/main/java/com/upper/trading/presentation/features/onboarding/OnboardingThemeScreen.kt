package com.upper.trading.presentation.features.onboarding

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.upper.trading.R
import com.upper.trading.core.utils.ObserveAsEvents
import com.upper.trading.core.utils.ThemePreviews
import com.upper.trading.core.utils.TradingPreview
import com.upper.trading.presentation.components.button.AppPrimaryButton
import com.upper.trading.presentation.components.layout.ImageActionLayout
import com.upper.trading.presentation.features.onboarding.components.ThemeModeToggle
import com.upper.trading.presentation.theme.TradingTheme

object OnboardingThemeDefault {
    val TextNextCorner = 4.dp
    val TextNextHeight = 40.dp
}

@Composable
fun OnboardingThemeRoute(
    onNavigateToIntro: () -> Unit,
    onNavigateToHomeTrade: () -> Unit = {},
    isSystemDark: Boolean = isSystemInDarkTheme(),
    viewModel: OnboardingViewModel = hiltViewModel(
        creationCallback = { factory: OnboardingViewModel.Factory ->
            factory.create(isSystemDarkTheme = isSystemDark)
        }
    )
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(isSystemDark) {
        viewModel.onIntent(OnboardingIntent.SelectTheme(isSystemDark))
    }

    ObserveAsEvents(viewModel.effect) {
        when (it) {
            OnboardingEffect.NavigateOnboardingIntro -> {
                onNavigateToIntro()
            }
            OnboardingEffect.NavigateHomeTrade -> {
                onNavigateToHomeTrade()
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
    val themeImage = painterResource(
        if (state.isDarkTheme) R.drawable.img_theme_dark else R.drawable.img_theme
    )

    ImageActionLayout(
        imagePainter = themeImage,
        actionText = stringResource(R.string.skip),
        onActionClick = { onIntent(OnboardingIntent.SkipOnboardingIntro) },
        modifier = modifier
    ) {
        ThemeModeToggle(
            isDarkTheme = state.isDarkTheme,
            onDarkThemeSelected = { onIntent(OnboardingIntent.SelectTheme(it)) }
        )

        Text(
            text = stringResource(R.string.select_theme_desc),
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )

        AppPrimaryButton(
            text = stringResource(R.string.next),
            onClick = {
                onIntent(OnboardingIntent.ConfirmTheme)
            },
            shape = RoundedCornerShape(OnboardingThemeDefault.TextNextCorner),
            height = OnboardingThemeDefault.TextNextHeight
        )
    }
}


@ThemePreviews
@Composable
fun PreviewOnboardingThemeScreen() {
    TradingPreview {
        OnboardingThemeScreen(
            state = OnboardingState(),
            onIntent = { }
        )
    }
}
