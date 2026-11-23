package com.upper.trading.core.utils

import android.os.SystemClock
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.Role
import kotlinx.coroutines.launch

@Composable
fun Modifier.bounceClick(
    scaleDown: Float = 0.95f,
    throttleTimeMs: Long = 600L,
    enabled: Boolean = true,
    onCLick: () -> Unit
): Modifier {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val coroutineScope = rememberCoroutineScope()
    val scale = remember { Animatable(1f) }

    LaunchedEffect(isPressed, enabled) {
        if (enabled && isPressed) {
            scale.animateTo(
                targetValue = scaleDown,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium
                )
            )
        } else {
            scale.animateTo(
                targetValue = 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium
                )
            )
        }
    }

    var lastClickTime by remember { mutableLongStateOf(0L) }
    return this
        .graphicsLayer {
            scaleX = scale.value
            scaleY = scale.value
        }
        .clickable(
            enabled = enabled,
            interactionSource = interactionSource,
            indication = null,
            onClick = {
                val currentTime = SystemClock.uptimeMillis()
                if (currentTime - lastClickTime >= throttleTimeMs) {
                    lastClickTime = currentTime
                    coroutineScope.launch {
                        // In scrollable containers (e.g. verticalScroll, LazyColumn), Compose delays
                        // emitting PressInteraction.Press by ~100ms (tap timeout) to distinguish
                        // between a tap and a scroll drag. For rapid clicks or mouse taps (<100ms),
                        // onClick fires while isPressed never had time to trigger the scale animation.
                        // If scale has not decreased (>= 0.98f), fire a synthetic quick bounce cycle
                        // (scaleDown -> 1f) so fast taps inside scroll containers remain snappy and responsive.
                        if (scale.value >= 0.98f) {
                            scale.animateTo(
                                scaleDown,
                                spring(
                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                    stiffness = Spring.StiffnessHigh
                                )
                            )
                            scale.animateTo(
                                1f,
                                spring(
                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                    stiffness = Spring.StiffnessMedium
                                )
                            )
                        }
                    }
                    onCLick()
                }
            }
        )
}

/**
 * Click modifier that applies a smooth scale bounce animation when pressed,
 * without any throttling, allowing rapid/fast clicking.
 */
@Composable
fun Modifier.scaleClick(
    scaleDown: Float = 0.95f,
    onClick: () -> Unit
): Modifier {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val coroutineScope = rememberCoroutineScope()
    val scale = remember { Animatable(1f) }

    LaunchedEffect(isPressed) {
        if (isPressed) {
            scale.animateTo(
                targetValue = scaleDown,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium
                )
            )
        } else {
            scale.animateTo(
                targetValue = 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium
                )
            )
        }
    }

    return this
        .graphicsLayer {
            scaleX = scale.value
            scaleY = scale.value
        }
        .clickable(
            interactionSource = interactionSource,
            indication = null,
            onClick = {
                coroutineScope.launch {
                    // In scrollable containers (e.g. verticalScroll, LazyColumn), Compose delays
                    // emitting PressInteraction.Press by ~100ms (tap timeout) to distinguish
                    // between a tap and a scroll drag. For rapid clicks or mouse taps (<100ms),
                    // onClick fires while isPressed never had time to trigger the scale animation.
                    // If scale has not decreased (>= 0.98f), fire a synthetic quick bounce cycle
                    // (scaleDown -> 1f) so fast taps inside scroll containers remain snappy and responsive.
                    if (scale.value >= 0.98f) {
                        scale.animateTo(
                            scaleDown,
                            spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessHigh
                            )
                        )
                        scale.animateTo(
                            1f,
                            spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessMedium
                            )
                        )
                    }
                }
                onClick()
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
