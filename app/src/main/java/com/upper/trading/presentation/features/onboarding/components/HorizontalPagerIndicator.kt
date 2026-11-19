package com.upper.trading.presentation.features.onboarding.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.upper.trading.core.utils.ThemePreviews
import com.upper.trading.core.utils.TradingPreview
import com.upper.trading.presentation.theme.AppColor

data class PagerIndicatorStyle(
    val activeWidth: Dp = 24.dp,
    val inactiveWidth: Dp = 48.dp,
    val height: Dp = 8.dp,
    val spacing: Dp = 12.dp,
    val activeColor: Color = AppColor.primarySurface,
    val inactiveColor: Color = AppColor.primarySurface.copy(alpha = 0.2f),
    val animationDuration: Int = 300
)

object PagerIndicatorDefault {
    val style: PagerIndicatorStyle = PagerIndicatorStyle()
}

@Composable
fun HorizontalPagerIndicator(
    pageCount: Int,
    currentPage: Int,
    modifier: Modifier = Modifier,
    style: PagerIndicatorStyle = PagerIndicatorDefault.style
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(style.spacing),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val page = if (currentPage >= pageCount) {
            pageCount - 1
        } else if (currentPage < 0) {
            0
        } else currentPage

        repeat(pageCount) { index ->
            val isSelected = index == page

            val width by animateDpAsState(
                targetValue = if (isSelected) style.activeWidth else style.inactiveWidth,
                animationSpec = tween(durationMillis = style.animationDuration),
                label = "indicator_width"
            )

            val color by animateColorAsState(
                targetValue = if (isSelected) style.activeColor else style.inactiveColor,
                animationSpec = tween(durationMillis = style.animationDuration),
                label = "indicator_color"
            )

            Box(
                modifier = Modifier
                    .width(width)
                    .height(style.height)
                    .background(
                        color = color,
                        shape = RoundedCornerShape(style.height / 2)
                    )
            )
        }
    }
}


@ThemePreviews
@Composable
fun PreviewHorizontalPagerIndicator() {
    TradingPreview {
        HorizontalPagerIndicator(
            pageCount = 3,
            currentPage = 0
        )
    }
}