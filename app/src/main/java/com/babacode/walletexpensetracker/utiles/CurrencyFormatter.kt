package com.babacode.walletexpensetracker.utiles

import java.text.NumberFormat
import java.util.Locale
import kotlin.math.abs
import kotlin.math.roundToInt

// Port of the reference design's formatMoney (refrence/src/lib/finance/format.ts).
// Uses en-IN digit grouping (lakhs/crores, e.g. "1,00,000") regardless of device
// locale, matching the reference's `Intl.NumberFormat("en-IN")` behaviour.
private val enInNumberFormat: NumberFormat =
    NumberFormat.getInstance(Locale.Builder().setLanguage("en").setRegion("IN").build())

fun formatMoney(amount: Double, currencySymbol: String, compact: Boolean = false): String {
    val sign = if (amount < 0) "-" else ""
    val absAmount = abs(amount)

    if (compact && absAmount >= 1000) {
        val inThousands = absAmount / 1000
        val roundedToOneDecimal = (inThousands * 10).roundToInt() / 10.0
        val formattedThousands = if (roundedToOneDecimal % 1.0 == 0.0) {
            roundedToOneDecimal.toInt().toString()
        } else {
            "%.1f".format(roundedToOneDecimal)
        }
        return "$sign$currencySymbol${formattedThousands}k"
    }

    enInNumberFormat.maximumFractionDigits = if (absAmount % 1.0 == 0.0) 0 else 2
    enInNumberFormat.minimumFractionDigits = 0
    return "$sign$currencySymbol${enInNumberFormat.format(absAmount)}"
}
