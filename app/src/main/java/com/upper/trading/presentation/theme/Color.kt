package com.upper.trading.presentation.theme

import androidx.compose.ui.graphics.Color

// ─────────────────────────────────────────────
// Raw Color Palette — Purple
// ─────────────────────────────────────────────
val purple05 = Color(0xFFEFECFC)
val purple1  = Color(0xFFDFD8FA)
val purple15 = Color(0xFFD0C5F7)
val purple2  = Color(0xFFC0B1F4)
val purple3  = Color(0xFFA08BEF)
val purple4  = Color(0xFF8164E9)
val purple5  = Color(0xFF613DE4)
val purple6  = Color(0xFF4E31B6)
val purple7  = Color(0xFF3A2589)
val purple8  = Color(0xFF27185B)
val purple85 = Color(0xFF1D1244)

// ─────────────────────────────────────────────
// Raw Color Palette — Yellow
// ─────────────────────────────────────────────
val yellow05 = Color(0xFFFFFAE8)
val yellow1  = Color(0xFFFEF5D0)
val yellow15 = Color(0xFFFEF0B9)
val yellow2  = Color(0xFFFDEBA1)
val yellow3  = Color(0xFFFCE073)
val yellow4  = Color(0xFFFCE073)
val yellow5  = Color(0xFFFACC15)
val yellow6  = Color(0xFFC8A311)
val yellow7  = Color(0xFF967A0D)
val yellow8  = Color(0xFF645208)
val yellow85 = Color(0xFF4B3D06)

// ─────────────────────────────────────────────
// Raw Color Palette — Red
// ─────────────────────────────────────────────
val red05 = Color(0xFFFDECEC)
val red1  = Color(0xFFFCDADA)
val red15 = Color(0xFFFAC7C7)
val red2  = Color(0xFFF9B4B4)
val red3  = Color(0xFFF58F8F)
val red4  = Color(0xFFF26969)
val red5  = Color(0xFFEF4444)
val red6  = Color(0xFFC13737)
val red7  = Color(0xFF932929)
val red8  = Color(0xFF661C1C)
val red85 = Color(0xFF4F1515)

// ─────────────────────────────────────────────
// Raw Color Palette — Green
// ─────────────────────────────────────────────
val green05 = Color(0xFFEDFCF2)
val green1  = Color(0xFFDBF8E6)
val green15 = Color(0xFFC9F5D9)
val green2  = Color(0xFFB7F2CC)
val green3  = Color(0xFF92EBB3)
val green4  = Color(0xFF6EE599)
val green5  = Color(0xFF4ADE80)
val green6  = Color(0xFF3BB266)
val green7  = Color(0xFF2C854D)
val green8  = Color(0xFF1E5933)
val green85 = Color(0xFF164326)

// ─────────────────────────────────────────────
// Raw Color Palette — Orange
// ─────────────────────────────────────────────
val orange05 = Color(0xFFFEF5E7)
val orange1  = Color(0xFFFDECCE)
val orange15 = Color(0xFFFCE2B6)
val orange2  = Color(0xFFFBD89D)
val orange3  = Color(0xFFF9C56D)
val orange4  = Color(0xFFF7B13C)
val orange5  = Color(0xFFF59E0B)
val orange6  = Color(0xFFC47E09)
val orange7  = Color(0xFF935F07)
val orange8  = Color(0xFF623F04)
val orange85 = Color(0xFF4A2F03)

// ─────────────────────────────────────────────
// Raw Color Palette — Blue Shades (cool neutral)
// ─────────────────────────────────────────────
val blueShades0  = Color(0xFFFFFFFF)
val blueShades05 = Color(0xFFFEFEFF)
val blueShades1  = Color(0xFFFCFDFE)
val blueShades15 = Color(0xFFFCFDFE)
val blueShades2  = Color(0xFFFBFCFE)
val blueShades3  = Color(0xFFF9FAFD)
val blueShades4  = Color(0xFFF7F9FD)
val blueShades5  = Color(0xFFF5F7FC)
val blueShades6  = Color(0xFFCDD5E9)
val blueShades7  = Color(0xFFA5B3D5)
val blueShades8  = Color(0xFF7E91C2)
val blueShades85 = Color(0xFF6A80B8)
val blueShades9  = Color(0xFF566FAE)
val blueShades95 = Color(0xFF425EA5)
val blueShades10 = Color(0xFF2E4D9B)

val darkBlueShades5  = Color(0xFF101828)
val darkBlueShades2  = Color(0xFF1D2939)

// ─────────────────────────────────────────────
// Raw Color Palette — Black Shades (gray neutral)
// ─────────────────────────────────────────────
val blackShades0  = Color(0xFFFFFFFF)
val blackShades05 = Color(0xFFF6F6F7)
val blackShades1  = Color(0xFFECECEE)
val blackShades15 = Color(0xFFE3E3E6)
val blackShades2  = Color(0xFFD9D9DD)
val backShades25 = Color(0xFFD4D4D4)
val blackShades3  = Color(0xFFC7C7CC)
val blackShades4  = Color(0xFFB4B4BB)
val blackShades5  = Color(0xFFA1A1AA)
val blackShades6  = Color(0xFF84848C)
val blackShades7  = Color(0xFF67676D)
val blackShades8  = Color(0xFF4B4B4F)
val blackShades85 = Color(0xFF3C3C40)
val blackShades9  = Color(0xFF2E2E30)
val blackShades95 = Color(0xFF1F1F21)
val blackShades10 = Color(0xFF111112)

// ─────────────────────────────────────────────
// App Semantic Colors
// Trading-specific aliases and status colors
// that do not map to Material3 slots
// ─────────────────────────────────────────────
object AppColor {
    // Trading price direction
    val bullish        = green6                   // #3BB266 — price up
    val bullishSurface = Color(0x1A3BB266)        // 10% opacity
    val bearish        = red5                     // #EF4444 — price down
    val bearishSurface = Color(0x1AEF4444)        // 10% opacity

    // Status colors (use directly when theme-independence is needed)
    val attention = yellow5                       // #FACC15
    val warning   = orange5                       // #F59E0B
    val error     = red5                          // #EF4444
    val success   = green6                        // #3BB266

    // Fixed primary surface (not theme-toggled)
    val primarySurface = purple5                  // #613DE4
    val textWhite = Color(0xFFF2F4F7)
    val borderBlueGray = Color(0xFF475467)
}
