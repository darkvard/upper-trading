package com.upper.trading.presentation.components.bottomsheet.country

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.upper.trading.R
import com.upper.trading.core.utils.ThemePreviews
import com.upper.trading.core.utils.TradingPreview
import com.upper.trading.data.datasource.CountryDataSource
import com.upper.trading.domain.model.Country
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.toImmutableMap
import kotlinx.collections.immutable.toPersistentList

private const val POPULAR_COUNTRIES_KEY = "POPULAR_COUNTRIES_KEY"
private const val STICKY_HEADER_KEY = "STICKY_HEADER_KEY"
private const val COUNTRIES_KEY = "COUNTRIES_KEY"

@Composable
fun CountryContent(
    groupedCountries: ImmutableMap<Char, List<Country>>,
    popularCountries: ImmutableList<Country>,
    selectedCountry: Country?,
    onCountryClick: (Country) -> Unit,
    modifier: Modifier = Modifier,
    style: CountryBottomSheetStyle = CountryBottomSheetDefaults.style
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(style.countryListSpacing)
    ) {
        if (popularCountries.isNotEmpty()) {
            popularCountryHeader(
                items = popularCountries,
                itemSelected = selectedCountry,
                onClick = onCountryClick,
                style = style
            )
        }

        stickyHeaderWithCountrySection(
            groupedCountries = groupedCountries,
            selectedCountry = selectedCountry,
            onCountryClick = onCountryClick,
            style = style
        )
    }
}

private fun LazyListScope.popularCountryHeader(
    items: ImmutableList<Country>,
    itemSelected: Country? = null,
    onClick: (Country) -> Unit,
    modifier: Modifier = Modifier,
    style: CountryBottomSheetStyle = CountryBottomSheetDefaults.style
) {
    item(key = POPULAR_COUNTRIES_KEY) {
        Column(
            modifier = modifier.fillMaxWidth()
        ) {
            Text(
                text = stringResource(R.string.popular_countries).uppercase(),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(style.popularHeaderSpacingBottom))

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(style.popularChipSpacing),
                verticalArrangement = Arrangement.spacedBy(style.popularChipSpacing)
            ) {
                items.forEach { country ->
                    PopularCountryChip(
                        item = country,
                        isSelected = country.code == itemSelected?.code,
                        onClick = { onClick(country) },
                        style = style
                    )
                }
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = style.dividerPaddingVertical),
                color = MaterialTheme.colorScheme.outlineVariant
            )
        }
    }
}

private fun LazyListScope.stickyHeaderWithCountrySection(
    groupedCountries: ImmutableMap<Char, List<Country>>,
    selectedCountry: Country? = null,
    onCountryClick: (Country) -> Unit,
    modifier: Modifier = Modifier,
    style: CountryBottomSheetStyle = CountryBottomSheetDefaults.style
) {
    groupedCountries.forEach { (group, countries) ->
        stickyHeader(
            key = "${STICKY_HEADER_KEY}_$group",
            contentType = STICKY_HEADER_KEY
        ) {
            Box(
                modifier = modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(
                        vertical = style.stickyHeaderPaddingVertical,
                        horizontal = style.itemRowPaddingHorizontal
                    )
            ) {
                Text(
                    text = group.toString(),
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.titleSmall
                )
            }
        }

        items(
            count = countries.size,
            key = { index -> countries[index].code },
            contentType = { COUNTRIES_KEY }
        ) { index ->
            val country = countries[index]
            CountryRowItem(
                item = country,
                isSelected = country.code == selectedCountry?.code,
                onClick = { onCountryClick(country) },
                style = style
            )
        }
    }
}

@ThemePreviews
@Composable
fun PreviewCountryContent() {
    TradingPreview {
        val countries = remember {
            mockGroupedCountries()
        }

        val popularCountries = remember(countries) {
            mockPopularCountries()
        }

        CountryContent(
            groupedCountries = countries,
            popularCountries = popularCountries,
            selectedCountry = popularCountries.firstOrNull(),
            onCountryClick = { }
        )
    }
}


internal fun mockPopularCountries(): ImmutableList<Country> {
    return CountryDataSource.POPULAR_COUNTRY.toPersistentList()
}

internal fun mockGroupedCountries(): ImmutableMap<Char, List<Country>> {
    return CountryDataSource.COUNTRIES
        .sortedBy { it.name.lowercase() }
        .groupBy {
            it.name.firstOrNull()?.uppercaseChar() ?: '#'
        }.toImmutableMap()
}