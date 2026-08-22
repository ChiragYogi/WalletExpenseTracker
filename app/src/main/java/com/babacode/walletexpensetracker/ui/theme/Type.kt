package com.babacode.walletexpensetracker.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import com.babacode.walletexpensetracker.R

// Two Google Fonts ported from the reference design (refrence/src/routes/__root.tsx,
// refrence/src/styles.css: --font-sans / --font-display): DM Sans for body/UI text,
// Space Grotesk for headings and large numeric displays (amounts, totals).
// Both are bundled as single variable-font files (res/font/); each weight below
// selects a specific instance via FontVariation so no separate static files are needed.

@OptIn(ExperimentalTextApi::class)
private fun dmSansWeight(weight: Int, fontWeight: FontWeight) = Font(
    resId = R.font.dm_sans_variable,
    weight = fontWeight,
    variationSettings = FontVariation.Settings(FontVariation.weight(weight)),
)

@OptIn(ExperimentalTextApi::class)
private fun spaceGroteskWeight(weight: Int, fontWeight: FontWeight) = Font(
    resId = R.font.space_grotesk_variable,
    weight = fontWeight,
    variationSettings = FontVariation.Settings(FontVariation.weight(weight)),
)

val DmSans = FontFamily(
    dmSansWeight(400, FontWeight.Normal),
    dmSansWeight(500, FontWeight.Medium),
    dmSansWeight(600, FontWeight.SemiBold),
)

val SpaceGrotesk = FontFamily(
    spaceGroteskWeight(500, FontWeight.Medium),
    spaceGroteskWeight(600, FontWeight.SemiBold),
    spaceGroteskWeight(700, FontWeight.Bold),
)

private val defaultTypography = Typography()

val WalletTypography = Typography(
    displayLarge = defaultTypography.displayLarge.copy(fontFamily = SpaceGrotesk, fontWeight = FontWeight.SemiBold),
    displayMedium = defaultTypography.displayMedium.copy(fontFamily = SpaceGrotesk, fontWeight = FontWeight.SemiBold),
    displaySmall = defaultTypography.displaySmall.copy(fontFamily = SpaceGrotesk, fontWeight = FontWeight.SemiBold),
    headlineLarge = defaultTypography.headlineLarge.copy(fontFamily = SpaceGrotesk, fontWeight = FontWeight.SemiBold),
    headlineMedium = defaultTypography.headlineMedium.copy(fontFamily = SpaceGrotesk, fontWeight = FontWeight.SemiBold),
    headlineSmall = defaultTypography.headlineSmall.copy(fontFamily = SpaceGrotesk, fontWeight = FontWeight.Medium),
    titleLarge = defaultTypography.titleLarge.copy(fontFamily = SpaceGrotesk, fontWeight = FontWeight.Medium),
    titleMedium = defaultTypography.titleMedium.copy(fontFamily = DmSans, fontWeight = FontWeight.SemiBold),
    titleSmall = defaultTypography.titleSmall.copy(fontFamily = DmSans, fontWeight = FontWeight.SemiBold),
    bodyLarge = defaultTypography.bodyLarge.copy(fontFamily = DmSans, fontWeight = FontWeight.Normal),
    bodyMedium = defaultTypography.bodyMedium.copy(fontFamily = DmSans, fontWeight = FontWeight.Normal),
    bodySmall = defaultTypography.bodySmall.copy(fontFamily = DmSans, fontWeight = FontWeight.Normal),
    labelLarge = defaultTypography.labelLarge.copy(fontFamily = DmSans, fontWeight = FontWeight.Medium),
    labelMedium = defaultTypography.labelMedium.copy(fontFamily = DmSans, fontWeight = FontWeight.Medium),
    labelSmall = defaultTypography.labelSmall.copy(fontFamily = DmSans, fontWeight = FontWeight.Medium),
)
