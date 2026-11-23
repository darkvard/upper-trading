package com.upper.trading.domain.usecase.country

import com.upper.trading.domain.model.Country
import com.upper.trading.domain.repository.CountryRepository
import javax.inject.Inject
import kotlin.text.contains

class GetGroupedCountriesUseCase @Inject constructor(
    private val repository: CountryRepository,
) {
    operator fun invoke(query: String = ""): Map<Char, List<Country>> {
        val items = repository.getCountries()
        val filtered = if (query.isBlank()) {
            items
        } else {
            val trimmed = query.trim()
            items.filter { country ->
                country.name.contains(trimmed, ignoreCase = true) ||
                        country.dialCode.contains(trimmed, ignoreCase = true) ||
                        country.code.contains(trimmed, ignoreCase = true)
            }
        }
        return filtered
            .sortedBy { it.name }
            .groupBy { it.name.firstOrNull()?.uppercaseChar() ?: '#' }
    }
}
