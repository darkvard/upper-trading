package com.upper.trading.domain.model

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Immutable
import com.upper.trading.R
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf

@Immutable
data class Country(
    val code: String = "VN",
    val name: String = "Vietnam",
    val dialCode: String = "+84",
    @DrawableRes val flagRes: Int = R.drawable.ic_flag_vn,
)

@Immutable
data class CountryPickerData(
    val popularCountries: ImmutableList<Country> = persistentListOf(),
    val groupedCountries: ImmutableMap<Char, List<Country>> = persistentMapOf()
)