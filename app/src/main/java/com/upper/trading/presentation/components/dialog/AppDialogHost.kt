package com.upper.trading.presentation.components.dialog

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Fingerprint
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.upper.trading.R
import com.upper.trading.core.utils.ThemePreviews
import com.upper.trading.core.utils.TradingPreview
import com.upper.trading.core.utils.UiText
import com.upper.trading.core.utils.bounceClick
import com.upper.trading.presentation.theme.AppColor
import com.upper.trading.presentation.theme.blueShades5
import com.upper.trading.presentation.theme.blueShades6
import com.upper.trading.presentation.theme.darkBlueShades2
import kotlinx.collections.immutable.toImmutableList

@Immutable
data class AppDialogStyle(
    val shadowElevation: Dp = 6.dp,
    val cornerRadius: Dp = 16.dp,
    val borderWidth: Dp = 1.dp,
    val padding: Dp = 24.dp,
    val iconSize: Dp = 24.dp,
    val iconBackgroundSize: Dp = 48.dp,
    val circleShape: Shape = CircleShape,
    val stepIndexSize: Dp = 20.dp,
    val stepSpacing: Dp = 8.dp,
    val stepLeadingSpacing: Dp = 8.dp,
    val buttonHeight: Dp = 44.dp,
    val buttonShape: Shape = RoundedCornerShape(8.dp),
    val buttonSpacing: Dp = 12.dp,
    val buttonPadding: Dp = 8.dp,
    val spacingIconToTitle: Dp = 16.dp,
    val spacingTitleToDescription: Dp = 8.dp,
    val spacingDescriptionToSteps: Dp = 12.dp,
    val spacingContentToActions: Dp = 24.dp,
)

