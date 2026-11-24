package com.upper.trading.domain.repository

import com.upper.trading.domain.model.Country

interface CountryRepository {
    suspend fun getCountries(): List<Country>

    suspend fun getPopularCountries(): List<Country>

    fun getCountryByCode(code: String): Country?

    suspend fun getDeviceCountryCode(): String?
}
