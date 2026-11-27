package com.upper.trading.presentation.features.auth.otp.components

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.KeyboardActionHandler
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.byValue
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.upper.trading.core.utils.ThemePreviews
import com.upper.trading.core.utils.TradingPreview
import com.upper.trading.presentation.theme.AppColor
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toPersistentList

/**
 * Creates and remembers a [TextFieldState] for OTP entry with two-way synchronization:
 * 1. Outside-in: Synchronizes external changes (e.g. autofill from notifications or clipboard) into [TextFieldState].
 * 2. Inside-out: Listens to user keyboard typing and emits updated digit lists via [onDigitsChange].
 *
 * @param digits Current list of digits from the UI state or ViewModel.
 * @param onDigitsChange Callback invoked when the user modifies the OTP text via keyboard.
 * @return A remembered [TextFieldState] bound to the external digit state.
 */
@Composable
fun rememberOtpTextFieldState(
    digits: ImmutableList<Int>,
    onDigitsChange: (ImmutableList<Int>) -> Unit
): TextFieldState {
    val initialText = remember { digits.joinToString("") }
    val state = rememberTextFieldState(initialText = initialText)
    val currentOnDigitsChange by rememberUpdatedState(onDigitsChange)

    // Outside-in sync: when digits change from an external source (autofill, paste, clear)
    LaunchedEffect(digits) {
        val externalText = digits.joinToString("")
        if (state.text.toString() != externalText) {
            state.edit {
                replace(0, length, externalText)
            }
        }
    }

    // Inside-out sync: when the user types in the text field, notify the caller
    LaunchedEffect(state) {
        snapshotFlow { state.text.toString() }
            .collect { newText ->
                val newDigits = newText.mapNotNull { it.digitToIntOrNull() }.toPersistentList()
                if (newDigits != digits) {
                    currentOnDigitsChange(newDigits)
                }
            }
    }

    return state
}

@Composable
fun OtpTextField(
    state: TextFieldState,
    modifier: Modifier = Modifier,
    digitCount: Int = 6,
    isValid: Boolean? = null,
    enabled: Boolean = true,
    keyboardOptions: KeyboardOptions = KeyboardOptions(
        keyboardType = KeyboardType.NumberPassword,
        imeAction = ImeAction.Done
    ),
    onKeyboardAction: KeyboardActionHandler? = null,
) {
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current
    val isEditable = enabled && isValid != true

    LaunchedEffect(isValid) {
        if (isValid == false) {
            focusRequester.requestFocus()
            keyboardController?.show()
        } else if (isValid == true) {
            keyboardController?.hide()
        }
    }

    val digitFilterTransformation = remember(digitCount) {
        InputTransformation.byValue { current, proposed ->
            if (proposed.length <= digitCount && proposed.all { it.isDigit() }) {
                proposed
            } else {
                current
            }
        }
    }

    BasicTextField(
        state = state,
        modifier = modifier.focusRequester(focusRequester),
        enabled = isEditable,
        keyboardOptions = keyboardOptions,
        onKeyboardAction = onKeyboardAction,
        lineLimits = TextFieldLineLimits.SingleLine,
        inputTransformation = digitFilterTransformation,
        decorator = { innerTextField ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        if (isEditable) {
                            focusRequester.requestFocus()
                            keyboardController?.show()
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                // Hidden native text field keeping IME session active
                Box(modifier = Modifier.size(1.dp).alpha(0f)) {
                    innerTextField()
                }

                // Custom 6-digit visual cells
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val currentText = state.text.toString()
                    repeat(digitCount) { index ->
                        val digit = currentText.getOrNull(index)?.digitToIntOrNull()
                        val isCellFocused = isEditable && (index == currentText.length)

                        DigitCell(
                            number = digit,
                            isValid = isValid,
                            isFocused = isCellFocused
                        )
                    }
                }
            }
        }
    )
}

@Composable
fun DigitCell(
    number: Int? = null,
    isValid: Boolean? = null,
    isFocused: Boolean = false,
    modifier: Modifier = Modifier
) {
    val textColor = when (isValid) {
        true -> AppColor.success
        false -> MaterialTheme.colorScheme.error
        else -> {
            if (number == null && !isFocused) {
                MaterialTheme.colorScheme.outline
            } else {
                MaterialTheme.colorScheme.primary
            }
        }
    }

    val background = when (isValid) {
        null -> MaterialTheme.colorScheme.surface
        else -> textColor.copy(alpha = 0.15f)
    }

    val infiniteTransition = rememberInfiniteTransition(label = "OtpCursorBlink")
    val animatedBarColor by if (isFocused && number == null && isValid == null) {
        infiniteTransition.animateColor(
            initialValue = MaterialTheme.colorScheme.primary,
            targetValue = MaterialTheme.colorScheme.outline,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 650, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "OtpBarColorAnimation"
        )
    } else {
        rememberUpdatedState(textColor)
    }

    Column(
        modifier = modifier
            .background(
                color = background,
                shape = RoundedCornerShape(4.dp)
            ),
        verticalArrangement = Arrangement.spacedBy(6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = number?.toString() ?: "",
            style = MaterialTheme.typography.titleLarge,
            color = textColor,
            modifier = Modifier.padding(top = 16.dp)
        )

        Box(
            modifier = Modifier
                .padding(bottom = 8.dp, start = 8.dp, end = 8.dp)
                .background(animatedBarColor, RoundedCornerShape(10.dp))
                .width(24.dp)
                .height(2.dp)
        )
    }
}

@ThemePreviews
@Composable
fun PreviewOtpTextField() {
    TradingPreview {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OtpTextField(
                state = rememberTextFieldState("123"),
            )
            OtpTextField(
                state = rememberTextFieldState("123456"),
                isValid = true,
            )
            OtpTextField(
                state = rememberTextFieldState("123456"),
                isValid = false,
            )
        }
    }
}
