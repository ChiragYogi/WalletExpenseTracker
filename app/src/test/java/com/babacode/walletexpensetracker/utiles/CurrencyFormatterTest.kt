package com.babacode.walletexpensetracker.utiles

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

// Digit-grouping characters (commas) are stripped before comparing: java.text.NumberFormat's
// en-IN grouping is backed by ICU on a real device (correctly produces lakh-style "1,00,000")
// but by the desktop JDK's own (non-lakh) locale data in this local JUnit runner, so asserting
// the exact grouped string here would be environment-dependent rather than a real regression
// check. Stripping commas keeps these tests meaningful (right digits, sign, decimals, symbol,
// compact suffix) without being brittle to that JVM-vs-Android divergence.
private fun String.digitsOnly() = replace(",", "")

class CurrencyFormatterTest {

    @Test
    fun `zero amount formats without a sign`() {
        assertEquals("$0", formatMoney(0.0, "$").digitsOnly())
    }

    @Test
    fun `whole number amount has no decimal places`() {
        assertEquals("$450", formatMoney(450.0, "$").digitsOnly())
    }

    @Test
    fun `fractional amount keeps up to two decimal places`() {
        assertEquals("$450.5", formatMoney(450.5, "$").digitsOnly())
        assertEquals("$450.57", formatMoney(450.567, "$").digitsOnly())
    }

    @Test
    fun `negative amount is prefixed with a minus before the currency symbol`() {
        val result = formatMoney(-450.0, "$")
        assertTrue(result.startsWith("-$"))
        assertEquals("-$450", result.digitsOnly())
    }

    @Test
    fun `lakh-scale amount preserves all digits regardless of grouping scheme`() {
        assertEquals("₹100000", formatMoney(100000.0, "₹").digitsOnly())
    }

    @Test
    fun `crore-scale amount preserves all digits regardless of grouping scheme`() {
        assertEquals("₹10000000", formatMoney(10000000.0, "₹").digitsOnly())
    }

    @Test
    fun `compact mode below 1000 falls back to full formatting`() {
        assertEquals("$999", formatMoney(999.0, "$", compact = true).digitsOnly())
    }

    @Test
    fun `compact mode at or above 1000 uses a k suffix`() {
        assertEquals("$1.5k", formatMoney(1500.0, "$", compact = true))
    }

    @Test
    fun `compact mode drops the decimal when it rounds to a whole thousand`() {
        assertEquals("$2k", formatMoney(2000.0, "$", compact = true))
    }

    @Test
    fun `compact mode rounds to one decimal place`() {
        assertEquals("$1.2k", formatMoney(1249.0, "$", compact = true))
    }

    @Test
    fun `compact mode preserves the negative sign`() {
        assertEquals("-$1.5k", formatMoney(-1500.0, "$", compact = true))
    }
}
