package com.upper.trading.domain.usecase.country

import com.upper.trading.di.qualifier.DefaultDispatcher
import com.upper.trading.domain.model.Country
import com.upper.trading.domain.repository.CountryRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetPopularCountriesUseCase @Inject constructor(
    @param:DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher,
    private val repository: CountryRepository,

) {
    suspend operator fun invoke(): List<Country> = withContext(defaultDispatcher) {
        repository.getPopularCountries()
    }
}
