package com.upper.trading.domain.usecase.onboarding

import com.upper.trading.domain.model.IntroSlide
import com.upper.trading.domain.repository.OnboardingRepository
import javax.inject.Inject

class GetIntroSlidesUseCase @Inject constructor(
    private val repository: OnboardingRepository
) {
    operator fun invoke(): List<IntroSlide> = repository.getIntroSlides()
}
