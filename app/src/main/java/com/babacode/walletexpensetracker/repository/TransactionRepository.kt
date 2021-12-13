package com.babacode.walletexpensetracker.repository


import com.babacode.walletexpensetracker.data.dao.TransactionDao
import com.babacode.walletexpensetracker.data.model.Month
import com.babacode.walletexpensetracker.data.model.Transaction
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class TransactionRepository @Inject constructor(
    private val dao: TransactionDao
) {

    fun getAllTransaction(): Flow<List<Transaction>>{
        return dao.getAllTransaction()
    }

    fun getCurrentMonthTransaction(startDate: Long, endDate: Long): Flow<List<Transaction>>{
        return dao.getThisMonthTransaction(startDate,endDate)
    }

    fun getTransactionByTransactionType(transactionType: String): Flow<List<Transaction>>{
        return dao.getTransactionByType(type = transactionType)
    }

    suspend fun insertNewTransaction(transaction: Transaction){
        dao.insertNewTransaction(transaction)
    }

    suspend fun updateTransaction(transaction: Transaction){
        dao.updateTransaction(transaction)
    }

    suspend fun deleteSingleTransaction(transactionId: Int){
        dao.deleteTransactionById(id = transactionId)
    }




}