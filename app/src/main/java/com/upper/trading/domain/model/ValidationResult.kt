package com.upper.trading.domain.model

import com.upper.trading.core.utils.UiText

data class ValidationResult(
    val successful: Boolean,
    val errorMessage: UiText? = null
)
