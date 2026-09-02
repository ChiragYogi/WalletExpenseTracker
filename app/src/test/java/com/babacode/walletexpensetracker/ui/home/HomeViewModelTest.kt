package com.babacode.walletexpensetracker.ui.home

import com.babacode.walletexpensetracker.data.model.PaymentMode
import com.babacode.walletexpensetracker.data.model.Transaction
import com.babacode.walletexpensetracker.data.model.TransactionType
import com.babacode.walletexpensetracker.fake.FakeBudgetDao
import com.babacode.walletexpensetracker.fake.FakeTransactionDao
import com.babacode.walletexpensetracker.repository.BudgetRepository
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
        tag = "Food",
        paymentType = PaymentMode.CASH,
        id = id
    )

    private fun viewModel(dao: FakeTransactionDao) =
        HomeViewModel(TransactionRepository(dao), BudgetRepository(FakeBudgetDao()))

    @Test
    fun `uiState reflects repository state`() = runTest {
        val dao = FakeTransactionDao()
        dao.insertNewTransaction(sampleTransaction())
        val viewModel = viewModel(dao)

        backgroundScope.launch { viewModel.uiState.collect {} }
        advanceUntilIdle()

        assertEquals(1, viewModel.uiState.value.recentTransactions.size)
    }

    @Test
    fun `deleteSingleTransaction removes transaction and reports success`() = runTest {
        val dao = FakeTransactionDao()
        dao.insertNewTransaction(sampleTransaction(id = 1))
        val viewModel = viewModel(dao)

        backgroundScope.launch { viewModel.uiState.collect {} }
        advanceUntilIdle()
        assertEquals(1, viewModel.uiState.value.recentTransactions.size)

        val result = viewModel.deleteSingleTransaction(sampleTransaction(id = 1))
        advanceUntilIdle()

        assertTrue(result.isSuccess)
        assertEquals(0, viewModel.uiState.value.recentTransactions.size)
    }
}
