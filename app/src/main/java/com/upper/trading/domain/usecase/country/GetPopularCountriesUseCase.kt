package com.upper.trading.domain.usecase.country

import com.upper.trading.domain.model.Country
import com.upper.trading.domain.repository.CountryRepository
import javax.inject.Inject

class GetPopularCountriesUseCase @Inject constructor(
    private val repository: CountryRepository,
) {
    operator fun invoke(): List<Country> = repository.getPopularCountries()
}
