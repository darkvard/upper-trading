package com.upper.trading.domain.usecase.country

import com.upper.trading.di.qualifier.DefaultDispatcher
import com.upper.trading.domain.model.Country
import com.upper.trading.domain.repository.CountryRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetGroupedCountriesUseCase @Inject constructor(
    @param:DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher,
    private val repository: CountryRepository
) {
    suspend operator fun invoke(query: String = ""): Map<Char, List<Country>> = withContext(defaultDispatcher) {
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
        filtered
            .sortedBy { it.name }
            .groupBy { it.name.firstOrNull()?.uppercaseChar() ?: '#' }
    }
}
