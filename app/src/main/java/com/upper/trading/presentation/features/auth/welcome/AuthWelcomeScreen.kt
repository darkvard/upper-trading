package com.upper.trading.presentation.features.auth.welcome

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.upper.trading.R
import com.upper.trading.core.utils.ThemePreviews
import com.upper.trading.core.utils.TradingPreview
import com.upper.trading.presentation.components.button.AppPrimaryButton
import com.upper.trading.presentation.components.layout.ImageActionLayout

@Composable
fun AuthWelcomeRoute(
    onSkipClick: () -> Unit,
    onGetStartedClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    AuthWelcomeScreen(
        onSkipClick = onSkipClick,
        onGetStartedClick = onGetStartedClick,
        modifier = modifier
    )
}

@Composable
fun AuthWelcomeScreen(
    onSkipClick: () -> Unit,
    onGetStartedClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ImageActionLayout(
        imagePainter = painterResource(R.drawable.img_welcome),
        actionText = stringResource(R.string.skip),
        onActionClick = onSkipClick,
        modifier = modifier
    ) {
        Text(
            text = stringResource(R.string.account_created_success_title),
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )
        AppPrimaryButton(
            text = stringResource(R.string.get_started),
            onClick = onGetStartedClick,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@ThemePreviews
@Composable
private fun PreviewAuthWelcomeScreen() {
    TradingPreview {
        AuthWelcomeScreen(
            onSkipClick = {},
            onGetStartedClick = {}
        )
    }
}
