package com.upper.trading.domain.usecase.country

import com.upper.trading.data.config.AppConfig
import com.upper.trading.data.datasource.CountryDataSource
import com.upper.trading.di.qualifier.DefaultDispatcher
import com.upper.trading.domain.model.Country
import com.upper.trading.domain.repository.CountryRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetDefaultCountryUseCase @Inject constructor(
    @param:DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher,
    private val countryRepository: CountryRepository
) {
    suspend operator fun invoke(): Country = withContext(defaultDispatcher) {
        countryRepository.getDeviceCountryCode()?.let {
            countryRepository.getCountryByCode(it)
        } ?: CountryDataSource.DEFAULT_COUNTRY
    }
}
