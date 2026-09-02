package com.babacode.walletexpensetracker.ui.detail

import com.babacode.walletexpensetracker.data.model.TransactionType
import com.babacode.walletexpensetracker.fake.sampleTransaction
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

class DetailTrendTest {

    @Test
    fun `daily buckets span a rolling 7-day window ending at the cursor`() {
        val cursor = LocalDate.of(2026, 2, 10)
        val inWindow = sampleTransaction(id = 1, date = LocalDate.of(2026, 2, 4), amount = 40.0)
        val onCursor = sampleTransaction(id = 2, date = cursor, amount = 60.0)
        val beforeWindow = sampleTransaction(id = 3, date = LocalDate.of(2026, 2, 3), amount = 999.0)

        val buckets = buildTrendBuckets(
            DetailPeriod.DAILY, cursor, listOf(inWindow, onCursor, beforeWindow), TransactionType.EXPENSE
        )

        assertEquals(7, buckets.size)
        assertEquals(40.0, buckets.first().amount, 0.0)
        assertEquals(60.0, buckets.last().amount, 0.0)
        // beforeWindow (999.0) falls outside the 7-day window and must not appear anywhere.
        assertEquals(100.0, buckets.sumOf { it.amount }, 0.0)
    }

    @Test
    fun `weekly buckets start on Monday regardless of the cursor's weekday`() {
        val cursor = LocalDate.of(2026, 2, 12) // a Thursday
        val monday = sampleTransaction(id = 1, date = LocalDate.of(2026, 2, 9), amount = 10.0)
        val sunday = sampleTransaction(id = 2, date = LocalDate.of(2026, 2, 15), amount = 20.0)

        val buckets = buildTrendBuckets(DetailPeriod.WEEKLY, cursor, listOf(monday, sunday), TransactionType.EXPENSE)

        assertEquals(7, buckets.size)
        assertEquals(10.0, buckets.first().amount, 0.0)
        assertEquals(20.0, buckets.last().amount, 0.0)
    }

    @Test
    fun `monthly buckets cover every day of the cursor's month`() {
        val cursor = LocalDate.of(2026, 2, 15) // February 2026 has 28 days
        val firstDay = sampleTransaction(id = 1, date = LocalDate.of(2026, 2, 1), amount = 5.0)
        val lastDay = sampleTransaction(id = 2, date = LocalDate.of(2026, 2, 28), amount = 7.0)

        val buckets = buildTrendBuckets(DetailPeriod.MONTHLY, cursor, listOf(firstDay, lastDay), TransactionType.EXPENSE)

        assertEquals(28, buckets.size)
        assertEquals(5.0, buckets.first().amount, 0.0)
        assertEquals(7.0, buckets.last().amount, 0.0)
    }

    @Test
    fun `yearly buckets are 12 months and scope by the cursor's own year`() {
        val cursor = LocalDate.of(2026, 6, 1)
        val thisMarch = sampleTransaction(id = 1, date = LocalDate.of(2026, 3, 15), amount = 30.0)
        val lastYearMarch = sampleTransaction(id = 2, date = LocalDate.of(2025, 3, 15), amount = 999.0)

        val buckets = buildTrendBuckets(
            DetailPeriod.YEARLY, cursor, listOf(thisMarch, lastYearMarch), TransactionType.EXPENSE
        )

        assertEquals(12, buckets.size)
        assertEquals("Mar", buckets[2].label)
        assertEquals(30.0, buckets[2].amount, 0.0)
    }

    @Test
    fun `buckets only include transactions matching the requested trend type`() {
        val cursor = LocalDate.of(2026, 2, 10)
        val expense = sampleTransaction(id = 1, date = cursor, type = TransactionType.EXPENSE, amount = 40.0)
        val income = sampleTransaction(id = 2, date = cursor, type = TransactionType.INCOME, amount = 50000.0)

        val buckets = buildTrendBuckets(DetailPeriod.DAILY, cursor, listOf(expense, income), TransactionType.EXPENSE)

        assertEquals(40.0, buckets.last().amount, 0.0)
    }
}
