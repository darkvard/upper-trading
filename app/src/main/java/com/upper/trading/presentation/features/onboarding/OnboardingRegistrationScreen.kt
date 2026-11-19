package com.upper.trading.presentation.features.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.upper.trading.R
import com.upper.trading.core.utils.ObserveAsEvents
import com.upper.trading.core.utils.ThemePreviews
import com.upper.trading.core.utils.TradingPreview
import com.upper.trading.core.utils.bounceClick
import com.upper.trading.presentation.components.layout.ImageActionDefaults
import com.upper.trading.presentation.components.layout.ImageActionLayout
import com.upper.trading.presentation.theme.AppColor

data class RegistrationStyle(
    val buttonStrokeWidth: Dp = 1.dp,
    val buttonCorner: Dp = 4.dp,
    val buttonPadding: Dp = 8.dp,
    val bottomSpacing: Dp = 16.dp,
    val buttonPaddingTop: Dp = 8.dp,
    val buttonShape: RoundedCornerShape = RoundedCornerShape(buttonCorner)
)

object RegistrationDefault {
    val style: RegistrationStyle = RegistrationStyle()
}

@Composable
fun OnboardingRegistrationRoute(
    onClickSignIn: (isSignUp: Boolean) -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    ObserveAsEvents(viewModel.effect) { effect ->
        when (effect) {
            OnboardingEffect.NavigateAuthSignUp -> {
                onClickSignIn(true)
            }

            OnboardingEffect.NavigateAuthSignUp -> {
                onClickSignIn(false)
            }

            else -> Unit
        }
    }

    OnboardingRegistrationScreen(onIntent = viewModel::onIntent)
}

@Composable
fun OnboardingRegistrationScreen(
    onIntent: (OnboardingIntent) -> Unit,
    modifier: Modifier = Modifier,
    style: RegistrationStyle = RegistrationDefault.style
) {
    ImageActionLayout(
        imagePainter = painterResource(R.drawable.img_onboarding_registration),
        actionText = stringResource(R.string.skip),
        onActionClick = { onIntent(OnboardingIntent.SkipOnboardingIntro) },
        modifier = modifier,
        style = ImageActionDefaults.style.copy(bottomSpacing = style.bottomSpacing)
    ) {

        Text(
            text = stringResource(R.string.create_your_account),
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground
        )

        Text(
            text = stringResource(R.string.sign_in),
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = style.buttonPaddingTop)
                .bounceClick {
                    onIntent(OnboardingIntent.SelectSignIn)
                }
                .border(
                    width = style.buttonStrokeWidth,
                    color = MaterialTheme.colorScheme.primary,
                    shape = style.buttonShape
                )
                .padding(style.buttonPadding),
        )

        Text(
            text = stringResource(R.string.sign_up),
            style = MaterialTheme.typography.headlineSmall,
            color = AppColor.textWhite,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .bounceClick {
                    onIntent(OnboardingIntent.SelectSignUp)
                }
                .background(
                    color = MaterialTheme.colorScheme.primary,
                    shape = style.buttonShape
                )
                .padding(style.buttonPadding),
            )
    }
}


@ThemePreviews
@Composable
fun PreviewOnboardingRegistrationScreen() {
    TradingPreview {
        OnboardingRegistrationScreen(
            onIntent = { }
        )
    }
}
