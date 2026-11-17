package com.upper.trading.presentation.features.onboarding.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.upper.trading.R
import com.upper.trading.core.utils.ThemePreviews
import com.upper.trading.presentation.theme.TradingTheme


object ThemeDefault {
    val ContainerWidth = 108.dp
    val ContainerHeight = 48.dp
    val ContainerCorner = 4.dp
    val ContainerPadding = 4.dp

    val OptionSize = 24.dp
    val OptionPadding = 4.dp

    val OptionCornerShape = RoundedCornerShape(2.dp)
}

@Composable
fun ThemeModeToggle(
    isDarkTheme: Boolean,
    onDarkThemeSelected: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(ThemeDefault.ContainerCorner)
            )
            .size(
                width = ThemeDefault.ContainerWidth,
                height = ThemeDefault.ContainerHeight
            )
            .padding(ThemeDefault.ContainerPadding),
        horizontalArrangement = Arrangement.spacedBy(ThemeDefault.OptionPadding),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ThemeOptionItem(
            isSelected = !isDarkTheme,
            iconRes = R.drawable.ic_sun,
            onClick = { onDarkThemeSelected.invoke(false) },
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
        )
        ThemeOptionItem(
            isSelected = isDarkTheme,
            iconRes = R.drawable.ic_moon,
            onClick = { onDarkThemeSelected.invoke(true) },
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
        )
    }
}

@Composable
private fun ThemeOptionItem(
    isSelected: Boolean,
    @DrawableRes iconRes: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(ThemeDefault.OptionCornerShape)
            .background(if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = null,
            modifier = Modifier.size(ThemeDefault.OptionSize),
            tint = if (isSelected) {
                Color.White
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            }
        )
    }
}

@ThemePreviews
@Composable
fun PreviewThemeModeToggle() {
    TradingTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            ThemeModeToggle(isDarkTheme = false, {})
        }
    }
}