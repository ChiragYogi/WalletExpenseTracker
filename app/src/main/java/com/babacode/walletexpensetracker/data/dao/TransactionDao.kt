package com.babacode.walletexpensetracker.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.babacode.walletexpensetracker.data.model.Transaction
import kotlinx.coroutines.flow.Flow


@Dao
interface TransactionDao {

    @Query("SELECT * FROM transaction_table ORDER by id DESC")
    fun getAllTransaction(): Flow<List<Transaction>>

    @Query("SELECT * FROM transaction_table WHERE transactionType = :transactionType")
    fun getTransactionByType(transactionType: String) : Flow<List<Transaction>>

    @Insert
    suspend fun insertNewTransaction(transaction: Transaction)

    @Insert
    suspend fun updateTransaction(transaction: Transaction)

    @Query("DELETE FROM transaction_table WHERE id = :id")
    suspend fun deleteTransactionById(id:Int)


}
