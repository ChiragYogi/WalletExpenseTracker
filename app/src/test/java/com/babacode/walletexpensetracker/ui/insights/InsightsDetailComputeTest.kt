package com.babacode.walletexpensetracker.ui.insights

import com.babacode.walletexpensetracker.data.model.PaymentMode
import com.babacode.walletexpensetracker.data.model.TransactionType
import com.babacode.walletexpensetracker.fake.sampleTransaction
import com.babacode.walletexpensetracker.ui.navigation.InsightKind
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

class InsightsDetailComputeTest {

    private val now = LocalDate.of(2026, 2, 15)

    @Test
    fun `tag kind filters to matching expense tag this month`() {
        val food = sampleTransaction(id = 1, date = now, type = TransactionType.EXPENSE, tag = "Food", amount = 100.0)
        val rent = sampleTransaction(id = 2, date = now, type = TransactionType.EXPENSE, tag = "Rent", amount = 300.0)

        val state = buildInsightsDetailUiState(InsightKind.TAG, "Food", 0, listOf(food, rent), now)

        assertEquals(listOf(food), state.transactions)
        assertEquals("Food", state.title)
        assertEquals(InsightsDetailSubtitleKind.CATEGORY, state.subtitleKind)
        assertEquals(false, state.isNetHeadline)
        assertEquals(100.0, state.headlineAmount, 0.0)
    }

    @Test
    fun `mode kind filters to matching payment mode this month`() {
        val cash = sampleTransaction(id = 1, date = now, type = TransactionType.EXPENSE, mode = PaymentMode.CASH, amount = 50.0)
        val upi = sampleTransaction(id = 2, date = now, type = TransactionType.EXPENSE, mode = PaymentMode.UPI, amount = 75.0)

        val state = buildInsightsDetailUiState(InsightKind.MODE, "UPI", 0, listOf(cash, upi), now)

        assertEquals(listOf(upi), state.transactions)
        assertEquals(InsightsDetailSubtitleKind.PAYMENT_MODE, state.subtitleKind)
    }

    @Test
    fun `top kind takes the 10 largest expenses sorted by amount, not by date`() {
        val transactions = (1..12).map { i ->
            sampleTransaction(id = i, date = now.minusDays(i.toLong()), type = TransactionType.EXPENSE, amount = i * 10.0)
        }

        val state = buildInsightsDetailUiState(InsightKind.TOP, null, 0, transactions, now)

        assertEquals(10, state.transactions.size)
        assertEquals(120.0, state.transactions.first().amount, 0.0)
        assertEquals(30.0, state.transactions.last().amount, 0.0)
    }

    @Test
    fun `month kind with no value includes every transaction that month, net headline`() {
        val expense = sampleTransaction(id = 1, date = now, type = TransactionType.EXPENSE, amount = 300.0)
        val income = sampleTransaction(id = 2, date = now, type = TransactionType.INCOME, amount = 1000.0)

        val state = buildInsightsDetailUiState(InsightKind.MONTH, null, 0, listOf(expense, income), now)

        assertEquals(2, state.transactions.size)
        assertEquals(true, state.isNetHeadline)
        assertEquals(700.0, state.headlineAmount, 0.0) // income - expense
    }

    @Test
    fun `offset shifts the scoped month backward`() {
        val januaryExpense =
            sampleTransaction(id = 1, date = LocalDate.of(2026, 1, 10), type = TransactionType.EXPENSE, amount = 300.0)
        val februaryExpense = sampleTransaction(id = 2, date = now, type = TransactionType.EXPENSE, amount = 999.0)

        val state = buildInsightsDetailUiState(InsightKind.MONTH, null, 1, listOf(januaryExpense, februaryExpense), now)

        assertEquals(listOf(januaryExpense), state.transactions)
    }

    @Test
    fun `percentOfMonth is the share of that month's total expense`() {
        val food = sampleTransaction(id = 1, date = now, type = TransactionType.EXPENSE, tag = "Food", amount = 250.0)
        val rent = sampleTransaction(id = 2, date = now, type = TransactionType.EXPENSE, tag = "Rent", amount = 750.0)

        val state = buildInsightsDetailUiState(InsightKind.TAG, "Food", 0, listOf(food, rent), now)

        assertEquals(25, state.percentOfMonth)
    }

    @Test
    fun `average is total expense divided by transaction count`() {
        val a = sampleTransaction(id = 1, date = now, type = TransactionType.EXPENSE, tag = "Food", amount = 100.0)
        val b = sampleTransaction(id = 2, date = now, type = TransactionType.EXPENSE, tag = "Food", amount = 300.0)

        val state = buildInsightsDetailUiState(InsightKind.TAG, "Food", 0, listOf(a, b), now)

        assertEquals(200.0, state.average, 0.0)
    }
}