object AppDialogHostDefaults {
    val style: AppDialogStyle = AppDialogStyle()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppDialogHost(
    state: AppDialogHostState,
    modifier: Modifier = Modifier,
    onDismissClick: (() -> Unit)? = null,
    onConfirmClick: (() -> Unit)? = null,
    dismissOnClickOutside: Boolean = true,
    style: AppDialogStyle = AppDialogHostDefaults.style,
    primaryColor: Color = MaterialTheme.colorScheme.primary,
    isDark: Boolean = isSystemInDarkTheme()
) {
    val currentData = state.currentData ?: return

    val actualPrimary = currentData.primaryColor ?: primaryColor
    val hasDismissButton = currentData.dismissName != null
    val canDismiss = dismissOnClickOutside && hasDismissButton

    val handleDismiss = onDismissClick ?: {
        currentData.onDismiss?.invoke()
        state.dismiss()
    }
    val handleConfirm = onConfirmClick ?: {
        currentData.onConfirm?.invoke()
        state.dismiss()
    }

    val resolvedIcon = currentData.icon ?: when (currentData.type) {
        DialogType.CONFIRMATION -> Icons.Rounded.Info
        DialogType.GUIDE -> Icons.AutoMirrored.Filled.Help
    }

    // Dismiss button styling (fixed high-contrast design tokens)
    val dismissBg = if (isDark) darkBlueShades2 else blueShades5
    val dismissBorder = if (isDark) AppColor.borderBlueGray else blueShades6
    val dismissText = MaterialTheme.colorScheme.onSurface

    BasicAlertDialog(
        onDismissRequest = {
            if (canDismiss) {
                handleDismiss()
            }
        },
        properties = DialogProperties(
            dismissOnBackPress = canDismiss,
            dismissOnClickOutside = canDismiss,
            usePlatformDefaultWidth = false
        )
    ) {
        Surface(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            shape = RoundedCornerShape(style.cornerRadius),
            color = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface,
            shadowElevation = style.shadowElevation,
            border = BorderStroke(style.borderWidth, actualPrimary.copy(alpha = 0.45f))
        ) {
            Column(
                modifier = Modifier.padding(style.padding)
            ) {
                Box(
                    modifier = Modifier
                        .size(style.iconBackgroundSize)
                        .clip(style.circleShape)
                        .background(actualPrimary.copy(alpha = 0.15f), shape = style.circleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = resolvedIcon,
                        contentDescription = null,
                        tint = actualPrimary,
                        modifier = Modifier.size(style.iconSize)
                    )
                }

                currentData.title?.let { titleText ->
                    Spacer(modifier = Modifier.height(style.spacingIconToTitle))
                    Text(
                        text = titleText.asString(),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                currentData.description?.let { descText ->
                    Spacer(modifier = Modifier.height(style.spacingTitleToDescription))
                    Text(
                        text = descText.asString(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (currentData.steps.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(style.spacingDescriptionToSteps))

                    currentData.steps.forEachIndexed { index, uiText ->
                        TipItem(
                            index = index + 1,
                            text = uiText.asString(),
                            indexColor = actualPrimary,
                            style = style,
                            modifier = Modifier.then(
                                if (index < currentData.steps.size - 1) {
                                    Modifier.padding(bottom = style.stepSpacing)
                                } else {
                                    Modifier
                                }
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(style.spacingContentToActions))

                // Action Buttons Row with Dismiss check
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(style.buttonSpacing)
                ) {
                    if (hasDismissButton && currentData.dismissName != null) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(style.buttonHeight)
                                .bounceClick(onCLick = handleDismiss)
                                .clip(shape = style.buttonShape)
                                .background(
                                    color = dismissBg,
                                    shape = style.buttonShape
                                )
                                .border(
                                    BorderStroke(style.borderWidth, dismissBorder),
                                    style.buttonShape
                                )
                                .padding(style.buttonPadding),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = currentData.dismissName.asString(),
                                style = MaterialTheme.typography.titleSmall,
                                color = dismissText
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .then(
                                if (hasDismissButton) Modifier.weight(1f) else Modifier.fillMaxWidth()
                            )
                            .height(style.buttonHeight)
                            .bounceClick(onCLick = handleConfirm)
                            .clip(shape = style.buttonShape)
                            .background(
                                color = actualPrimary,
                                shape = style.buttonShape
                            )
                            .padding(style.buttonPadding),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = currentData.confirmName.asString(),
                            style = MaterialTheme.typography.titleSmall,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TipItem(
    index: Int,
    text: String,
    modifier: Modifier = Modifier,
    indexColor: Color = MaterialTheme.colorScheme.primary,
    style: AppDialogStyle = AppDialogHostDefaults.style,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(style.stepIndexSize)
                .clip(style.circleShape)
                .background(indexColor.copy(alpha = 0.15f), shape = style.circleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "$index",
                style = MaterialTheme.typography.labelSmall,
                color = indexColor,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.width(style.stepLeadingSpacing))

        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f)
        )
    }
}

@ThemePreviews
@Composable
fun PreviewAppDialogConfirmHost() {
    TradingPreview {
        val confirmHostState = remember {
            AppDialogHostState(
                initialData = AppDialogData(
                    title = UiText.StringResource(R.string.biometric_enroll_dialog_title),
                    description = UiText.StringResource(R.string.biometric_enroll_dialog_message),
                    icon = Icons.Rounded.CheckCircle,
                    type = DialogType.CONFIRMATION,
                )
            )
        }

        Surface(modifier = Modifier.fillMaxSize()) {
            AppDialogHost(state = confirmHostState)
        }
    }
}

@ThemePreviews
@Composable
fun PreviewAppDialogGuide() {
    TradingPreview {
        val guideHostState = remember {
            AppDialogHostState(
                initialData = AppDialogData(
                    title = UiText.StringResource(R.string.biometric_enroll_dialog_title),
                    description = UiText.StringResource(R.string.biometric_enroll_dialog_message),
                    steps = listOf(
                        UiText.StringResource(R.string.biometric_guide_step_1),
                        UiText.StringResource(R.string.biometric_guide_step_2),
                        UiText.StringResource(R.string.biometric_guide_step_3)
                    ).toImmutableList(),
                    icon = Icons.Rounded.Fingerprint,
                    type = DialogType.GUIDE,
                    confirmName = UiText.StringResource(R.string.biometric_enroll_dialog_confirm),
                    dismissName = UiText.StringResource(R.string.cancel)
                )
            )
        }

        Surface(modifier = Modifier.fillMaxSize()) {
            AppDialogHost(state = guideHostState)
        }
    }
}
