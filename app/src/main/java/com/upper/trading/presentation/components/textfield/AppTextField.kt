package com.upper.trading.presentation.components.textfield

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicSecureTextField
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.KeyboardActionHandler
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.TextObfuscationMode
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.upper.trading.R
import com.upper.trading.core.utils.ThemePreviews
import com.upper.trading.core.utils.TradingPreview

@Immutable
data class AppTextFieldStyle(
    val headerPadding: Dp = 4.dp,
    val inputHeight: Dp = 40.dp,
    val borderWidth: Dp = 1.dp,
    val cornerRadius: Dp = 4.dp,
    val inputFrameSpacing: Dp = 8.dp,
    val iconSize: Dp = 24.dp,
    val contentPadding: PaddingValues = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
)

object AppTextFieldDefault {
    val style: AppTextFieldStyle = AppTextFieldStyle()
}

@Composable
fun AppTextField(
    state: TextFieldState,
    modifier: Modifier = Modifier.fillMaxWidth(),
    enabled: Boolean = true,
    singleLine: Boolean = true,
    style: AppTextFieldStyle = AppTextFieldDefault.style,
    label: String? = null,
    placeholder: String? = null,
    headerActionText: String? = null,
    onHeaderActionClick: (() -> Unit)? = null,
    isError: Boolean = false,
    errorMessage: String? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    onKeyboardAction: KeyboardActionHandler? = null,
    leadingContent: (@Composable () -> Unit)? = null,
    trailingContent: (@Composable () -> Unit)? = null,
) {
    InputTextFieldContainer(
        modifier = modifier,
        style = style,
        label = label,
        headerActionText = headerActionText,
        onHeaderActionClick = onHeaderActionClick,
        isError = isError,
        errorMessage = errorMessage
    ) {
        val lineLimits = if (singleLine) {
            TextFieldLineLimits.SingleLine
        } else {
            TextFieldLineLimits.MultiLine(minHeightInLines = 1, maxHeightInLines = Int.MAX_VALUE)
        }

        InputTextFieldFrame(
            style = style,
            borderColor = borderColor,
            leadingContent = leadingContent,
            trailingContent = trailingContent,
        ) {
            BasicTextField(
                state = state,
                enabled = enabled,
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.onSurface
                ),
                interactionSource = interactionSource,
                keyboardOptions = keyboardOptions,
                onKeyboardAction = onKeyboardAction,
                lineLimits = lineLimits,
                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                decorator = { innerTextField ->
                    InputPlaceholderAndField(
                        text = state.text,
                        placeholder = placeholder,
                        innerTextField = innerTextField
                    )
                }
            )
        }
    }
}

@Composable
fun AppSecureTextField(
    state: TextFieldState,
    modifier: Modifier = Modifier.fillMaxWidth(),
    enabled: Boolean = true,
    isPasswordHidden: Boolean = true,
    onTogglePasswordVisibility: (() -> Unit)? = null,
    style: AppTextFieldStyle = AppTextFieldDefault.style,
    label: String? = null,
    placeholder: String? = null,
    headerActionText: String? = null,
    onHeaderActionClick: (() -> Unit)? = null,
    isError: Boolean = false,
    errorMessage: String? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    onKeyboardAction: KeyboardActionHandler? = null,
    leadingContent: (@Composable () -> Unit)? = null,
) {
    val obfuscationMode = if (isPasswordHidden) {
        TextObfuscationMode.RevealLastTyped
    } else {
        TextObfuscationMode.Visible
    }

    InputTextFieldContainer(
        modifier = modifier,
        style = style,
        label = label,
        headerActionText = headerActionText,
        onHeaderActionClick = onHeaderActionClick,
        isError = isError,
        errorMessage = errorMessage
    ) {
        InputTextFieldFrame(
            style = style,
            borderColor = borderColor,
            leadingContent = leadingContent,
            trailingContent = if (onTogglePasswordVisibility != null) {
                {
                    PasswordToggleIcon(
                        isPasswordHidden = isPasswordHidden,
                        onClick = onTogglePasswordVisibility
                    )
                }
            } else null,
        ) {
            BasicSecureTextField(
                state = state,
                enabled = enabled,
                textObfuscationMode = obfuscationMode,
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.onSurface
                ),
                interactionSource = interactionSource,
                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                keyboardOptions = keyboardOptions,
                onKeyboardAction = onKeyboardAction,
                decorator = { innerTextField ->
                    InputPlaceholderAndField(
                        text = state.text,
                        placeholder = placeholder,
                        innerTextField = innerTextField
                    )
                }
            )
        }
    }
}

@ThemePreviews
@Composable
fun PreviewAppTextField() {
    TradingPreview {
        val emailState = rememberTextFieldState(initialText = "")
        val phoneState = rememberTextFieldState(initialText = "0987654321")
        val passwordState = rememberTextFieldState(initialText = "123456")
        val errorState = rememberTextFieldState(initialText = "invalid_email@")
        val disabledState = rememberTextFieldState(initialText = "upper_trader_vip")
        var isPasswordHidden by remember { mutableStateOf(true) }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            AppTextField(
                state = emailState,
                label = "Email Address",
                placeholder = "Enter your email",
                headerActionText = "Sign in with phone",
                onHeaderActionClick = {},
                leadingContent = {
                    Icon(
                        painter = painterResource(R.drawable.ic_at_sign),
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            )

            AppTextField(
                state = phoneState,
                label = "Phone Number",
                placeholder = "Enter phone number",
                headerActionText = "Sign in with email",
                onHeaderActionClick = {},
                leadingContent = {
                    Image(
                        painter = painterResource(R.drawable.ic_flag_vn),
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                }
            )

            AppSecureTextField(
                state = passwordState,
                label = "Password",
                placeholder = "Enter password",
                isPasswordHidden = isPasswordHidden,
                onTogglePasswordVisibility = { isPasswordHidden = !isPasswordHidden }
            )

            AppTextField(
                state = errorState,
                label = "Email (Error state)",
                isError = true,
                errorMessage = "Invalid email format. Please check again."
            )

            AppTextField(
                state = disabledState,
                label = "Referral Code (Disabled)",
                enabled = false
            )
        }
    }
}