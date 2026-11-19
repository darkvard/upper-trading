package com.upper.trading.core.utils

import android.os.SystemClock
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.Role

@Composable
fun Modifier.bounceClick(
    scaleDown: Float = 0.95f,
    throttleTimeMs: Long = 600L,
    onCLick: () -> Unit
): Modifier {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) scaleDown else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "bounce_scale"
    )

    var lastClickTime by remember { mutableLongStateOf(0L) }
    return this
        .graphicsLayer {
            scaleX = scale
            scaleY = scale
        }
        .clickable(
            interactionSource = interactionSource,
            indication = null,
            onClick = {
                val currentTIme = SystemClock.uptimeMillis()
                if (currentTIme - lastClickTime >= throttleTimeMs) {
                    lastClickTime = currentTIme
                    onCLick()
                }
            }
        )
}

/**
 * Click modifier that throttles rapid clicks to prevent multiple triggers (anti-spam).
 * Does not apply any bounce/scale animation.
 *
 * @param throttleTimeMs Minimum interval between consecutive clicks (default 600ms)
 * @param hasRipple Whether to show ripple indication on click (default false)
 * @param role Optional accessibility role
 * @param onClick Callback triggered on valid click
 */
@Composable
fun Modifier.throttleClick(
    throttleTimeMs: Long = 600L,
    hasRipple: Boolean = false,
    role: Role? = null,
    onClick: () -> Unit
): Modifier {
    var lastClickTime by remember { mutableLongStateOf(0L) }
    val interactionSource = remember { MutableInteractionSource() }

    return this.clickable(
        interactionSource = interactionSource,
        indication = if (hasRipple) ripple() else null,
        role = role,
        onClick = {
            val currentTime = SystemClock.uptimeMillis()
            if (currentTime - lastClickTime >= throttleTimeMs) {
                lastClickTime = currentTime
                onClick()
            }
        }
    )
}
