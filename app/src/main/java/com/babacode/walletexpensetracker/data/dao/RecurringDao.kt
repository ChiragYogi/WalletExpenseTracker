package com.babacode.walletexpensetracker.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.babacode.walletexpensetracker.data.model.RecurringRule
import kotlinx.coroutines.flow.Flow

@Dao
interface RecurringDao {

    @Query("SELECT * FROM recurring_table ORDER BY id DESC")
    fun getAllRecurringRules(): Flow<List<RecurringRule>>

    @Query("SELECT * FROM recurring_table WHERE active = 1 AND nextDate <= :asOfDate")
    suspend fun getDueRecurringRules(asOfDate: Long): List<RecurringRule>

    @Insert
    suspend fun insertRecurringRule(rule: RecurringRule)

    @Update
    suspend fun updateRecurringRule(rule: RecurringRule)

    @Delete
    suspend fun deleteRecurringRule(rule: RecurringRule)

    @Query("DELETE FROM recurring_table WHERE id = :id")
    suspend fun deleteRecurringRuleById(id: Int)
}
