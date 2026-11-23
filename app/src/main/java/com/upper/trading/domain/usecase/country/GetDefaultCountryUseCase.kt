package com.upper.trading.domain.usecase.country

import com.upper.trading.domain.model.Country
import com.upper.trading.domain.repository.CountryRepository
import javax.inject.Inject

class GetDefaultCountryUseCase @Inject constructor(
    private val countryRepository: CountryRepository
) {
    operator fun invoke(): Country? {
        return countryRepository.getDeviceCountryCode()?.let { countryRepository.getCountryByCode(it) }
            ?: countryRepository.getCountryByCode(DEFAULT_FALLBACK_COUNTRY_CODE)
            ?: countryRepository.getCountries().firstOrNull()
    }

    companion object {
        private const val DEFAULT_FALLBACK_COUNTRY_CODE = "VN"
    }
}