package com.upper.trading.data.repository

import android.content.Context
import android.telephony.TelephonyManager
import com.upper.trading.data.config.RemoteConfigManager
import com.upper.trading.data.local.datasource.CountryLocalDataSource
import com.upper.trading.di.qualifier.DefaultDispatcher
import com.upper.trading.domain.model.Country
import com.upper.trading.domain.repository.CountryRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CountryRepositoryImpl @Inject constructor(
    @param:ApplicationContext private val context: Context,
    @param:DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher,
    private val remoteConfig: RemoteConfigManager
) : CountryRepository {

    private val countryMap: Map<String, Country> by lazy {
        CountryLocalDataSource.COUNTRIES.associateBy { it.code.uppercase() }
    }

    override suspend fun getCountries(): List<Country> = withContext(defaultDispatcher) {
        CountryLocalDataSource.COUNTRIES
    }

    override suspend fun getPopularCountries(): List<Country> = withContext(defaultDispatcher) {
        remoteConfig.getConfigOnce(waitForRemote = true).popularCountryCodes
            .mapNotNull { code -> getCountryByCode(code) }
            .ifEmpty { CountryLocalDataSource.POPULAR_COUNTRY }
    }

    override fun getCountryByCode(code: String): Country? {
        return countryMap[code.trim().uppercase()]
    }

    override suspend fun getDeviceCountryCode(): String? {
        return remoteConfig.getConfigOnce(waitForRemote = true).defaultCountryCode.ifBlank {
            (context.getSystemService(Context.TELEPHONY_SERVICE) as? TelephonyManager)?.let {
                val simCountry = it.simCountryIso?.trim().orEmpty()
                if (simCountry.length == 2) return simCountry.uppercase()

                val networkCountry = it.networkCountryIso?.trim().orEmpty()
                if (networkCountry.length == 2) return networkCountry.uppercase()

                val localeCountry = context.resources.configuration.locales[0].country.trim()
                if (localeCountry.length == 2) return localeCountry.uppercase()

                null
            }
            null
        }
    }

    override suspend fun getDefaultCountry(): Country = withContext(defaultDispatcher) {
        getDeviceCountryCode()?.let {
            getCountryByCode(it)
        } ?: CountryLocalDataSource.DEFAULT_COUNTRY
    }
}
