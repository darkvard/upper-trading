package com.upper.trading.presentation.components.tab

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.upper.trading.core.utils.ThemePreviews
import com.upper.trading.core.utils.TradingPreview
import com.upper.trading.presentation.theme.AppColor
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

data class AppSegmentedControlStyle(
    val height: Dp = 44.dp,
    val padding: Dp = 4.dp,
    val containerCornerRadius: Dp = 4.dp,
    val borderStroke: Dp = 1.dp
)

object AppSegmentedDefault {
    val style: AppSegmentedControlStyle = AppSegmentedControlStyle()
}

@Composable
fun <T> AppSegmentedControl(
    items: ImmutableList<T>,
    selectedItem: T,
    onItemSelected: (T) -> Unit,
    modifier: Modifier = Modifier,
    itemLabel: @Composable (T) -> String = { it.toString() },
    style: AppSegmentedControlStyle = AppSegmentedDefault.style,
    containerColor: Color = MaterialTheme.colorScheme.surface,
    selectedColor: Color = MaterialTheme.colorScheme.primary,
    itemBorderColor: Color = AppColor.borderBlueGray,
    selectedTextColor: Color = MaterialTheme.colorScheme.onPrimary,
    unselectedTextColor: Color = MaterialTheme.colorScheme.onSurfaceVariant
) {
    val selectedIndex = items.indexOf(selectedItem).coerceAtLeast(0)

    // ROOT CONTAINER: Applies container background, corner clipping, and inner padding (No outer border)
    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .height(style.height)
            .clip(RoundedCornerShape(style.containerCornerRadius))
            .background(containerColor)
            .padding(style.padding)
    ) {
        val totalWidth = maxWidth
        val segmentWidth = totalWidth / items.size

        val indicatorOffset by animateDpAsState(
            targetValue = segmentWidth * selectedIndex,
            animationSpec = spring(stiffness = 650f, dampingRatio = 0.92f),
            label = "SegmentIndicatorAnimation"
        )

        // Layer 1: Outer frame with single vertical divider between items (avoids duplicate borders)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .border(BorderStroke(style.borderStroke, itemBorderColor))
        ) {
            Row(modifier = Modifier.fillMaxSize()) {
                items.forEachIndexed { index, _ ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                    ) {
                        if (index < items.lastIndex) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.CenterEnd)
                                    .fillMaxHeight()
                                    .width(style.borderStroke)
                                    .background(itemBorderColor)
                            )
                        }
                    }
                }
            }
        }

        // Layer 2: Animated sliding indicator for the selected tab
        Box(
            modifier = Modifier
                .offset(x = indicatorOffset)
                .width(segmentWidth)
                .fillMaxHeight()
                .background(selectedColor)
        )

        // Layer 3: Interactive labels with smooth animated text color transition
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEach { item ->
                val isSelected = item == selectedItem
                val animatedTextColor by animateColorAsState(
                    targetValue = if (isSelected) selectedTextColor else unselectedTextColor,
                    animationSpec = tween(durationMillis = 200),
                    label = "SegmentTextColor"
                )

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { onItemSelected(item) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = itemLabel(item),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                        color = animatedTextColor,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}


@ThemePreviews
@Composable
fun PreviewAppSegmentedControl() {
    val temp = persistentListOf("Sign 1", "Sign 2", "Sign 3", "Sign 4", "Sign 5")
    var selectedItem by remember { mutableStateOf(temp.first()) }

    TradingPreview {
        AppSegmentedControl(
            items = temp,
            selectedItem = selectedItem,
            onItemSelected = { selectedItem = it }
        )
    }
}