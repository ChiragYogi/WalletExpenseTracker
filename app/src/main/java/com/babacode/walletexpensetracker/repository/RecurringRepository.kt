package com.babacode.walletexpensetracker.repository

import com.babacode.walletexpensetracker.data.dao.RecurringDao
import com.babacode.walletexpensetracker.data.model.RecurringRule
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RecurringRepository @Inject constructor(
    private val dao: RecurringDao
) {

    fun getAllRecurringRules(): Flow<List<RecurringRule>> = dao.getAllRecurringRules()

    suspend fun getDueRecurringRules(asOfDate: Long): List<RecurringRule> =
        dao.getDueRecurringRules(asOfDate)

    suspend fun insertRecurringRule(rule: RecurringRule) = dao.insertRecurringRule(rule)

    suspend fun updateRecurringRule(rule: RecurringRule) = dao.updateRecurringRule(rule)

    suspend fun deleteRecurringRule(rule: RecurringRule) = dao.deleteRecurringRule(rule)
}
