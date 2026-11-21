package com.upper.trading.presentation.features.auth.signin.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.upper.trading.R
import com.upper.trading.core.utils.ThemePreviews
import com.upper.trading.core.utils.TradingPreview
import com.upper.trading.presentation.components.button.AppSocialButton
import com.upper.trading.presentation.features.auth.signin.AuthSignInIntent

@Immutable
data class AuthSocialSectionStyle(
    val dividerThickness: Dp = 2.dp,
    val dividerTextHorizontalPadding: Dp = 16.dp,
    val googleButtonPaddingTop: Dp = 24.dp,
    val appleButtonPaddingTop: Dp = 16.dp,
)

object AuthSocialSectionDefaults {
    val style: AuthSocialSectionStyle = AuthSocialSectionStyle()
}

@Composable
fun AuthSocialSection(
    onIntent: (AuthSignInIntent) -> Unit,
    modifier: Modifier = Modifier,
    style: AuthSocialSectionStyle = AuthSocialSectionDefaults.style
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier,
            verticalAlignment = Alignment.CenterVertically
        ) {
            HorizontalDivider(
                modifier = Modifier.weight(1f),
                thickness = style.dividerThickness,
                color = MaterialTheme.colorScheme.outline,
            )

            Text(
                text = stringResource(R.string.or),
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(horizontal = style.dividerTextHorizontalPadding)
            )

            HorizontalDivider(
                modifier = Modifier.weight(1f),
                thickness = style.dividerThickness,
                color = MaterialTheme.colorScheme.outline,
            )
        }

        AppSocialButton(
            text = stringResource(R.string.sign_in_with_google),
            icon = painterResource(R.drawable.ic_google),
            onClick = {
                onIntent(AuthSignInIntent.SignInWithGoogle)
            },
            modifier = Modifier.padding(top = style.googleButtonPaddingTop)
        )

        AppSocialButton(
            text = stringResource(R.string.sign_in_with_apple),
            icon = painterResource(R.drawable.ic_apple),
            onClick = {
                onIntent(AuthSignInIntent.SignInWithApple)
            },
            modifier = Modifier.padding(top = style.appleButtonPaddingTop)
        )
    }
}

@ThemePreviews
@Composable
fun PreviewAuthSocialSection() {
    TradingPreview {
        AuthSocialSection(
            onIntent = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}
