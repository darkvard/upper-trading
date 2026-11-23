package com.upper.trading.presentation.features.auth.signin.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.upper.trading.R
import com.upper.trading.core.utils.ThemePreviews
import com.upper.trading.core.utils.TradingPreview
import com.upper.trading.core.utils.throttleClick
import com.upper.trading.presentation.components.button.AppPrimaryButton
import com.upper.trading.presentation.components.textfield.AppSecureTextField
import com.upper.trading.presentation.features.auth.signin.AuthSignInIntent
import com.upper.trading.presentation.features.auth.signin.AuthTab
import com.upper.trading.presentation.features.auth.signin.SignInFormState

@Immutable
data class AuthSignInFormStyle(
    val itemSpacing: Dp = 20.dp,
    val forgotPasswordTopSpacing: Dp = 12.dp,
)

object AuthSignInFormDefaults {
    val style: AuthSignInFormStyle = AuthSignInFormStyle()
}

@Composable
fun AuthSignInForm(
    formState: SignInFormState,
    isLoading: Boolean,
    onIntent: (AuthSignInIntent) -> Unit,
    modifier: Modifier = Modifier,
    emailState: TextFieldState = rememberTextFieldState(initialText = formState.email),
    phoneState: TextFieldState = rememberTextFieldState(initialText = formState.phoneNumber),
    passwordState: TextFieldState = rememberTextFieldState(initialText = formState.password),
    style: AuthSignInFormStyle = AuthSignInFormDefaults.style,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(style.itemSpacing)
    ) {
        AuthInputModeField(
            authTab = AuthTab.SIGN_IN,
            inputMode = formState.inputMode,
            emailState = emailState,
            phoneState = phoneState,
            countryDialCode = formState.country.dialCode,
            countryFlag = formState.country.flagRes,
            emailError = formState.emailError?.asString(),
            phoneError = formState.phoneError?.asString(),
            onModeChange = {
                onIntent(AuthSignInIntent.ChangeInputMode(it))
            },
            onCountryClick = {
                onIntent(AuthSignInIntent.ToggleCountryBottomSheetVisibility)
            },
            isCountryPickerExpanded = formState.isCountryPickerOpen
        )

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(style.forgotPasswordTopSpacing)
        ) {
            AppSecureTextField(
                state = passwordState,
                label = stringResource(R.string.password),
                placeholder = stringResource(R.string.enter_your_password),
                isPasswordHidden = formState.isPasswordHidden,
                onTogglePasswordVisibility = {
                    onIntent(AuthSignInIntent.TogglePasswordVisibility)
                },
                isError = formState.passwordError != null,
                errorMessage = formState.passwordError?.asString()
            )

            Text(
                text = stringResource(R.string.forgot_password),
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.throttleClick {
                    onIntent(AuthSignInIntent.ForgotPasswordClick)
                }
            )
        }

        AppPrimaryButton(
            text = stringResource(R.string.sign_in),
            isLoading = isLoading,
            onClick = {
                onIntent(AuthSignInIntent.Submit)
            }
        )
    }
}

@ThemePreviews
@Composable
fun PreviewAuthSignInForm() {
    TradingPreview {
        AuthSignInForm(
            formState = SignInFormState(),
            isLoading = false,
            onIntent = {},
        )
    }
}
