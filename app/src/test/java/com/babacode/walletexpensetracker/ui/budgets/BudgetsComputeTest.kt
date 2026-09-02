package com.babacode.walletexpensetracker.ui.budgets

import com.babacode.walletexpensetracker.data.model.Budget
import com.babacode.walletexpensetracker.data.model.TransactionType
import com.babacode.walletexpensetracker.fake.sampleTransaction
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class BudgetsComputeTest {

    private val now = LocalDate.of(2026, 2, 15) // Feb 2026 has 28 days; 14 days left inclusive.

    @Test
    fun `a budget under its limit is not flagged as over`() {
        val food = sampleTransaction(id = 1, date = now, type = TransactionType.EXPENSE, tag = "Food", amount = 200.0)
        val budget = Budget(tag = "Food", limitAmount = 500.0, id = 1)

        val state = buildBudgetsUiState(listOf(food), listOf(budget), now)

        val row = state.rows.single()
        assertEquals(200.0, row.used, 0.0)
        assertEquals(40, row.percent)
        assertEquals(300.0, row.remaining, 0.0)
        assertTrue(!row.isOver)
        assertTrue(state.overspentTags.isEmpty())
    }

    @Test
    fun `a budget over its limit is flagged and reported in overspentTags`() {
        val food = sampleTransaction(id = 1, date = now, type = TransactionType.EXPENSE, tag = "Food", amount = 600.0)
        val budget = Budget(tag = "Food", limitAmount = 500.0, id = 1)

        val state = buildBudgetsUiState(listOf(food), listOf(budget), now)

        val row = state.rows.single()
        assertTrue(row.isOver)
        assertEquals(-100.0, row.remaining, 0.0)
        assertEquals(listOf("Food"), state.overspentTags)
    }

    @Test
    fun `a budget with no spending this month reports zero used`() {
        val budget = Budget(tag = "Rent", limitAmount = 1000.0, id = 1)

        val state = buildBudgetsUiState(emptyList(), listOf(budget), now)

        val row = state.rows.single()
        assertEquals(0.0, row.used, 0.0)
        assertEquals(0, row.percent)
    }

    @Test
    fun `spending on an un-budgeted tag does not create a row`() {
        val other = sampleTransaction(id = 1, date = now, type = TransactionType.EXPENSE, tag = "Other", amount = 999.0)
        val budget = Budget(tag = "Food", limitAmount = 500.0, id = 1)

        val state = buildBudgetsUiState(listOf(other), listOf(budget), now)

        assertEquals(0.0, state.rows.single().used, 0.0)
    }

    @Test
    fun `daysLeft counts today plus remaining days in the month`() {
        val state = buildBudgetsUiState(emptyList(), emptyList(), now)

        assertEquals(14, state.daysLeft)
    }

    @Test
    fun `spending from a different month is excluded`() {
        val lastMonth =
            sampleTransaction(id = 1, date = LocalDate.of(2026, 1, 20), type = TransactionType.EXPENSE, tag = "Food", amount = 999.0)
        val budget = Budget(tag = "Food", limitAmount = 500.0, id = 1)

        val state = buildBudgetsUiState(listOf(lastMonth), listOf(budget), now)

        assertEquals(0.0, state.rows.single().used, 0.0)
    }
}
