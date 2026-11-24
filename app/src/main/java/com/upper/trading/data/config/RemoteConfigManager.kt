package com.upper.trading.data.config

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.remoteConfigSettings
import com.upper.trading.BuildConfig
import com.upper.trading.data.datasource.CountryDataSource
import com.upper.trading.di.qualifier.ApplicationScope
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.Duration.Companion.milliseconds

@Singleton
class RemoteConfigManager @Inject constructor(
    private val remoteConfig: FirebaseRemoteConfig,
    @param:ApplicationScope private val appScope: CoroutineScope
) {
    private val initComplete = CompletableDeferred<Unit>()

    private val _configState = MutableStateFlow(AppConfig())

    val configState get() = _configState.asStateFlow()

    fun init() {
        appScope.launch {
            try {
                remoteConfig.setConfigSettingsAsync(
                    remoteConfigSettings {
                        minimumFetchIntervalInSeconds = if (BuildConfig.DEBUG) 0 else 43_200
                    }
                ).await()
                remoteConfig.setDefaultsAsync(defaultConfigMap).await()
                parseAndApply(isRemoteSynced = false)
            } catch (_: Exception) {
            } finally {
                if (!initComplete.isCompleted) {
                    initComplete.complete(Unit)
                }
            }

            try {
                if (remoteConfig.fetchAndActivate().await()) {
                    parseAndApply(isRemoteSynced = true)
                }
            } catch (_: Exception) {
            }
        }
    }

    suspend fun getConfigOnce(
        waitForRemote: Boolean = true,
        timeoutMs: Long = 1500L
    ): AppConfig {
        if (waitForRemote) {
            return withTimeoutOrNull(timeoutMs.milliseconds) {
                _configState.first { it.isRemoteSynced }
            } ?: _configState.value
        }
        initComplete.await()
        return _configState.value
    }

    private fun parseAndApply(isRemoteSynced: Boolean) {
        val popularCodes = remoteConfig.getStringList(Keys.POPULAR_COUNTRY_CODES)
        val defaultCode = remoteConfig.getTrimmedString(Keys.DEFAULT_COUNTRY_CODE)
        val current = _configState.value

        _configState.value = current.copy(
            popularCountryCodes = popularCodes.ifEmpty { current.popularCountryCodes },
            defaultCountryCode = defaultCode.ifBlank { current.defaultCountryCode },
            isRemoteSynced = isRemoteSynced
        )
    }

    private val defaultConfigMap: Map<String, Any> = mapOf(
        Keys.POPULAR_COUNTRY_CODES to Json.encodeToString(
            CountryDataSource.POPULAR_COUNTRY.map { it.code }
        ),
        Keys.DEFAULT_COUNTRY_CODE to CountryDataSource.DEFAULT_COUNTRY.code
    )

    object Keys {
        const val POPULAR_COUNTRY_CODES = "popular_country_codes"
        const val DEFAULT_COUNTRY_CODE = "default_country_code"
    }
}
