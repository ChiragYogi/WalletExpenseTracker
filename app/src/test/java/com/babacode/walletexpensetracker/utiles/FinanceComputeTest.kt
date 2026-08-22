package com.babacode.walletexpensetracker.utiles

import com.babacode.walletexpensetracker.data.model.PaymentMode
import com.babacode.walletexpensetracker.data.model.TransactionType
import com.babacode.walletexpensetracker.fake.sampleTransaction
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

class FinanceComputeTest {

    @Test
    fun `inRange keeps only transactions within the closed range`() {
        val jan1 = sampleTransaction(id = 1, date = LocalDate.of(2026, 1, 1))
        val jan15 = sampleTransaction(id = 2, date = LocalDate.of(2026, 1, 15))
        val feb1 = sampleTransaction(id = 3, date = LocalDate.of(2026, 2, 1))
        val start = Extra.convertLocalDateToLong(LocalDate.of(2026, 1, 1))
        val end = Extra.convertLocalDateToLong(LocalDate.of(2026, 1, 31))

        val result = FinanceCompute.inRange(listOf(jan1, jan15, feb1), start, end)

        assertEquals(listOf(jan1, jan15), result)
    }

    @Test
    fun `totals sums income and expense separately and nets them`() {
        val transactions = listOf(
            sampleTransaction(id = 1, type = TransactionType.INCOME, amount = 50000.0),
            sampleTransaction(id = 2, type = TransactionType.EXPENSE, amount = 450.0),
            sampleTransaction(id = 3, type = TransactionType.EXPENSE, amount = 200.0)
        )

        val totals = FinanceCompute.totals(transactions)

        assertEquals(50000.0, totals.income, 0.0)
        assertEquals(650.0, totals.expense, 0.0)
        assertEquals(49350.0, totals.net, 0.0)
    }

    @Test
    fun `byTag groups by tag within a type and sorts descending`() {
        val transactions = listOf(
            sampleTransaction(id = 1, type = TransactionType.EXPENSE, tag = "Food", amount = 100.0),
            sampleTransaction(id = 2, type = TransactionType.EXPENSE, tag = "Food", amount = 50.0),
            sampleTransaction(id = 3, type = TransactionType.EXPENSE, tag = "Rent", amount = 300.0),
            sampleTransaction(id = 4, type = TransactionType.INCOME, tag = "Salary", amount = 99999.0)
        )

        val result = FinanceCompute.byTag(transactions, TransactionType.EXPENSE)

        assertEquals(listOf("Rent" to 300.0, "Food" to 150.0), result)
    }

    @Test
    fun `byMode groups across both transaction types by payment mode`() {
        val transactions = listOf(
            sampleTransaction(id = 1, mode = PaymentMode.CASH, amount = 100.0),
            sampleTransaction(id = 2, mode = PaymentMode.CASH, amount = 50.0),
            sampleTransaction(id = 3, mode = PaymentMode.UPI, amount = 300.0)
        )

        val result = FinanceCompute.byMode(transactions)

        assertEquals(listOf("UPI" to 300.0, "Cash" to 150.0), result)
    }

    @Test
    fun `sortByDateDesc orders newest first and breaks ties by id descending`() {
        val older = sampleTransaction(id = 1, date = LocalDate.of(2026, 1, 1))
        val newer = sampleTransaction(id = 2, date = LocalDate.of(2026, 2, 1))
        val sameDayLowerId = sampleTransaction(id = 3, date = LocalDate.of(2026, 2, 1))

        val result = FinanceCompute.sortByDateDesc(listOf(older, newer, sameDayLowerId))

        assertEquals(listOf(sameDayLowerId, newer, older), result)
    }
}
