package com.upper.trading.presentation.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.upper.trading.R

val RobotoFontFamily = FontFamily(
    Font(R.font.roboto_regular, FontWeight.Normal),
    Font(R.font.roboto_medium, FontWeight.Medium),
    Font(R.font.roboto_bold, FontWeight.SemiBold),
    Font(R.font.roboto_bold, FontWeight.Bold)
)

object AppTypography {

    fun robotoStyle(
        size: TextUnit,
        lineHeight: TextUnit,
        weight: FontWeight,
        letterSpacing: TextUnit = 0.sp
    ) = TextStyle(
        fontFamily = RobotoFontFamily,
        fontWeight = weight,
        fontSize = size,
        lineHeight = lineHeight,
        letterSpacing = letterSpacing
    )

    // Headline (32, 28, 24)
    val HeadlineLarge = robotoStyle(32.sp, 40.sp, FontWeight.SemiBold, 0.sp)
    val HeadlineMedium = robotoStyle(28.sp, 36.sp, FontWeight.Medium, 0.sp)
    val HeadlineSmall = robotoStyle(24.sp, 32.sp, FontWeight.Medium, 0.sp)

    // Title (20, 16, 14)
    val TitleLarge = robotoStyle(20.sp, 24.sp, FontWeight.Medium, 0.1.sp)
    val TitleMedium = robotoStyle(16.sp, 20.sp, FontWeight.Medium, 0.15.sp)
    val TitleSmall = robotoStyle(14.sp, 20.sp, FontWeight.Medium, 0.1.sp)

    // Label (14, 12, 11)
    val LabelLarge = robotoStyle(14.sp, 20.sp, FontWeight.Medium, 0.1.sp)
    val LabelMedium = robotoStyle(12.sp, 16.sp, FontWeight.Medium, 0.5.sp)
    val LabelSmall = robotoStyle(11.sp, 16.sp, FontWeight.Medium, 0.5.sp)

    // Body (16, 14, 12)
    val BodyLarge = robotoStyle(16.sp, 24.sp, FontWeight.Normal, 0.5.sp)
    val BodyMedium = robotoStyle(14.sp, 20.sp, FontWeight.Normal, 0.25.sp)
    val BodySmall = robotoStyle(12.sp, 16.sp, FontWeight.Normal, 0.4.sp)
}

val UpperTypography = Typography(
    headlineLarge = AppTypography.HeadlineLarge,
    headlineMedium = AppTypography.HeadlineMedium,
    headlineSmall = AppTypography.HeadlineSmall,
    titleLarge = AppTypography.TitleLarge,
    titleMedium = AppTypography.TitleMedium,
    titleSmall = AppTypography.TitleSmall,
    labelLarge = AppTypography.LabelLarge,
    labelMedium = AppTypography.LabelMedium,
    labelSmall = AppTypography.LabelSmall,
    bodyLarge = AppTypography.BodyLarge,
    bodyMedium = AppTypography.BodyMedium,
    bodySmall = AppTypography.BodySmall
)
