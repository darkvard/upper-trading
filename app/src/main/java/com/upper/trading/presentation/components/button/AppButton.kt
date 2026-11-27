package com.upper.trading.presentation.components.button

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.upper.trading.R
import com.upper.trading.core.utils.ThemePreviews
import com.upper.trading.core.utils.TradingPreview
import com.upper.trading.core.utils.bounceClick
import com.upper.trading.core.utils.scaleClick
import com.upper.trading.presentation.theme.AppColor

@Composable
fun AppPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    height: Dp = 40.dp,
    color: Color = AppColor.primarySurface,
    shape: Shape = RoundedCornerShape(4.dp),
    textStyle: TextStyle = MaterialTheme.typography.titleLarge,
    isThrottled: Boolean = true
) {
    val isClickable = enabled && !isLoading
    val clickModifier = if (isThrottled) {
        Modifier.bounceClick(enabled = isClickable) { onClick() }
    } else {
        Modifier.scaleClick { if (isClickable) onClick() }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .then(clickModifier)
            .alpha(if (isClickable) 1f else 0.45f)
            .background(
                color = color,
                shape = shape
            ),
        contentAlignment = Alignment.Center
    ) {
        AnimatedContent(
            targetState = isLoading,
            label = "button_loading_transition"
        ) { loading ->
            if (loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = Color.White,
                    strokeWidth = 2.dp
                )
            } else {
                Text(
                    text = text,
                    color = Color.White,
                    style = textStyle
                )
            }
        }
    }
}

@Composable
fun AppSocialButton(
    text: String,
    icon: Painter,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    height: Dp = 40.dp,
    color: Color = MaterialTheme.colorScheme.surface,
    shape: Shape = RoundedCornerShape(4.dp),
    style: TextStyle = MaterialTheme.typography.titleMedium,
    isThrottled: Boolean = true
) {
    val clickModifier = if (isThrottled) {
        Modifier.bounceClick(onCLick = onClick)
    } else {
        Modifier.scaleClick(onClick = onClick)
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .then(clickModifier)
            .background(color = color, shape = shape),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            painter = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.padding(horizontal = 4.dp))

        Text(
            text = text,
            style = style,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@ThemePreviews
@Composable
fun PreviewAppPrimaryButton() {
    TradingPreview {
        Column(
            modifier = Modifier.padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            AppPrimaryButton(
                text = "Next", onClick = {

                }
            )

            AppSocialButton(
                text = "Google",
                icon = painterResource(R.drawable.ic_google),
                onClick = {

                }
            )

            AppSocialButton(
                text = "Apple",
                icon = painterResource(R.drawable.ic_apple),
                onClick = {

                }
            )
        }

    }
}