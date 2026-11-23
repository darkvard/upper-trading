package com.upper.trading.domain.repository

import com.upper.trading.domain.model.Country

interface CountryRepository {
    fun getCountries(): List<Country>

    fun getPopularCountries(): List<Country>

    fun getCountryByCode(code: String): Country?

    fun getDeviceCountryCode(): String?
}
