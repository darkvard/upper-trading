package com.upper.trading.presentation.components.bottomsheet.country

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.upper.trading.R
import com.upper.trading.core.utils.bounceClick
import com.upper.trading.presentation.theme.purple5

@Composable
internal fun TitleHeaderAction(
    onCloseClick: () -> Unit,
    modifier: Modifier = Modifier,
    style: CountryBottomSheetStyle = CountryBottomSheetDefaults.style
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(style.headerSpacing)) {
            Text(
                text = stringResource(R.string.phone_number).uppercase(),
                fontSize = style.subTitleSize,
                fontWeight = FontWeight.Bold,
                color = purple5,
                letterSpacing = style.subTitleLetterSpacing
            )

            Text(
                text = stringResource(R.string.select_country),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        }

        Box(
            modifier = Modifier
                .size(style.closeIconTouchTarget)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.onSurface.copy(alpha = style.closeBackgroundAlpha))
                .bounceClick { onCloseClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Rounded.Close,
                contentDescription = "Close",
                modifier = Modifier.size(style.closeIconSize)
            )
        }
    }

}