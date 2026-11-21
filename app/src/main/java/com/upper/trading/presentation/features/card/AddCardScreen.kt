package com.upper.trading.presentation.features.card

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.upper.trading.core.utils.ThemePreviews
import com.upper.trading.core.utils.TradingPreview
import com.upper.trading.presentation.components.button.AppPrimaryButton
import com.upper.trading.presentation.components.layout.ProgressBarActionLayout
import com.upper.trading.presentation.components.layout.ProgressBarState

@Composable
fun AddCardScreen(
    onBackClick: () -> Unit,
    onCloseClick: () -> Unit,
    onNextClick: () -> Unit,
    modifier: Modifier = Modifier,
    progressBarState: ProgressBarState? = null
) {
    ProgressBarActionLayout(
        progressBarState = progressBarState,
        onBackClick = onBackClick,
        onCloseClick = onCloseClick,
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Add Your Card",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(8.dp))

                val progressText = progressBarState?.let {
                    "\nProgress: Step ${it.current.toInt()} of ${it.total.toInt()} (${(it.current / it.total * 100).toInt()}%)"
                }.orEmpty()

                Text(
                    text = "Enter Credit / Debit Card Information$progressText",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(32.dp))

                AppPrimaryButton(
                    text = "Confirm Card Info",
                    onClick = onNextClick
                )
            }
        }
    }
}

@ThemePreviews
@Composable
fun PreviewAddCardScreen() {
    TradingPreview {
        AddCardScreen(
            onBackClick = {},
            onCloseClick = {},
            onNextClick = {}
        )
    }
}
