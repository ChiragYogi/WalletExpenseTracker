package com.babacode.walletexpensetracker.ui.recurring

import com.babacode.walletexpensetracker.data.model.Frequency
import com.babacode.walletexpensetracker.utiles.Extra
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

class RecurringComputeTest {

    private fun long(date: LocalDate) = Extra.convertLocalDateToLong(date)

    @Test
    fun `daily frequency advances by one day`() {
        val result = nextOccurrence(long(LocalDate.of(2026, 2, 10)), Frequency.DAILY)

        assertEquals(long(LocalDate.of(2026, 2, 11)), result)
    }

    @Test
    fun `weekly frequency advances by seven days`() {
        val result = nextOccurrence(long(LocalDate.of(2026, 2, 10)), Frequency.WEEKLY)

        assertEquals(long(LocalDate.of(2026, 2, 17)), result)
    }

    @Test
    fun `monthly frequency advances by one calendar month`() {
        val result = nextOccurrence(long(LocalDate.of(2026, 1, 31)), Frequency.MONTHLY)

        // java.time.LocalDate.plusMonths clamps to the shorter month's last day.
        assertEquals(long(LocalDate.of(2026, 2, 28)), result)
    }

    @Test
    fun `yearly frequency advances by one year`() {
        val result = nextOccurrence(long(LocalDate.of(2026, 2, 10)), Frequency.YEARLY)

        assertEquals(long(LocalDate.of(2027, 2, 10)), result)
    }

    @Test
    fun `yearly frequency handles a leap-day anchor`() {
        val result = nextOccurrence(long(LocalDate.of(2024, 2, 29)), Frequency.YEARLY)

        assertEquals(long(LocalDate.of(2025, 2, 28)), result)
    }
}
