package com.babacode.walletexpensetracker.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.babacode.walletexpensetracker.data.model.Budget
import kotlinx.coroutines.flow.Flow

@Dao
interface BudgetDao {

    @Query("SELECT * FROM budget_table ORDER BY id DESC")
    fun getAllBudgets(): Flow<List<Budget>>

    @Query("SELECT * FROM budget_table WHERE tag = :tag LIMIT 1")
    suspend fun getBudgetForTag(tag: String): Budget?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertBudget(budget: Budget)

    @Update
    suspend fun updateBudget(budget: Budget)

    @Delete
    suspend fun deleteBudget(budget: Budget)

    @Query("DELETE FROM budget_table WHERE id = :id")
    suspend fun deleteBudgetById(id: Int)
}
