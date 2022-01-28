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
    fun getTransactionByStartDateAndEndDate(startDate: Long, endDate: Long): Flow<List<Transaction>>

    @Query("SELECT * FROM transaction_table WHERE(transactionType = :transactionType) AND (date >= :startDate and date <= :endDate) ORDER BY id DESC")
    fun getTransactionByType(
        transactionType: TransactionType,
        startDate: Long,
        endDate: Long
    ): Flow<List<Transaction>>


    @Insert
    suspend fun insertNewTransaction(transaction: Transaction)

    @Update
    suspend fun updateTransaction(transaction: Transaction)

    @Delete
    suspend fun deleteSelectedTransaction(transaction: Transaction)

    @Query("DELETE FROM transaction_table WHERE id = :id")
    suspend fun deleteTransactionById(id: Int)

    @Query("SELECT * FROM transaction_table WHERE date = :date ORDER BY id DESC")
    fun getSingleDayTransaction(date: Long): Flow<List<Transaction>>

    @Query("SELECT * FROM transaction_table WHERE(transactionType = :transactionType) AND (date = :date) ORDER BY id DESC")
    fun getSingleDayTransactionByType(
        transactionType: TransactionType,
        date: Long
    ): Flow<List<Transaction>>


    @Query("DELETE FROM transaction_table")
    suspend fun deleteAllTheTransaction()


}
