package com.babacode.walletexpensetracker.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

// Semantic color roles used by the reference design that have no equivalent
// Material3 ColorScheme slot (refrence/src/styles.css: --income, --income-soft,
// --expense, --expense-soft, --warning, --elevated, --chart-5).
data class ExtendedColors(
    val income: Color,
    val incomeSoft: Color,
    val expense: Color,
    val expenseSoft: Color,
    val warning: Color,
    val elevated: Color,
    val chartFive: Color,
)

private val LightExtendedColors = ExtendedColors(
    income = IncomeLight,
    incomeSoft = IncomeSoftLight,
    expense = ExpenseLight,
    expenseSoft = ExpenseSoftLight,
    warning = WarningLight,
    elevated = ElevatedLight,
    chartFive = ChartFiveLight,
)

private val DarkExtendedColors = ExtendedColors(
    income = IncomeDark,
    incomeSoft = IncomeSoftDark,
    expense = ExpenseDark,
    expenseSoft = ExpenseSoftDark,
    warning = WarningDark,
    elevated = ElevatedDark,
    chartFive = ChartFiveDark,
)

val LocalExtendedColors = staticCompositionLocalOf { LightExtendedColors }

private val LightColorScheme = lightColorScheme(
    primary = PrimaryLight,
    onPrimary = PrimaryForegroundLight,
    secondary = SecondaryLight,
    onSecondary = SecondaryForegroundLight,
    background = BackgroundLight,
    onBackground = ForegroundLight,
    surface = CardLight,
    onSurface = CardForegroundLight,
    surfaceVariant = MutedLight,
    onSurfaceVariant = MutedForegroundLight,
    tertiary = AccentLight,
    onTertiary = AccentForegroundLight,
    error = DestructiveLight,
    onError = DestructiveForegroundLight,
    outline = BorderLight,
    outlineVariant = BorderLight,
)

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryDark,
    onPrimary = PrimaryForegroundDark,
    secondary = SecondaryDark,
    onSecondary = SecondaryForegroundDark,
    background = BackgroundDark,
    onBackground = ForegroundDark,
    surface = CardDark,
    onSurface = CardForegroundDark,
    surfaceVariant = MutedDark,
    onSurfaceVariant = MutedForegroundDark,
    tertiary = AccentDark,
    onTertiary = AccentForegroundDark,
    error = DestructiveDark,
    onError = DestructiveForegroundDark,
    outline = BorderDark,
    outlineVariant = BorderDark,
)

@Composable
fun WalletExpenseTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val extendedColors = if (darkTheme) DarkExtendedColors else LightExtendedColors

    CompositionLocalProvider(
        LocalExtendedColors provides extendedColors,
        LocalSpacing provides Spacing(),
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            shapes = WalletShapes,
            typography = WalletTypography,
            content = content,
        )
    }
}

// Convenience accessors, e.g. `WalletTheme.extendedColors.income`.
object WalletTheme {
    val extendedColors: ExtendedColors
        @Composable get() = LocalExtendedColors.current

    val spacing: Spacing
        @Composable get() = LocalSpacing.current
}
