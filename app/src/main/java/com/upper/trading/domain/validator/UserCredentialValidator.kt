package com.upper.trading.domain.validator

import com.upper.trading.R
import com.upper.trading.core.utils.UiText
import com.upper.trading.domain.model.ValidationResult
import java.util.regex.Pattern
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Domain-level validator for user credentials (Email, Phone, Password, Terms).
 * Reusable across Auth (Sign In, Sign Up), Security, and Profile flows.
 */
@Singleton
class UserCredentialValidator @Inject constructor() {

    private val emailPattern = Pattern.compile(
        "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$"
    )

    private val phonePattern = Pattern.compile("^[0-9]{9,12}$")

    companion object {
        const val MIN_PASSWORD_LENGTH = 6
    }

    fun validateEmail(email: String, isSubmit: Boolean = false): ValidationResult {
        val trimmed = email.trim()
        if (trimmed.isEmpty()) {
            return if (isSubmit) {
                ValidationResult(
                    successful = false,
                    errorMessage = UiText.StringResource(R.string.error_empty_email)
                )
            } else {
                ValidationResult(successful = true)
            }
        }
        if (!emailPattern.matcher(trimmed).matches()) {
            return ValidationResult(
                successful = false,
                errorMessage = UiText.StringResource(R.string.error_invalid_email)
            )
        }
        return ValidationResult(successful = true)
    }

    fun validatePhone(phone: String, isSubmit: Boolean = false): ValidationResult {
        val trimmed = phone.trim()
        if (trimmed.isEmpty()) {
            return if (isSubmit) {
                ValidationResult(
                    successful = false,
                    errorMessage = UiText.StringResource(R.string.error_empty_phone)
                )
            } else {
                ValidationResult(successful = true)
            }
        }
        if (!phonePattern.matcher(trimmed).matches()) {
            return ValidationResult(
                successful = false,
                errorMessage = UiText.StringResource(R.string.error_invalid_phone)
            )
        }
        return ValidationResult(successful = true)
    }

    fun validatePassword(password: String, isSubmit: Boolean = false): ValidationResult {
        if (password.isEmpty()) {
            return if (isSubmit) {
                ValidationResult(
                    successful = false,
                    errorMessage = UiText.StringResource(R.string.error_empty_password)
                )
            } else {
                ValidationResult(successful = true)
            }
        }
        if (password.length < MIN_PASSWORD_LENGTH) {
            return ValidationResult(
                successful = false,
                errorMessage = UiText.StringResource(R.string.error_password_too_short)
            )
        }
        if (password.firstOrNull()?.isUpperCase() != true) {
            return ValidationResult(
                successful = false,
                errorMessage = UiText.StringResource(R.string.error_password_first_letter_uppercase)
            )
        }
        if (password.none { it.isDigit() }) {
            return ValidationResult(
                successful = false,
                errorMessage = UiText.StringResource(R.string.error_password_missing_number)
            )
        }
        if (password.none { !it.isLetterOrDigit() }) {
            return ValidationResult(
                successful = false,
                errorMessage = UiText.StringResource(R.string.error_password_missing_special_char)
            )
        }
        return ValidationResult(successful = true)
    }

    fun validateConfirmPassword(
        password: String,
        confirmPassword: String,
        isSubmit: Boolean = false
    ): ValidationResult {
        if (confirmPassword.isEmpty()) {
            return if (isSubmit) {
                ValidationResult(
                    successful = false,
                    errorMessage = UiText.StringResource(R.string.error_empty_confirm_password)
                )
            } else {
                ValidationResult(successful = true)
            }
        }
        if (confirmPassword != password) {
            return ValidationResult(
                successful = false,
                errorMessage = UiText.StringResource(R.string.error_password_mismatch)
            )
        }
        return ValidationResult(successful = true)
    }

    fun validateTerms(isAgreed: Boolean): ValidationResult {
        if (!isAgreed) {
            return ValidationResult(
                successful = false,
                errorMessage = UiText.StringResource(R.string.error_agree_terms)
            )
        }
        return ValidationResult(successful = true)
    }

    /**
     * Validates all inputs for the Sign In submission flow in one call.
     */
    fun validateSignIn(
        isEmailMode: Boolean,
        email: String,
        phone: String,
        password: String
    ): SignInValidationResult {
        val emailError = if (isEmailMode) validateEmail(email, isSubmit = true).errorMessage else null
        val phoneError = if (!isEmailMode) validatePhone(phone, isSubmit = true).errorMessage else null
        val passwordError = validatePassword(password, isSubmit = true).errorMessage

        return SignInValidationResult(
            emailError = emailError,
            phoneError = phoneError,
            passwordError = passwordError
        )
    }

    /**
     * Validates all inputs for the Sign Up submission flow in one call.
     */
    fun validateSignUp(
        isEmailMode: Boolean,
        email: String,
        phone: String,
        password: String,
        confirmPassword: String,
        isAgreeTerms: Boolean
    ): SignUpValidationResult {
        val emailError = if (isEmailMode) validateEmail(email, isSubmit = true).errorMessage else null
        val phoneError = if (!isEmailMode) validatePhone(phone, isSubmit = true).errorMessage else null
        val passwordError = validatePassword(password, isSubmit = true).errorMessage
        val confirmPasswordError = validateConfirmPassword(
            password = password,
            confirmPassword = confirmPassword,
            isSubmit = true
        ).errorMessage
        val termsError = validateTerms(isAgreeTerms).errorMessage

        return SignUpValidationResult(
            emailError = emailError,
            phoneError = phoneError,
            passwordError = passwordError,
            confirmPasswordError = confirmPasswordError,
            termsError = termsError
        )
    }
}

data class SignInValidationResult(
    val emailError: UiText? = null,
    val phoneError: UiText? = null,
    val passwordError: UiText? = null
) {
    val hasError: Boolean get() = emailError != null || phoneError != null || passwordError != null
}

data class SignUpValidationResult(
    val emailError: UiText? = null,
    val phoneError: UiText? = null,
    val passwordError: UiText? = null,
    val confirmPasswordError: UiText? = null,
    val termsError: UiText? = null
) {
    val hasError: Boolean
        get() = emailError != null || phoneError != null ||
                passwordError != null || confirmPasswordError != null || termsError != null
}
