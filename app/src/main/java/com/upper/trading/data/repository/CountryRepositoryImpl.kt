package com.upper.trading.data.repository

import android.content.Context
import android.telephony.TelephonyManager
import com.upper.trading.R
import com.upper.trading.data.datasource.CountryDataSource
import com.upper.trading.domain.model.Country
import com.upper.trading.domain.repository.CountryRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class CountryRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
) : CountryRepository {

    private val countryMap: Map<String, Country> by lazy {
        getCountries().associateBy { it.code.uppercase() }
    }

    override fun getCountries(): List<Country> = CountryDataSource.COUNTRIES

    override fun getPopularCountries(): List<Country> = CountryDataSource.POPULAR_COUNTRY

    override fun getCountryByCode(code: String): Country? {
        return countryMap[code.trim().uppercase()]
    }

    override fun getDeviceCountryCode(): String? {
        val tm = context.getSystemService(Context.TELEPHONY_SERVICE) as? TelephonyManager

        val simCountry = tm?.simCountryIso?.trim().orEmpty()
        if (simCountry.length == 2) return simCountry.uppercase()

        val networkCountry = tm?.networkCountryIso?.trim().orEmpty()
        if (networkCountry.length == 2) return networkCountry.uppercase()

        val localeCountry = context.resources.configuration.locales[0].country.trim()
        if (localeCountry.length == 2) return localeCountry.uppercase()

        return null
    }
}
