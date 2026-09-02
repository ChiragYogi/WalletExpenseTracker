package com.babacode.walletexpensetracker.repository

import com.babacode.walletexpensetracker.data.dao.BudgetDao
import com.babacode.walletexpensetracker.data.model.Budget
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class BudgetRepository @Inject constructor(
    private val dao: BudgetDao
) {

    fun getAllBudgets(): Flow<List<Budget>> = dao.getAllBudgets()

    suspend fun getBudgetForTag(tag: String): Budget? = dao.getBudgetForTag(tag)

    suspend fun upsertBudget(tag: String, limitAmount: Double) {
        val existing = dao.getBudgetForTag(tag)
        dao.upsertBudget(Budget(tag = tag, limitAmount = limitAmount, id = existing?.id ?: 0))
    }

    suspend fun deleteBudget(budget: Budget) = dao.deleteBudget(budget)
}
