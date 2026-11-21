package com.upper.trading.presentation.features.auth.signin.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.upper.trading.R
import com.upper.trading.core.utils.ThemePreviews
import com.upper.trading.core.utils.TradingPreview
import com.upper.trading.core.utils.bounceClick
import com.upper.trading.presentation.features.auth.signin.AuthSignInIntent

@Immutable
data class AuthSignInFingerprintStyle(
    val itemSpacing: Dp = 16.dp,
    val buttonSize: Dp = 48.dp,
    val buttonCornerRadius: Dp = 4.dp,
    val buttonShape: Shape = RoundedCornerShape(buttonCornerRadius),
)

object AuthSignInFingerprintDefaults {
    val style: AuthSignInFingerprintStyle = AuthSignInFingerprintStyle()
}

@Composable
fun AuthSignInFingerprint(
    onIntent: (AuthSignInIntent) -> Unit,
    modifier: Modifier = Modifier,
    style: AuthSignInFingerprintStyle = AuthSignInFingerprintDefaults.style
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(style.itemSpacing),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(style.buttonSize)
                .background(
                    color = MaterialTheme.colorScheme.primary,
                    shape = style.buttonShape
                )
                .bounceClick {
                    onIntent(AuthSignInIntent.SignInWithBiometric)
                },
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(R.drawable.ic_fingerprint),
                contentDescription = null,
            )
        }

        Text(
            text = stringResource(R.string.use_fingerprint_instead),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@ThemePreviews
@Composable
fun PreviewAuthSignInFingerprint() {
    TradingPreview {
        AuthSignInFingerprint(onIntent = {})
    }
}
