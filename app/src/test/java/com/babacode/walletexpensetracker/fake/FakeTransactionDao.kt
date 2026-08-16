package com.babacode.walletexpensetracker.fake

import com.babacode.walletexpensetracker.data.dao.TransactionDao
import com.babacode.walletexpensetracker.data.model.Transaction
import com.babacode.walletexpensetracker.data.model.TransactionType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class FakeTransactionDao : TransactionDao {

    private val transactions = MutableStateFlow<List<Transaction>>(emptyList())
    private var nextId = 1

    override fun getAllTransaction(): Flow<List<Transaction>> = transactions

    override fun getTransactionByStartDateAndEndDate(startDate: Long, endDate: Long): Flow<List<Transaction>> =
        transactions.map { list -> list.filter { it.date in startDate..endDate } }

    override fun getTransactionByType(
        transactionType: TransactionType,
        startDate: Long,
        endDate: Long
    ): Flow<List<Transaction>> =
        transactions.map { list ->
            list.filter { it.transactionType == transactionType && it.date in startDate..endDate }
        }

    override suspend fun insertNewTransaction(transaction: Transaction) {
        val withId = if (transaction.id == 0) transaction.copy(id = nextId++) else transaction
        transactions.value = transactions.value + withId
    }

    override suspend fun updateTransaction(transaction: Transaction) {
        transactions.value = transactions.value.map { if (it.id == transaction.id) transaction else it }
    }

    override suspend fun deleteSelectedTransaction(transaction: Transaction) {
        transactions.value = transactions.value.filterNot { it.id == transaction.id }
    }

    override suspend fun deleteTransactionById(id: Int) {
        transactions.value = transactions.value.filterNot { it.id == id }
    }

    override fun getSingleDayTransaction(date: Long): Flow<List<Transaction>> =
        transactions.map { list -> list.filter { it.date == date } }

    override fun getSingleDayTransactionByType(transactionType: TransactionType, date: Long): Flow<List<Transaction>> =
        transactions.map { list -> list.filter { it.transactionType == transactionType && it.date == date } }

    override suspend fun deleteAllTheTransaction() {
        transactions.value = emptyList()
    }
}
