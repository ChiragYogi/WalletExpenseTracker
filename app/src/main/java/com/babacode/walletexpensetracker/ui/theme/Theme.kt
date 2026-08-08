package com.babacode.walletexpensetracker.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = ColorPrimary,
    secondary = ColorSecondary,
    background = BackgroundLight,
    surface = BackgroundLight,
    onBackground = OnBackgroundLight,
    onSurface = OnBackgroundLight,
    onSurfaceVariant = SummaryTextLight,
)

private val DarkColorScheme = darkColorScheme(
    primary = ColorPrimary,
    secondary = ColorSecondary,
    background = BackgroundDark,
    surface = BackgroundDark,
    onBackground = OnBackgroundDark,
    onSurface = OnBackgroundDark,
    onSurfaceVariant = SummaryTextDark,
)

@Composable
fun WalletExpenseTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(colorScheme = colorScheme, content = content)
}
