package com.babacode.walletexpensetracker.fake

import com.babacode.walletexpensetracker.data.dao.BudgetDao
import com.babacode.walletexpensetracker.data.model.Budget
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeBudgetDao : BudgetDao {

    private val budgets = MutableStateFlow<List<Budget>>(emptyList())
    private var nextId = 1

    override fun getAllBudgets(): Flow<List<Budget>> = budgets

    override suspend fun getBudgetForTag(tag: String): Budget? = budgets.value.firstOrNull { it.tag == tag }

    override suspend fun upsertBudget(budget: Budget) {
        val withId = if (budget.id == 0) budget.copy(id = nextId++) else budget
        budgets.value = budgets.value.filterNot { it.id == withId.id } + withId
    }

    override suspend fun updateBudget(budget: Budget) {
        budgets.value = budgets.value.map { if (it.id == budget.id) budget else it }
    }

    override suspend fun deleteBudget(budget: Budget) {
        budgets.value = budgets.value.filterNot { it.id == budget.id }
    }

    override suspend fun deleteBudgetById(id: Int) {
        budgets.value = budgets.value.filterNot { it.id == id }
    }
}
