package com.upper.trading.domain.repository

import com.upper.trading.domain.model.IntroSlide

interface OnboardingRepository {
    fun getIntroSlides(): List<IntroSlide>
}
