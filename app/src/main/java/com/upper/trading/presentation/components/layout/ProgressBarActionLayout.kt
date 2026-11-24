package com.upper.trading.presentation.components.layout

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.upper.trading.R
import com.upper.trading.core.utils.ThemePreviews
import com.upper.trading.core.utils.TradingPreview
import com.upper.trading.core.utils.bounceClick

data class ProgressBarStyle(
    val touchSize: Dp = 40.dp,
    val iconPadding: Dp = 8.dp,
    val padding: Dp = 8.dp,
    val progressBarHeight: Dp = 2.dp,
)

data class ProgressBarState(
    val current: Float = 1f,
    val total: Float = 9f,
    val from: Float = (current - 1f).coerceAtLeast(0f),
)

@Stable
class FlowProgressState(initialFraction: Float = 0f) {
    val progress = Animatable(initialFraction)
}

val LocalFlowProgress = staticCompositionLocalOf { FlowProgressState() }

object ProgressBarActionDefaults {
    val style = ProgressBarStyle()
}

@Composable
fun ProgressBarActionLayout(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    onCloseClick: (() -> Unit)? = null,
    progressBarState: ProgressBarState? = null,
    style: ProgressBarStyle = ProgressBarActionDefaults.style,
    bodyContent: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(style.padding),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_arrow_long_left),
                contentDescription = "Back",
                modifier = Modifier
                    .size(style.touchSize)
                    .padding(style.iconPadding)
                    .bounceClick(onCLick = onBackClick)
            )

            if (onCloseClick != null && progressBarState != null) {
                Icon(
                    painter = painterResource(R.drawable.ic_close),
                    contentDescription = "Close",
                    modifier = Modifier
                        .size(style.touchSize)
                        .padding(style.iconPadding)
                        .bounceClick(onCLick = onCloseClick)
                )
            }
        }

        if (progressBarState != null) {
            AnimateProgressBar(state = progressBarState, style = style)
        }

        bodyContent()
    }
}

@Composable
fun AnimateProgressBar(
    state: ProgressBarState,
    modifier: Modifier = Modifier,
    style: ProgressBarStyle
) {
    val safeTotal = if (state.total <= 0f) 1f else state.total
    val targetFraction = (state.current / safeTotal).coerceIn(0f, 1f)
    val flowProgress = LocalFlowProgress.current

    LaunchedEffect(targetFraction) {
        if (flowProgress.progress.value == 0f && state.from > 0f) {
            flowProgress.progress.snapTo((state.from / safeTotal).coerceIn(0f, 1f))
        }
        flowProgress.progress.animateTo(
            targetValue = targetFraction,
            animationSpec = tween(
                durationMillis = 500,
                easing = FastOutSlowInEasing
            )
        )
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(style.progressBarHeight)
            .background(MaterialTheme.colorScheme.outline)
    ) {
        if (flowProgress.progress.value > 0f) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(flowProgress.progress.value.coerceIn(0f, 1f))
                    .height(style.progressBarHeight)
                    .background(MaterialTheme.colorScheme.primary)
            )
        }
    }
}

@ThemePreviews
@Composable
fun PreviewProgressBarActionLayout_WithProgress() {
    TradingPreview {
        ProgressBarActionLayout(
            progressBarState = ProgressBarState(current = 1f, total = 9f),
            onBackClick = {},
            onCloseClick = {}
        ) {
        }
    }
}

@ThemePreviews
@Composable
fun PreviewProgressBarActionLayout_WithoutProgress() {
    TradingPreview {
        ProgressBarActionLayout(
            progressBarState = null,
            onBackClick = {}
        ) {
        }
    }
}
