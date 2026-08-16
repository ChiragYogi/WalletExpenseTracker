package com.babacode.walletexpensetracker.ui.home

import com.babacode.walletexpensetracker.data.model.PaymentType
import com.babacode.walletexpensetracker.data.model.Transaction
import com.babacode.walletexpensetracker.data.model.TransactionTag
import com.babacode.walletexpensetracker.data.model.TransactionType
import com.babacode.walletexpensetracker.fake.FakeTransactionDao
import com.babacode.walletexpensetracker.repository.TransactionRepository
import com.babacode.walletexpensetracker.util.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private fun sampleTransaction(id: Int = 0) = Transaction(
        note = "Coffee",
        date = 0L,
        transactionType = TransactionType.EXPENSE,
        amount = 5.0,
        tag = TransactionTag.FOOD,
        paymentType = PaymentType.CASH,
        id = id
    )

    @Test
    fun `recentTransaction reflects repository state`() = runTest {
        val dao = FakeTransactionDao()
        dao.insertNewTransaction(sampleTransaction())
        val viewModel = HomeViewModel(TransactionRepository(dao))

        backgroundScope.launch { viewModel.recentTransaction.collect {} }
        advanceUntilIdle()

        assertEquals(1, viewModel.recentTransaction.value.size)
    }

    @Test
    fun `deleteSingleTransaction removes transaction and reports success`() = runTest {
        val dao = FakeTransactionDao()
        dao.insertNewTransaction(sampleTransaction(id = 1))
        val viewModel = HomeViewModel(TransactionRepository(dao))

        backgroundScope.launch { viewModel.recentTransaction.collect {} }
        advanceUntilIdle()
        assertEquals(1, viewModel.recentTransaction.value.size)

        val result = viewModel.deleteSingleTransaction(sampleTransaction(id = 1))
        advanceUntilIdle()

        assertTrue(result.isSuccess)
        assertEquals(0, viewModel.recentTransaction.value.size)
    }
}
