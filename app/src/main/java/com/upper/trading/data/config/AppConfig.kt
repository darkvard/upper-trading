package com.upper.trading.data.config


data class AppConfig(
    val popularCountryCodes: List<String> = emptyList(),
    val defaultCountryCode: String = "",
    val isRemoteSynced: Boolean = false
)