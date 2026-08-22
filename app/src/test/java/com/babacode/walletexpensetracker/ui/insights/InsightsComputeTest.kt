package com.babacode.walletexpensetracker.ui.insights

import com.babacode.walletexpensetracker.data.model.TransactionType
import com.babacode.walletexpensetracker.fake.sampleTransaction
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

class InsightsComputeTest {

    private val now = LocalDate.of(2026, 2, 15)

    @Test
    fun `delta is the percent change in expense versus last month`() {
        val thisMonth = sampleTransaction(id = 1, date = now, type = TransactionType.EXPENSE, amount = 1200.0)
        val lastMonth = sampleTransaction(id = 2, date = LocalDate.of(2026, 1, 15), type = TransactionType.EXPENSE, amount = 1000.0)

        val state = buildInsightsUiState(listOf(thisMonth, lastMonth), now)

        assertEquals(1200.0, state.currentExpense, 0.0)
        assertEquals(1000.0, state.previousExpense, 0.0)
        assertEquals(20, state.deltaPercent)
    }

    @Test
    fun `delta is zero when there was no spending last month to compare against`() {
        val thisMonth = sampleTransaction(id = 1, date = now, type = TransactionType.EXPENSE, amount = 500.0)

        val state = buildInsightsUiState(listOf(thisMonth), now)

        assertEquals(0, state.deltaPercent)
    }

    @Test
    fun `categories are scoped to expenses in the current month only`() {
        val food = sampleTransaction(id = 1, date = now, type = TransactionType.EXPENSE, tag = "Food", amount = 300.0)
        val income = sampleTransaction(id = 2, date = now, type = TransactionType.INCOME, tag = "Salary", amount = 50000.0)
        val lastMonthFood =
            sampleTransaction(id = 3, date = LocalDate.of(2026, 1, 15), type = TransactionType.EXPENSE, tag = "Food", amount = 999.0)

        val state = buildInsightsUiState(listOf(food, income, lastMonthFood), now)

        assertEquals(listOf(CategoryBreakdown("Food", 300.0)), state.categories)
    }

    @Test
    fun `top spends is the 5 largest expenses this month, expenses only`() {
        val transactions = (1..6).map { i ->
            sampleTransaction(id = i, date = now, type = TransactionType.EXPENSE, amount = i * 100.0)
        } + sampleTransaction(id = 99, date = now, type = TransactionType.INCOME, amount = 999999.0)

        val state = buildInsightsUiState(transactions, now)

        assertEquals(5, state.topSpends.size)
        assertEquals(600.0, state.topSpends.first().amount, 0.0)
        assertEquals(200.0, state.topSpends.last().amount, 0.0)
    }

    @Test
    fun `trend has 6 points ending with the current month`() {
        val state = buildInsightsUiState(emptyList(), now)

        assertEquals(6, state.trend.size)
        assertEquals("Feb", state.trend.last().label)
        assertEquals("Sep", state.trend.first().label)
    }

    @Test
    fun `projected spend scales current pace to the full month`() {
        // now = Feb 15 2026: 15 days elapsed out of a 28-day month.
        val transaction = sampleTransaction(id = 1, date = now, type = TransactionType.EXPENSE, amount = 1500.0)

        val state = buildInsightsUiState(listOf(transaction), now)

        assertEquals(1500.0 / 15 * 28, state.projectedSpend, 0.001)
    }
}
