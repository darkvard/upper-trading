package com.upper.trading.presentation.components.bottomsheet.country

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalBottomSheetProperties
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.rememberBottomSheetState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.upper.trading.core.utils.ThemePreviews
import com.upper.trading.core.utils.TradingPreview
import com.upper.trading.domain.model.Country
import com.upper.trading.domain.model.CountryPickerData
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.launch


data class CountryBottomSheetStyle(
    val topCornerRadius: Dp = 28.dp,
    val dragLinePadding: Dp = 12.dp,
    val dragLineWidth: Dp = 32.dp,
    val dragLineHeight: Dp = 4.dp,
    val dragLineAlpha: Float = 0.5f,
    val headerSpacing: Dp = 2.dp,
    val contentPadding: Dp = 16.dp,
    val contentSpacing: Dp = 16.dp,
    val closeIconSize: Dp = 24.dp,
    val closeIconTouchTarget: Dp = 48.dp,
    val searchIconSize: Dp = 24.dp,
    val clearIconSize: Dp = 18.dp,
    val closeBackgroundAlpha: Float = 0.05f,
    val subTitleSize: TextUnit = 11.sp,
    val subTitleLetterSpacing: TextUnit = 0.8.sp,
    val searchPlaceHolderTextSize: TextUnit = 14.sp,
    // Popular Chip Style
    val itemChipPaddingVertical: Dp = 8.dp,
    val itemChipPaddingHorizontal: Dp = 14.dp,
    val itemChipSpacing: Dp = 8.dp,
    val itemChipCorner: Dp = 8.dp,
    val itemChipFlagSize: Dp = 20.dp,
    val itemChipFlagWidth: Dp = 20.dp,
    val itemChipFlagHeight: Dp = 15.dp,
    val itemChipBorderWidth: Dp = 1.dp,
    // Country Row Item Style
    val itemRowCorner: Dp = 10.dp,
    val itemRowPaddingVertical: Dp = 12.dp,
    val itemRowPaddingHorizontal: Dp = 12.dp,
    val itemRowSpacing: Dp = 12.dp,
    val itemRowFlagWidth: Dp = 24.dp,
    val itemRowFlagHeight: Dp = 18.dp,
    val itemRowCheckIconSize: Dp = 20.dp,
    val itemRowCheckSpacing: Dp = 8.dp,
    // Content & List Style
    val popularHeaderSpacingBottom: Dp = 12.dp,
    val popularChipSpacing: Dp = 8.dp,
    val dividerPaddingVertical: Dp = 16.dp,
    val stickyHeaderPaddingVertical: Dp = 8.dp,
    val countryListSpacing: Dp = 0.dp
)

object CountryBottomSheetDefaults {
    val style: CountryBottomSheetStyle = CountryBottomSheetStyle()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CountryBottomSheet(
    countries: CountryPickerData,
    selectedCountry: Country? = null,
    searchQueryState: TextFieldState,
    onSelectCountry: (Country) -> Unit,
    onDismiss: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    sheetMaxHeight: Dp? = null,
    sheetState: SheetState = rememberBottomSheetState(initialValue = SheetValue.Hidden),
    style: CountryBottomSheetStyle = CountryBottomSheetDefaults.style
) {
    val scope = rememberCoroutineScope()

    val closeSheet: (onFinished: (() -> Unit)?) -> Unit = { onFinished ->
        scope.launch { sheetState.hide() }.invokeOnCompletion {
            if (!sheetState.isVisible) {
                onDismiss?.invoke()
                onFinished?.invoke()
            }
        }
    }

    ModalBottomSheet(
        onDismissRequest = { onDismiss?.invoke() },
        modifier = modifier,
        sheetState = sheetState,
        shape = RoundedCornerShape(
            topStart = style.topCornerRadius,
            topEnd = style.topCornerRadius,
            bottomStart = 0.dp,
            bottomEnd = 0.dp
        ),
        containerColor = MaterialTheme.colorScheme.surface,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = style.dragLinePadding)
                    .size(width = style.dragLineWidth, height = style.dragLineHeight)
                    .background(Color.LightGray.copy(alpha = style.dragLineAlpha), CircleShape)
            )
        },
        properties = ModalBottomSheetProperties(
            shouldDismissOnClickOutside = searchQueryState.text.isEmpty()
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .then(
                    if (sheetMaxHeight != null) {
                        Modifier.height(sheetMaxHeight)
                    } else {
                        Modifier.fillMaxHeight(0.75f)
                    }
                )
                .padding(horizontal = style.contentPadding)
                .navigationBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(style.contentSpacing)
        ) {
            TitleHeaderAction(
                onCloseClick = {
                    closeSheet(null)
                },
                style = style
            )

            SearchCountryBar(
                searchQueryState = searchQueryState,
                style = style
            )

            CountryContent(
                groupedCountries = countries.groupedCountries,
                popularCountries = if (searchQueryState.text.isEmpty()) countries.popularCountries else persistentListOf(),
                selectedCountry = selectedCountry,
                onCountryClick = { country ->
                    onSelectCountry(country)
                },
                searchQuery = searchQueryState.text.toString(),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                style = style
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@ThemePreviews
@Composable
fun PreviewCountryBottomSheet() {
    TradingPreview(modifier = Modifier.fillMaxSize()) {
        val countries = remember {
            val popularCountries = mockPopularCountries()
            CountryPickerData(
                popularCountries = popularCountries,
                groupedCountries = mockGroupedCountries()
            )
        }

        val previewSheetState = rememberBottomSheetState(
            initialValue = SheetValue.Expanded,
            enabledValues = setOf(SheetValue.Hidden, SheetValue.Expanded)
        )

        val searchQueryState = rememberTextFieldState()

        CountryBottomSheet(
            countries = countries,
            selectedCountry = countries.popularCountries.firstOrNull(),
            searchQueryState = searchQueryState,
            onSelectCountry = {},
            sheetState = previewSheetState,
        )

    }
}

