package com.upper.trading.presentation.components.snackbar

import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Chat
import androidx.compose.material.icons.automirrored.rounded.ExitToApp
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Error
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.upper.trading.core.utils.ThemePreviews
import com.upper.trading.core.utils.TradingPreview
import com.upper.trading.core.utils.UiText
import com.upper.trading.presentation.theme.AppColor
import com.upper.trading.presentation.theme.blackShades10
import com.upper.trading.presentation.theme.blackShades4
import com.upper.trading.presentation.theme.blackShades7
import com.upper.trading.presentation.theme.blueShades6
import com.upper.trading.presentation.theme.darkBlueShades5
import com.upper.trading.presentation.theme.green05
import com.upper.trading.presentation.theme.green2
import com.upper.trading.presentation.theme.green5
import com.upper.trading.presentation.theme.green6
import com.upper.trading.presentation.theme.green7
import com.upper.trading.presentation.theme.purple05
import com.upper.trading.presentation.theme.purple2
import com.upper.trading.presentation.theme.purple4
import com.upper.trading.presentation.theme.purple5
import com.upper.trading.presentation.theme.purple6
import com.upper.trading.presentation.theme.purple7
import com.upper.trading.presentation.theme.red05
import com.upper.trading.presentation.theme.red2
import com.upper.trading.presentation.theme.red5
import com.upper.trading.presentation.theme.red7

@Immutable
data class AppSnackbarStyle(
    val cornerRadius: Dp = 12.dp,
    val borderWidth: Dp = 1.dp,
    val elevation: Dp = 8.dp,
    val shadowColor: Color = Color.Black.copy(alpha = 0.15f),
    val horizontalPadding: Dp = 12.dp,
    val verticalPaddingSingleLine: Dp = 14.dp,
    val verticalPaddingWithSubtitle: Dp = 12.dp,
    val leadingSpacing: Dp = 12.dp,
    val textSpacing: Dp = 4.dp,
    val iconSize: Dp = 24.dp,
    val spinnerSize: Dp = 20.dp,
    val spinnerStrokeWidth: Dp = 2.5.dp,
    val closeIconSize: Dp = 20.dp,
    val actionPaddingHorizontal: Dp = 8.dp,
    val actionPaddingVertical: Dp = 4.dp,
)

object AppSnackbarDefaults {
    val style: AppSnackbarStyle = AppSnackbarStyle()
}

