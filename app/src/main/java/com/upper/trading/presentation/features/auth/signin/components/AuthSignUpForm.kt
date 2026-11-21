package com.upper.trading.presentation.features.auth.signin.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.upper.trading.R
import com.upper.trading.core.utils.ThemePreviews
import com.upper.trading.core.utils.TradingPreview
import com.upper.trading.presentation.components.button.AppPrimaryButton
import com.upper.trading.presentation.components.textfield.AppSecureTextField
import com.upper.trading.presentation.features.auth.signin.AuthSignInIntent
import com.upper.trading.presentation.features.auth.signin.AuthTab
import com.upper.trading.presentation.features.auth.signin.SignUpFormState

@Immutable
data class AuthSignUpFormStyle(
    val itemSpacing: Dp = 20.dp,
    val termsSpacing: Dp = 16.dp,
    val checkboxSize: Dp = 18.dp,
    val checkboxCornerRadius: Dp = 4.dp,
    val checkboxShape: Shape = RoundedCornerShape(checkboxCornerRadius),
    val checkboxBorderWidth: Dp = 1.dp,
    val checkboxSpacing: Dp = 8.dp,
    val checkboxIconScale: Float = 0.7f,
)

object AuthSignUpFormDefaults {
    val style: AuthSignUpFormStyle = AuthSignUpFormStyle()
}

@Composable
fun AuthSignUpForm(
    formState: SignUpFormState,
    isLoading: Boolean,
    onIntent: (AuthSignInIntent) -> Unit,
    modifier: Modifier = Modifier,
    emailState: TextFieldState = rememberTextFieldState(initialText = formState.email),
    phoneState: TextFieldState = rememberTextFieldState(initialText = formState.phoneNumber),
    passwordState: TextFieldState = rememberTextFieldState(initialText = formState.password),
    confirmPasswordState: TextFieldState = rememberTextFieldState(initialText = formState.confirmPassword),
    style: AuthSignUpFormStyle = AuthSignUpFormDefaults.style,
) {
    Column(
        modifier = modifier,
    ) {
        AuthInputModeField(
            authTab = AuthTab.SIGN_UP,
            inputMode = formState.inputMode,
            emailState = emailState,
            phoneState = phoneState,
            countryCode = formState.countryCode,
            countryFlag = formState.countryFlagRes,
            emailError = formState.emailError?.asString(),
            phoneError = formState.phoneError?.asString(),
            onModeChange = {
                onIntent(AuthSignInIntent.ChangeInputMode(it))
            },
            onCountryClick = {
                onIntent(AuthSignInIntent.ToggleCountryPopupVisibility)
            },
            isCountryPickerExpanded = formState.isCountryPickerOpen
        )

        Spacer(modifier = Modifier.height(style.itemSpacing))

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

        Spacer(modifier = Modifier.height(style.itemSpacing))

        AppSecureTextField(
            state = confirmPasswordState,
            label = stringResource(R.string.confirm_password),
            placeholder = stringResource(R.string.confirm_your_password),
            isPasswordHidden = formState.isConfirmPasswordHidden,
            onTogglePasswordVisibility = {
                onIntent(AuthSignInIntent.ToggleConfirmPasswordVisibility)
            },
            isError = formState.confirmPasswordError != null,
            errorMessage = formState.confirmPasswordError?.asString()
        )

        Spacer(modifier = Modifier.height(style.termsSpacing))

        AgreeTermPolicy(
            isAgreeTerms = formState.isAgreeTerms,
            onIntent = onIntent,
            style = style
        )

        Spacer(modifier = Modifier.height(style.itemSpacing))

        AppPrimaryButton(
            text = stringResource(R.string.sign_up),
            isLoading = isLoading,
            onClick = {
                onIntent(AuthSignInIntent.Submit)
            }
        )
    }
}

@Composable
fun AgreeTermPolicy(
    isAgreeTerms: Boolean,
    onIntent: (AuthSignInIntent) -> Unit,
    modifier: Modifier = Modifier,
    style: AuthSignUpFormStyle = AuthSignUpFormDefaults.style,
) {
    val containerColor = if (isAgreeTerms) {
        MaterialTheme.colorScheme.primary
    } else {
        Color.Transparent
    }

    val borderColor = if (isAgreeTerms) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.onBackground
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(style.checkboxSpacing)
    ) {
        Box(
            modifier = Modifier
                .size(style.checkboxSize)
                .clip(style.checkboxShape)
                .background(containerColor)
                .border(width = style.checkboxBorderWidth, color = borderColor, shape = style.checkboxShape)
                .clickable(
                    role = Role.Checkbox,
                    onClick = {
                        onIntent(AuthSignInIntent.ToggleAgreeTerms(!isAgreeTerms))
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isAgreeTerms) {
                Icon(
                    imageVector = Icons.Rounded.Check,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(style.checkboxSize * style.checkboxIconScale)
                )
            }
        }
        Text(
            text = stringResource(R.string.i_agree_to_the_terms_policy),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@ThemePreviews
@Composable
fun PreviewAuthSignUpForm() {
    TradingPreview {
        AuthSignUpForm(
            formState = SignUpFormState(),
            isLoading = false,
            onIntent = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}
