package com.babacode.walletexpensetracker.data.dao

import androidx.room.*
import com.babacode.walletexpensetracker.data.model.Transaction
import com.babacode.walletexpensetracker.data.model.TransactionType
import kotlinx.coroutines.flow.Flow


@Dao
interface TransactionDao {

    @Query("SELECT * FROM transaction_table ORDER BY id DESC")
    fun getAllTransaction(): Flow<List<Transaction>>

    @Query("SELECT * FROM transaction_table WHERE date >= :startDate and date <= :endDate")
    fun getThisMonthTransaction(startDate: Long, endDate: Long): Flow<List<Transaction>>

    @Query("SELECT * FROM transaction_table WHERE transactionType = :transactionType ORDER BY id DESC")
    fun getTransactionByType(transactionType: TransactionType) : Flow<List<Transaction>>

    @Insert
    suspend fun insertNewTransaction(transaction: Transaction)

    @Update
    suspend fun updateTransaction(transaction: Transaction)

    @Delete
    suspend fun deleteSelectedTransaction(transaction: Transaction)

    @Query("DELETE FROM transaction_table WHERE id = :id")
    suspend fun deleteTransactionById(id:Int)





}