@Composable
fun AppCustomSnackbar(
    data: AppSnackbarData,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    style: AppSnackbarStyle = AppSnackbarDefaults.style,
    isDark: Boolean = isSystemInDarkTheme()
) {
    val (bgColor, borderColor, accentColor) = when (data.type) {
        SnackbarType.SUCCESS -> if (isDark) {
            Triple(darkBlueShades5, green7, green5)
        } else {
            Triple(green05, green2, green6)
        }

        SnackbarType.ERROR -> if (isDark) {
            Triple(darkBlueShades5, red7, red5)
        } else {
            Triple(red05, red2, red5)
        }

        SnackbarType.LOADING -> if (isDark) {
            Triple(darkBlueShades5, AppColor.borderBlueGray, purple4)
        } else {
            Triple(Color.White, blueShades6, purple5)
        }

        SnackbarType.INFO -> if (isDark) {
            Triple(darkBlueShades5, purple7, purple5)
        } else {
            Triple(purple05, purple2, purple6)
        }
    }

    val titleColor = if (isDark) Color.White else blackShades10
    val subtitleColor = if (isDark) blackShades4 else blackShades7
    val shape = RoundedCornerShape(style.cornerRadius)
    val paddingVertical = if (data.message == null) style.verticalPaddingSingleLine else style.verticalPaddingWithSubtitle

    Row(
        modifier = modifier
            .fillMaxWidth()
            .shadow(elevation = style.elevation, shape = shape, spotColor = style.shadowColor)
            .background(color = bgColor, shape = shape)
            .border(width = style.borderWidth, color = borderColor, shape = shape)
            .padding(horizontal = style.horizontalPadding, vertical = paddingVertical),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.size(style.iconSize), contentAlignment = Alignment.Center) {
            val customIcon = data.icon
            if (customIcon != null) {
                Icon(
                    imageVector = customIcon,
                    contentDescription = null,
                    tint = accentColor
                )
            } else {
                when (data.type) {
                    SnackbarType.SUCCESS -> Icon(
                        imageVector = Icons.Rounded.CheckCircle,
                        contentDescription = "Success",
                        tint = accentColor
                    )

                    SnackbarType.ERROR -> {
                        Icon(
                            imageVector = Icons.Rounded.Error,
                            contentDescription = "Error",
                            tint = accentColor
                        )
                    }

                    SnackbarType.LOADING -> {
                        CircularProgressIndicator(
                            modifier = Modifier.size(style.spinnerSize),
                            color = accentColor,
                            strokeWidth = style.spinnerStrokeWidth
                        )
                    }

                    SnackbarType.INFO -> Icon(
                        imageVector = Icons.AutoMirrored.Rounded.Chat,
                        contentDescription = "Info",
                        tint = accentColor
                    )
                }
            }
        }

        Spacer(modifier = Modifier.width(style.leadingSpacing))

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(style.textSpacing)
        ) {
            Text(
                text = data.title.asString(),
                style = MaterialTheme.typography.titleSmall,
                color = titleColor,
                maxLines = 1,
                softWrap = false,
                modifier = Modifier.basicMarquee()
            )
            if (data.message != null) {
                Text(
                    text = data.message.asString(),
                    style = MaterialTheme.typography.bodySmall,
                    color = subtitleColor,
                    maxLines = 1,
                    softWrap = false,
                    modifier = Modifier.basicMarquee()
                )
            }
        }

        if (data.actionLabel != null && data.onAction != null) {
            Text(
                text = data.actionLabel.asString(),
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = accentColor,
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable { data.onAction.invoke(); onDismiss() }
                    .padding(horizontal = style.actionPaddingHorizontal, vertical = style.actionPaddingVertical)
                    .basicMarquee()
            )
        } else if (data.type != SnackbarType.INFO) {
            Icon(
                imageVector = Icons.Rounded.Close,
                contentDescription = "Close",
                tint = accentColor,
                modifier = Modifier
                    .size(style.closeIconSize)
                    .clip(CircleShape)
                    .clickable(onClick = onDismiss)
            )
        }
    }
}

@ThemePreviews
@Composable
fun PreviewAppCustomSnackbar() {
    TradingPreview {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(16.dp)
        ) {
            AppCustomSnackbar(
                data = AppSnackbarData(
                    type = SnackbarType.SUCCESS,
                    title = UiText.DynamicString("Order placed successfully"),
                ),
                onDismiss = {

                }
            )

            AppCustomSnackbar(
                data = AppSnackbarData(
                    type = SnackbarType.SUCCESS,
                    title = UiText.DynamicString("Order placed successfully"),
                    message = UiText.DynamicString("Your order is now open"),
                ),
                onDismiss = {

                }
            )

            AppCustomSnackbar(
                data = AppSnackbarData(
                    type = SnackbarType.ERROR,
                    title = UiText.DynamicString("Order could not be placed"),
                    onAction = {}
                ),
                onDismiss = {

                },
            )

            AppCustomSnackbar(
                data = AppSnackbarData(
                    type = SnackbarType.ERROR,
                    title = UiText.DynamicString("Order could not be placed"),
                    message = UiText.DynamicString("Check your connection and try again"),
                    actionLabel = UiText.DynamicString("Retry"),
                    onAction = {}
                ),
                onDismiss = {

                },
            )

            AppCustomSnackbar(
                data = AppSnackbarData(
                    type = SnackbarType.LOADING,
                    title = UiText.DynamicString("This may take a few seconds."),
                ),
                onDismiss = {

                }
            )

            AppCustomSnackbar(
                data = AppSnackbarData(
                    type = SnackbarType.LOADING,
                    title = UiText.DynamicString("Placing your order."),
                    message = UiText.DynamicString("This may take a few seconds."),
                ),
                onDismiss = {

                }
            )

            AppCustomSnackbar(
                data = AppSnackbarData(
                    type = SnackbarType.INFO,
                    title = UiText.DynamicString("Press back again to exit"),
                    icon = Icons.AutoMirrored.Rounded.ExitToApp
                ),
                onDismiss = {

                }
            )
        }
    }
}

