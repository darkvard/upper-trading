package com.upper.trading.presentation.components.textfield

import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color

@Stable
internal interface AppTextFieldScope {
    val interactionSource: MutableInteractionSource
    val borderColor: Color
}

private class AppTextFieldScopeImpl(
    override val interactionSource: MutableInteractionSource, initialBorderColor: Color
): AppTextFieldScope {
    override var borderColor: Color by mutableStateOf(initialBorderColor)
}

@Composable
internal fun InputTextFieldContainer(
    modifier: Modifier = Modifier,
    style: AppTextFieldStyle = AppTextFieldDefault.style,
    label: String? = null,
    headerActionText: String? = null,
    onHeaderActionClick: (() -> Unit)? = null,
    isError: Boolean = false,
    errorMessage: String? = null,
    content: @Composable AppTextFieldScope.() -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    val borderColor = when {
        isError -> MaterialTheme.colorScheme.error
        isFocused -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.outline
    }

    val textFieldScope = remember(interactionSource) {
        AppTextFieldScopeImpl(interactionSource, borderColor)
    }
    textFieldScope.borderColor = borderColor

    Column(
        modifier = modifier
    ) {
        if (!label.isNullOrBlank() || (!headerActionText.isNullOrBlank() && onHeaderActionClick != null)) {
            HeaderTextAction(
                label = label,
                headerActionText = headerActionText,
                onHeaderActionClick = onHeaderActionClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = style.headerPaddingVertical)
            )
        }

        textFieldScope.content()

        if (isError && !errorMessage.isNullOrBlank()) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(style.errorPadding)
            )
        }
    }
}


@Composable
internal fun InputTextFieldFrame(
    style: AppTextFieldStyle,
    borderColor: Color,
    leadingContent: (@Composable () -> Unit)? = null,
    trailingContent: (@Composable () -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit
) {
    val shape = RoundedCornerShape(style.cornerRadius)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(style.inputHeight)
            .clip(shape)
            .border(
                width = style.borderWidth,
                color = borderColor,
                shape = shape
            )
            .padding(style.contentPadding),
        horizontalArrangement = Arrangement.spacedBy(style.inputFrameSpacing),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (leadingContent != null) {
            leadingContent()
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            contentAlignment = Alignment.CenterStart,
            content = content
        )

        if (trailingContent != null) {
            trailingContent()
        }
    }
}

