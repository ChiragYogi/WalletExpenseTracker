package com.babacode.walletexpensetracker.data.dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.babacode.walletexpensetracker.data.database.TransactionDatabase
import com.babacode.walletexpensetracker.data.model.PaymentType
import com.babacode.walletexpensetracker.data.model.Transaction
import com.babacode.walletexpensetracker.data.model.TransactionTag
import com.babacode.walletexpensetracker.data.model.TransactionType
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TransactionDaoTest {

    private lateinit var database: TransactionDatabase
    private lateinit var dao: TransactionDao

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            TransactionDatabase::class.java
        ).allowMainThreadQueries().build()
        dao = database.getTransactionDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

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
    fun insertAndReadBack() = runBlocking {
        dao.insertNewTransaction(sampleTransaction())

        val transactions = dao.getAllTransaction().first()

        assertEquals(1, transactions.size)
        assertEquals("Coffee", transactions.first().note)
    }

    @Test
    fun deleteRemovesTransaction() = runBlocking {
        dao.insertNewTransaction(sampleTransaction())
        val inserted = dao.getAllTransaction().first().first()

        dao.deleteSelectedTransaction(inserted)

        assertTrue(dao.getAllTransaction().first().isEmpty())
    }

    @Test
    fun getTransactionByStartDateAndEndDateFiltersCorrectly() = runBlocking {
        dao.insertNewTransaction(sampleTransaction(date = 50L))
        dao.insertNewTransaction(sampleTransaction(date = 150L))

        val result = dao.getTransactionByStartDateAndEndDate(0L, 100L).first()

        assertEquals(1, result.size)
        assertEquals(50L, result.first().date)
    }
}
