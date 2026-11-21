package com.upper.trading.presentation.features.auth.signin.components

import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import com.upper.trading.R
import com.upper.trading.core.utils.ThemePreviews
import com.upper.trading.core.utils.TradingPreview
import com.upper.trading.presentation.components.textfield.AppPhoneTextField
import com.upper.trading.presentation.components.textfield.AppTextField
import com.upper.trading.presentation.features.auth.signin.AuthInputMode
import com.upper.trading.presentation.features.auth.signin.AuthTab

@Immutable
data class AuthInputModeFieldStyle(
    val fadeInDurationMs: Int = 220,
    val fadeOutDurationMs: Int = 180,
)

object AuthInputModeFieldDefaults {
    val style: AuthInputModeFieldStyle = AuthInputModeFieldStyle()
}

/**
 * A composite input field for authentication that seamlessly switches between
 * Email mode and Phone number mode based on [inputMode].
 *
 * Emits [onModeChange] when the user clicks the header action (e.g. "Sign in with phone").
 */
@Composable
fun AuthInputModeField(
    authTab: AuthTab,
    inputMode: AuthInputMode,
    emailState: TextFieldState,
    phoneState: TextFieldState,
    countryCode: String,
    @DrawableRes countryFlag: Int,
    onModeChange: (AuthInputMode) -> Unit,
    onCountryClick: () -> Unit,
    modifier: Modifier = Modifier.fillMaxWidth(),
    style: AuthInputModeFieldStyle = AuthInputModeFieldDefaults.style,
    isCountryPickerExpanded: Boolean = false,
    emailError: String? = null,
    phoneError: String? = null,
    emailLabel: String = stringResource(R.string.email),
    phoneLabel: String = stringResource(R.string.mobile_number),
    emailPlaceholder: String = stringResource(R.string.enter_your_email),
    phonePlaceholder: String = stringResource(R.string.enter_your_phone_number),
    signInWithPhoneText: String = stringResource(R.string.sign_in_with_phone),
    signInWithEmailText: String = stringResource(R.string.sign_in_with_email),
) {
    AnimatedContent(
        targetState = inputMode,
        transitionSpec = {
            (fadeIn(animationSpec = tween(style.fadeInDurationMs)) togetherWith
                    fadeOut(animationSpec = tween(style.fadeOutDurationMs))) using null
        },
        label = "AuthInputModeTransition"
    ) { mode ->
        when (mode) {
            AuthInputMode.EMAIL -> {
                AppTextField(
                    state = emailState,
                    modifier = modifier,
                    label = emailLabel,
                    placeholder = emailPlaceholder,
                    headerActionText = if (authTab == AuthTab.SIGN_IN) {
                        signInWithPhoneText
                    } else {
                        stringResource(R.string.sign_up_with_phone)
                    },
                    onHeaderActionClick = { onModeChange(AuthInputMode.PHONE) },
                    isError = emailError != null,
                    errorMessage = emailError,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next
                    )
                )
            }

            AuthInputMode.PHONE -> {
                AppPhoneTextField(
                    state = phoneState,
                    countryCode = countryCode,
                    countryFlag = countryFlag,
                    onCountryClick = onCountryClick,
                    modifier = modifier,
                    isExpanded = isCountryPickerExpanded,
                    label = phoneLabel,
                    placeholder = phonePlaceholder,
                    headerActionText = if (authTab == AuthTab.SIGN_IN) {
                        signInWithEmailText
                    } else {
                        stringResource(R.string.sign_up_with_email)
                    },
                    onHeaderActionClick = { onModeChange(AuthInputMode.EMAIL) },
                    isError = phoneError != null,
                    errorMessage = phoneError,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Phone,
                        imeAction = ImeAction.Next
                    )
                )
            }
        }
    }
}

@ThemePreviews
@Composable
fun PreviewAuthInputModeField() {
    TradingPreview {
        var inputMode by remember { mutableStateOf(AuthInputMode.EMAIL) }
        val emailState = rememberTextFieldState(initialText = "")
        val phoneState = rememberTextFieldState(initialText = "")
        var isCountryPickerOpen by remember { mutableStateOf(false) }

        AuthInputModeField(
            authTab = AuthTab.SIGN_IN,
            inputMode = inputMode,
            emailState = emailState,
            phoneState = phoneState,
            countryCode = "+84",
            countryFlag = R.drawable.ic_flag_vn,
            onModeChange = { inputMode = it },
            onCountryClick = { isCountryPickerOpen = !isCountryPickerOpen },
            isCountryPickerExpanded = isCountryPickerOpen
        )
    }
}
