package com.upper.trading.presentation.components.textfield

import androidx.annotation.DrawableRes
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.upper.trading.R
import com.upper.trading.core.utils.bounceClick
import com.upper.trading.core.utils.throttleClick

@Composable
internal fun InputPlaceholderAndField(
    text: CharSequence,
    placeholder: String?,
    innerTextField: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(),
        contentAlignment = Alignment.CenterStart
    ) {
        if (text.isBlank() && !placeholder.isNullOrBlank()) {
            Text(
                text = placeholder,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        innerTextField()
    }
}

@Composable
internal fun PhoneCountryPrefix(
    countryDialCode: String,
    @DrawableRes countryFlag: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    style: AppTextFieldStyle = AppTextFieldDefault.style
) {
    Row(
        modifier = modifier
            .bounceClick(enabled = enabled, onCLick = onClick),
        horizontalArrangement = Arrangement.spacedBy(style.inputFrameSpacing),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(countryFlag),
            contentDescription = null,
            modifier = Modifier.size(style.iconSize),
            contentScale = ContentScale.Crop
        )
        Text(
            text = countryDialCode,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
internal fun CountryDropdownIcon(
    isExpanded: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    style: AppTextFieldStyle = AppTextFieldDefault.style
) {
    val rotation by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f,
        animationSpec = tween(durationMillis = 200),
        label = "CountryDropdownArrowAnimation"
    )

    Icon(
        painter = painterResource(R.drawable.ic_chevron_down),
        contentDescription = if (isExpanded) "Close country list" else "Open country list",
        modifier = modifier
            .size(style.iconSize)
            .graphicsLayer { rotationZ = rotation }
            .bounceClick(enabled = enabled, onCLick = onClick),
        tint = MaterialTheme.colorScheme.onBackground
    )
}

@Composable
internal fun PasswordToggleIcon(
    isPasswordHidden: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    style: AppTextFieldStyle = AppTextFieldDefault.style
) {
    Image(
        painter = painterResource(
            if (isPasswordHidden) R.drawable.ic_visibility_off
            else R.drawable.ic_visibility_on
        ),
        contentDescription = if (isPasswordHidden) "Show password" else "Hide password",
        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onBackground),
        modifier = modifier
            .size(style.iconSize)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
    )
}

@Composable
internal fun HeaderTextAction(
    label: String? = null,
    headerActionText: String? = null,
    onHeaderActionClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    style: AppTextFieldStyle = AppTextFieldDefault.style
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (!label.isNullOrBlank()) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onBackground
            )
        } else {
            Spacer(modifier = Modifier.width(1.dp))
        }

        if (!headerActionText.isNullOrBlank() && onHeaderActionClick != null) {
            Text(
                text = headerActionText,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .padding(
                        vertical = style.headerPaddingVertical,
                        horizontal = style.headerPaddingHorizontal
                    )
                    .throttleClick {
                        onHeaderActionClick()
                    }
            )
        }
    }
}
