package com.babacode.walletexpensetracker.repository


import com.babacode.walletexpensetracker.data.dao.TransactionDao
import com.babacode.walletexpensetracker.data.model.Transaction
import com.babacode.walletexpensetracker.data.model.TransactionType
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class TransactionRepository @Inject constructor(
    private val dao: TransactionDao
) {

    fun getAllTransaction(): Flow<List<Transaction>> {
        return dao.getAllTransaction()
    }

    fun getTransactionForSelectedDate(startDate: Long, endDate: Long): Flow<List<Transaction>> {
        return dao.getTransactionByStartDateAndEndDate(startDate, endDate)
    }

    fun getSingleDayTransaction(date: Long): Flow<List<Transaction>> {
        return dao.getSingleDayTransaction(date)
    }

    fun getSingleDayTransactionByType(
        transactionType: TransactionType,
        currentDate: Long
    ): Flow<List<Transaction>> {
        return dao.getSingleDayTransactionByType(transactionType,currentDate)
    }

    fun getTransactionByTransactionType(
        transactionType: TransactionType,
        startDate: Long,
        endDate: Long
    ): Flow<List<Transaction>> {
        return dao.getTransactionByType(transactionType = transactionType, startDate, endDate)
    }

    suspend fun insertNewTransaction(transaction: Transaction) {
        dao.insertNewTransaction(transaction)
    }

    suspend fun updateTransaction(transaction: Transaction) {
        dao.updateTransaction(transaction)
    }

    suspend fun deleteSingleTransaction(transaction: Transaction) {
        dao.deleteSelectedTransaction(transaction)
    }

    suspend fun deleteAllTheTransaction() {
        dao.deleteAllTheTransaction()
    }


}