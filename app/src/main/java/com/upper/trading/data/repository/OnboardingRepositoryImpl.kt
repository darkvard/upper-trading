package com.upper.trading.data.repository

import com.upper.trading.R
import com.upper.trading.domain.model.IntroSlide
import com.upper.trading.domain.repository.OnboardingRepository
import javax.inject.Inject

class OnboardingRepositoryImpl @Inject constructor() : OnboardingRepository {
    override fun getIntroSlides(): List<IntroSlide> = listOf(
        IntroSlide(
            imageRes = R.drawable.img_onboarding_trusted,
            titleRes = R.string.intro_desc_1
        ),
        IntroSlide(
            imageRes = R.drawable.img_onboarding_any_currency,
            titleRes = R.string.intro_desc_2
        ),
        IntroSlide(
            imageRes = R.drawable.img_onboarding_invest_and_earn,
            titleRes = R.string.intro_desc_3
        )
    )
}
