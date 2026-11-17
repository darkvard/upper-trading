package com.upper.trading.domain.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class IntroSlide(
    @DrawableRes val imageRes: Int,
    @StringRes val titleRes: Int,
)
