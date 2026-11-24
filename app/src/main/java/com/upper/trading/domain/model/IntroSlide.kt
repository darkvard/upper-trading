package com.upper.trading.domain.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class IntroSlide(
    @param:DrawableRes val imageRes: Int,
    @param:StringRes val titleRes: Int,
)
