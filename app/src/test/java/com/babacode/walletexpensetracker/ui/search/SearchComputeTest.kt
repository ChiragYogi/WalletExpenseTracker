package com.babacode.walletexpensetracker.ui.search

import com.babacode.walletexpensetracker.data.model.PaymentMode
import com.babacode.walletexpensetracker.data.model.TransactionType
import com.babacode.walletexpensetracker.fake.sampleTransaction
import com.babacode.walletexpensetracker.utiles.Extra
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

class SearchComputeTest {

    @Test
    fun `no filters returns every transaction sorted newest first`() {
        val older = sampleTransaction(id = 1, date = LocalDate.of(2026, 1, 1))
        val newer = sampleTransaction(id = 2, date = LocalDate.of(2026, 2, 1))

        val result = filterTransactions(listOf(older, newer), SearchFilters())

        assertEquals(listOf(newer, older), result)
    }

    @Test
    fun `text query matches the note case-insensitively`() {
        val match = sampleTransaction(id = 1, note = "Grocery run")
        val noMatch = sampleTransaction(id = 2, note = "unrelated")

        val result = filterTransactions(listOf(match, noMatch), SearchFilters(query = "GRO"))

        assertEquals(listOf(match), result)
    }

    @Test
    fun `text query matches the tag even when the note doesn't`() {
        val match = sampleTransaction(id = 1, note = "x", tag = "Food")
        val noMatch = sampleTransaction(id = 2, note = "x", tag = "Rent")

        val result = filterTransactions(listOf(match, noMatch), SearchFilters(query = "food"))

        assertEquals(listOf(match), result)
    }

    @Test
    fun `text query matches the payment mode even when note and tag don't`() {
        val match = sampleTransaction(id = 1, note = "x", tag = "Other", mode = PaymentMode.UPI)
        val noMatch = sampleTransaction(id = 2, note = "x", tag = "Other", mode = PaymentMode.CASH)

        val result = filterTransactions(listOf(match, noMatch), SearchFilters(query = "upi"))

        assertEquals(listOf(match), result)
    }

    @Test
    fun `type filter keeps only the matching transaction type`() {
        val expense = sampleTransaction(id = 1, type = TransactionType.EXPENSE)
        val income = sampleTransaction(id = 2, type = TransactionType.INCOME)

        val result = filterTransactions(listOf(expense, income), SearchFilters(type = TransactionType.INCOME))

        assertEquals(listOf(income), result)
    }

    @Test
    fun `tag filter keeps only the matching tag`() {
        val food = sampleTransaction(id = 1, tag = "Food")
        val rent = sampleTransaction(id = 2, tag = "Rent")

        val result = filterTransactions(listOf(food, rent), SearchFilters(tag = "Food"))

        assertEquals(listOf(food), result)
    }

    @Test
    fun `mode filter keeps only the matching payment mode`() {
        val cash = sampleTransaction(id = 1, mode = PaymentMode.CASH)
        val upi = sampleTransaction(id = 2, mode = PaymentMode.UPI)

        val result = filterTransactions(listOf(cash, upi), SearchFilters(mode = PaymentMode.UPI))

        assertEquals(listOf(upi), result)
    }

    @Test
    fun `date range filter is inclusive of both endpoints`() {
        val before = sampleTransaction(id = 1, date = LocalDate.of(2026, 1, 31))
        val start = sampleTransaction(id = 2, date = LocalDate.of(2026, 2, 1))
        val end = sampleTransaction(id = 3, date = LocalDate.of(2026, 2, 28))
        val after = sampleTransaction(id = 4, date = LocalDate.of(2026, 3, 1))

        val result = filterTransactions(
            listOf(before, start, end, after),
            SearchFilters(
                fromDate = Extra.convertLocalDateToLong(LocalDate.of(2026, 2, 1)),
                toDate = Extra.convertLocalDateToLong(LocalDate.of(2026, 2, 28))
            )
        )

        assertEquals(listOf(end, start), result)
    }

    @Test
    fun `amount range filter is inclusive of both endpoints`() {
        val low = sampleTransaction(id = 1, amount = 50.0)
        val mid = sampleTransaction(id = 2, amount = 100.0)
        val high = sampleTransaction(id = 3, amount = 150.0)

        val result = filterTransactions(listOf(low, mid, high), SearchFilters(minAmount = "50", maxAmount = "100"))

        assertEquals(setOf(low, mid), result.toSet())
    }

    @Test
    fun `blank amount filters are ignored rather than excluding everything`() {
        val transaction = sampleTransaction(id = 1, amount = 100.0)

        val result = filterTransactions(listOf(transaction), SearchFilters(minAmount = "", maxAmount = ""))

        assertEquals(listOf(transaction), result)
    }
}
