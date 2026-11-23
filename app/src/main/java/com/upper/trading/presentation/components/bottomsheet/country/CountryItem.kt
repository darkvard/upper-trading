package com.upper.trading.presentation.components.bottomsheet.country

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.upper.trading.R
import com.upper.trading.core.utils.ThemePreviews
import com.upper.trading.core.utils.TradingPreview
import com.upper.trading.domain.model.Country

@Composable
fun PopularCountryChip(
    item: Country,
    isSelected: Boolean = false,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    style: CountryBottomSheetStyle = CountryBottomSheetDefaults.style
) {
    val shape = RoundedCornerShape(style.itemChipCorner)
    val contentColor = getContentColor(isSelected)
    val backgroundColor = getBackgroundColor(isSelected)

    val borderColor = if (isSelected) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.outlineVariant
    }

    Row(
        modifier = modifier
            .clip(shape)
            .background(color = backgroundColor, shape = shape)
            .border(width = style.itemChipBorderWidth, color = borderColor, shape = shape)
            .selectable(
                selected = isSelected,
                onClick = onClick,
                role = Role.RadioButton
            )
            .padding(
                vertical = style.itemChipPaddingVertical,
                horizontal = style.itemChipPaddingHorizontal
            ),
        horizontalArrangement = Arrangement.spacedBy(style.itemChipSpacing),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(item.flagRes),
            contentDescription = "Flag",
            modifier = Modifier.size(width = style.itemChipFlagWidth, height = style.itemChipFlagHeight)
        )

        Text(
            text = item.name,
            style = MaterialTheme.typography.labelLarge,
            color = contentColor
        )
    }
}

@Composable
fun CountryRowItem(
    item: Country,
    isSelected: Boolean = false,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    style: CountryBottomSheetStyle = CountryBottomSheetDefaults.style
) {
    val contentColor = getContentColor(isSelected)
    val backgroundColor = if (isSelected) {
        MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
    } else {
        MaterialTheme.colorScheme.surface
    }
    val shape = if (isSelected) RoundedCornerShape(style.itemRowCorner) else RectangleShape

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(color = backgroundColor, shape = shape)
            .selectable(
                selected = isSelected,
                onClick = onClick,
                role = Role.RadioButton
            )
            .padding(
                vertical = style.itemRowPaddingVertical,
                horizontal = style.itemRowPaddingHorizontal
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(item.flagRes),
            contentDescription = "Flag",
            modifier = Modifier.size(width = style.itemRowFlagWidth, height = style.itemRowFlagHeight)
        )

        Spacer(modifier = Modifier.width(style.itemRowSpacing))

        Text(
            text = item.name,
            style = MaterialTheme.typography.bodyLarge,
            color = contentColor
        )

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = item.dialCode,
            style = MaterialTheme.typography.bodyMedium,
            color = if (isSelected) contentColor else MaterialTheme.colorScheme.onSurfaceVariant
        )

        if (isSelected) {
            Spacer(modifier = Modifier.width(style.itemRowCheckSpacing))

            Icon(
                imageVector = Icons.Filled.Check,
                contentDescription = "Checked",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(style.itemRowCheckIconSize)
            )
        }
    }
}

@Composable
private fun getBackgroundColor(isSelected: Boolean): Color {
    return if (isSelected) {
        MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
    } else {
        MaterialTheme.colorScheme.surface
    }
}

@Composable
private fun getContentColor(isSelected: Boolean): Color {
    return if (isSelected) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.onSurface
    }
}

@ThemePreviews
@Composable
fun PreviewCountryItem() {
    TradingPreview {
        val country = remember { Country("VN", "Vietnam", "+84", R.drawable.ic_flag_vn) }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
            ) {
                PopularCountryChip(
                    item = country,
                    isSelected = true,
                    onClick = { },
                )

                PopularCountryChip(
                    item = country,
                    isSelected = false,
                    onClick = { },
                )
            }

            CountryRowItem(
                item = country,
                isSelected = false,
                onClick = { },
            )

            CountryRowItem(
                item = country,
                isSelected = true,
                onClick = { },
            )
        }
    }
}
