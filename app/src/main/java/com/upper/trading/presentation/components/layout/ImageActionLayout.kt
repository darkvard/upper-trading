package com.upper.trading.presentation.components.layout

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.upper.trading.R
import com.upper.trading.core.utils.ThemePreviews
import com.upper.trading.presentation.theme.AppColor
import com.upper.trading.presentation.theme.TradingTheme

data class ImageActionStyle(
    val imageSize: Dp = 358.dp,
    val imagePaddingTop: Dp = 40.dp,
    val imagePaddingHorizontal: Dp = 26.dp,
    val actionPaddingTop: Dp = 24.dp,
    val actionPaddingEnd: Dp = 27.dp,
    val bottomPaddingHorizontal: Dp = 16.dp,
    val bottomPaddingBottom: Dp = 40.dp,
    val bottomSpacing: Dp = 24.dp,
)

object ImageActionDefaults {
    val style = ImageActionStyle()
}

@Composable
fun ImageActionLayout(
    imagePainter: Painter,
    modifier: Modifier = Modifier,
    actionText: String? = null,
    onActionClick: () -> Unit = {},
    style: ImageActionStyle = ImageActionDefaults.style,
    bottomContent: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .paint(
                painterResource(R.drawable.img_theme_background),
                contentScale = ContentScale.Crop
            )
            .statusBarsPadding()
            .navigationBarsPadding(),
    ) {
        if (actionText != null) {
            Text(
                text = actionText,
                style = MaterialTheme.typography.titleMedium,
                color = AppColor.primarySurface,
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(top = style.actionPaddingTop, end = style.actionPaddingEnd)
                    .clickable { onActionClick() }
            )
        }

        Image(
            painter = imagePainter,
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(
                    top = style.imagePaddingTop,
                    start = style.imagePaddingHorizontal,
                    end = style.imagePaddingHorizontal
                )
                .size(style.imageSize)
        )

        Spacer(modifier = Modifier.weight(1f))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = style.bottomPaddingHorizontal,
                    end = style.bottomPaddingHorizontal,
                    bottom = style.bottomPaddingBottom
                ),
            verticalArrangement = Arrangement.spacedBy(style.bottomSpacing),
            horizontalAlignment = Alignment.CenterHorizontally,
            content = bottomContent
        )
    }
}


@ThemePreviews
@Composable
private fun PreviewImageActionLayout() {
    TradingTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            ImageActionLayout(
                imagePainter = painterResource(R.drawable.img_theme),
                actionText = "Skip",
                onActionClick = {}
            ) {
                Text(
                    text = "You can immediately select a color theme",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center
                )
                Button(
                    onClick = {},
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(text = "Next")
                }
            }
        }
    }
}