package com.upper.trading.presentation.components.textfield

import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicSecureTextField
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.KeyboardActionHandler
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.TextObfuscationMode
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.upper.trading.R
import com.upper.trading.core.utils.ThemePreviews
import com.upper.trading.core.utils.TradingPreview

@Immutable
data class AppTextFieldStyle(
    val headerPaddingVertical: Dp = 4.dp,
    val headerPaddingHorizontal: Dp = 2.dp,
    val inputHeight: Dp = 40.dp,
    val borderWidth: Dp = 1.dp,
    val cornerRadius: Dp = 4.dp,
    val inputFrameSpacing: Dp = 8.dp,
    val iconSize: Dp = 24.dp,
    val contentPadding: PaddingValues = PaddingValues(
        horizontal = 16.dp, vertical = 10.dp
    ),
    val errorPadding: PaddingValues = PaddingValues(
        start = 8.dp,
        top = 4.dp,
        end = 8.dp,
        bottom = 0.dp
    ),
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
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(),
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
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(),
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

@Composable
fun AppPhoneTextField(
    state: TextFieldState,
    countryDialCode: String,
    @DrawableRes countryFlag: Int,
    onCountryClick: () -> Unit,
    modifier: Modifier = Modifier.fillMaxWidth(),
    isExpanded: Boolean = false,
    enabled: Boolean = true,
    style: AppTextFieldStyle = AppTextFieldDefault.style,
    label: String? = null,
    placeholder: String? = null,
    headerActionText: String? = null,
    onHeaderActionClick: (() -> Unit)? = null,
    isError: Boolean = false,
    errorMessage: String? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
    onKeyboardAction: KeyboardActionHandler? = null,
    trailingContent: (@Composable () -> Unit)? = null,
) {
    AppTextField(
        state = state,
        modifier = modifier,
        enabled = enabled,
        singleLine = true,
        style = style,
        label = label,
        placeholder = placeholder,
        headerActionText = headerActionText,
        onHeaderActionClick = onHeaderActionClick,
        isError = isError,
        errorMessage = errorMessage,
        keyboardOptions = keyboardOptions,
        onKeyboardAction = onKeyboardAction,
        leadingContent = {
            PhoneCountryPrefix(
                countryDialCode = countryDialCode,
                countryFlag = countryFlag,
                onClick = onCountryClick,
                enabled = enabled
            )
        },
        trailingContent = trailingContent ?: {
            CountryDropdownIcon(
                isExpanded = isExpanded,
                onClick = onCountryClick,
                enabled = enabled
            )
        }
    )
}

@ThemePreviews
@Composable
fun PreviewAppTextField() {
    TradingPreview {
        val emailState = rememberTextFieldState(initialText = "")
        val phoneState = rememberTextFieldState(initialText = "")
        val passwordState = rememberTextFieldState(initialText = "123456")
        val errorState = rememberTextFieldState(initialText = "invalid_email@")
        val disabledState = rememberTextFieldState(initialText = "upper_trader_vip")
        var isPhoneMode by remember { mutableStateOf(true) }
        var isPasswordHidden by remember { mutableStateOf(true) }
        var isCountryPickerOpen by remember { mutableStateOf(false) }
        var selectedCountryDialCode by remember { mutableStateOf("+84") }
        var selectedCountryFlag by remember { mutableIntStateOf(R.drawable.ic_flag_vn) }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            AnimatedContent(
                targetState = isPhoneMode,
                transitionSpec = {
                    fadeIn(animationSpec = tween(220)) togetherWith fadeOut(
                        animationSpec = tween(
                            180
                        )
                    )
                },
                label = "InputModePreviewTransition"
            ) { inPhoneMode ->
                if (!inPhoneMode) {
                    AppTextField(
                        state = emailState,
                        label = "Email Address",
                        placeholder = "Enter your email",
                        headerActionText = "Sign in with phone",
                        onHeaderActionClick = { isPhoneMode = true }
                    )
                } else {
                    AppPhoneTextField(
                        state = phoneState,
                        countryDialCode = selectedCountryDialCode,
                        countryFlag = selectedCountryFlag,
                        isExpanded = isCountryPickerOpen,
                        onCountryClick = {
                            isCountryPickerOpen = !isCountryPickerOpen
                        },
                        label = "Mobile Number",
                        placeholder = "Enter your number",
                        headerActionText = "Sign In with Email",
                        onHeaderActionClick = { isPhoneMode = false }
                    )
                }
            }

            AppSecureTextField(
                state = passwordState,
                label = "Password",
                placeholder = "Enter password",
                isPasswordHidden = isPasswordHidden,
                onTogglePasswordVisibility = { isPasswordHidden = !isPasswordHidden },
                isError = false,
                errorMessage = "Invalid password."
            )

            AppSecureTextField(
                state = passwordState,
                label = "Password",
                placeholder = "Enter password",
                isPasswordHidden = isPasswordHidden,
                onTogglePasswordVisibility = { isPasswordHidden = !isPasswordHidden },
                isError = true,
                errorMessage = "Invalid password."
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