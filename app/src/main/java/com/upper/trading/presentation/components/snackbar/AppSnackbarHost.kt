package com.upper.trading.presentation.components.snackbar

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.upper.trading.core.utils.ThemePreviews
import com.upper.trading.core.utils.TradingPreview
import com.upper.trading.core.utils.UiText

object AppSnackbarAnimationDefaults {
    const val ENTER_DURATION_MS = 300
    const val ENTER_FADE_DURATION_MS = 220
    const val ENTER_OFFSET_MULTIPLIER = 1.5f
    const val ENTER_SCALE = 0.96f

    const val EXIT_DURATION_MS = 260
    const val EXIT_FADE_DURATION_MS = 180
    const val EXIT_FADE_DELAY_MS = 80
    const val EXIT_OFFSET_MULTIPLIER = 2.0f
    const val EXIT_SCALE = 0.96f

    const val DEFAULT_DISPLAY_DURATION_MS = 3500L
}

@Composable
fun AppSnackbarHost(
    hostState: AppSnackbarHostState,
    modifier: Modifier = Modifier
) {
    val current = hostState.currentData
    var lastData by remember { mutableStateOf<AppSnackbarData?>(null) }

    if (current != null) {
        lastData = current
    }

    AnimatedVisibility(
        visible = current != null,
        enter = slideInVertically(
            initialOffsetY = { fullHeight -> (fullHeight * AppSnackbarAnimationDefaults.ENTER_OFFSET_MULTIPLIER).toInt() },
            animationSpec = tween(
                durationMillis = AppSnackbarAnimationDefaults.ENTER_DURATION_MS,
                easing = LinearOutSlowInEasing
            )
        ) + fadeIn(
            animationSpec = tween(
                durationMillis = AppSnackbarAnimationDefaults.ENTER_FADE_DURATION_MS,
                easing = LinearEasing
            )
        ) + scaleIn(
            initialScale = AppSnackbarAnimationDefaults.ENTER_SCALE,
            animationSpec = tween(
                durationMillis = AppSnackbarAnimationDefaults.ENTER_DURATION_MS,
                easing = LinearOutSlowInEasing
            )
        ),
        exit = slideOutVertically(
            targetOffsetY = { fullHeight -> (fullHeight * AppSnackbarAnimationDefaults.EXIT_OFFSET_MULTIPLIER).toInt() },
            animationSpec = tween(
                durationMillis = AppSnackbarAnimationDefaults.EXIT_DURATION_MS,
                easing = FastOutLinearInEasing
            )
        ) + fadeOut(
            animationSpec = tween(
                durationMillis = AppSnackbarAnimationDefaults.EXIT_FADE_DURATION_MS,
                delayMillis = AppSnackbarAnimationDefaults.EXIT_FADE_DELAY_MS,
                easing = LinearEasing
            )
        ) + scaleOut(
            targetScale = AppSnackbarAnimationDefaults.EXIT_SCALE,
            animationSpec = tween(
                durationMillis = AppSnackbarAnimationDefaults.EXIT_DURATION_MS,
                easing = FastOutLinearInEasing
            )
        ),
        modifier = modifier
    ) {
        lastData?.let { data ->
            AppCustomSnackbar(
                data = data,
                onDismiss = {
                    hostState.dismiss()
                }
            )
        }
    }
}

@ThemePreviews
@Composable
fun PreviewAppSnackbarHost() {
    TradingPreview {
        val hostState = remember {
            AppSnackbarHostState(
                initialData = AppSnackbarData(
                    type = SnackbarType.SUCCESS,
                    title = UiText.DynamicString("Order placed successfully"),
                    message = UiText.DynamicString("Your order is now open.")
                )
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(androidx.compose.material3.MaterialTheme.colorScheme.background)
        ) {
            AppSnackbarHost(
                hostState = hostState,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(vertical = 32.dp, horizontal = 16.dp)
            )
        }
    }
}