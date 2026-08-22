package com.babacode.walletexpensetracker.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// Spacing scale matching the gap/padding values most commonly used across
// the reference design (refrence/src/routes, refrence/src/components/app).
data class Spacing(
    val extraSmall: Dp = 4.dp,
    val small: Dp = 8.dp,
    val medium: Dp = 12.dp,
    val default: Dp = 16.dp,
    val large: Dp = 20.dp,
    val extraLarge: Dp = 24.dp,
    val huge: Dp = 32.dp,
)

val LocalSpacing = staticCompositionLocalOf { Spacing() }
