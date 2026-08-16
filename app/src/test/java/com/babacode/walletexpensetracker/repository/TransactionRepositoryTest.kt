package com.babacode.walletexpensetracker.repository

import com.babacode.walletexpensetracker.data.model.PaymentType
import com.babacode.walletexpensetracker.data.model.Transaction
import com.babacode.walletexpensetracker.data.model.TransactionTag
import com.babacode.walletexpensetracker.data.model.TransactionType
import com.babacode.walletexpensetracker.fake.FakeTransactionDao
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class TransactionRepositoryTest {

    private fun sampleTransaction(id: Int = 0, date: Long = 100L) = Transaction(
        note = "Coffee",
        date = date,
        transactionType = TransactionType.EXPENSE,
        amount = 5.0,
        tag = TransactionTag.FOOD,
        paymentType = PaymentType.CASH,
        id = id
    )

    @Test
    fun `insertNewTransaction adds to getAllTransaction`() = runTest {
        val repository = TransactionRepository(FakeTransactionDao())

        repository.insertNewTransaction(sampleTransaction())

        assertEquals(1, repository.getAllTransaction().first().size)
    }

    @Test
    fun `deleteSingleTransaction removes it from getAllTransaction`() = runTest {
        val repository = TransactionRepository(FakeTransactionDao())
        repository.insertNewTransaction(sampleTransaction(id = 1))

        repository.deleteSingleTransaction(sampleTransaction(id = 1))

        assertTrue(repository.getAllTransaction().first().isEmpty())
    }

    @Test
    fun `getTransactionForSelectedDate filters by date range`() = runTest {
        val repository = TransactionRepository(FakeTransactionDao())
        repository.insertNewTransaction(sampleTransaction(id = 1, date = 50L))
        repository.insertNewTransaction(sampleTransaction(id = 2, date = 150L))

        val result = repository.getTransactionForSelectedDate(0L, 100L).first()

        assertEquals(1, result.size)
        assertEquals(50L, result.first().date)
    }
}
