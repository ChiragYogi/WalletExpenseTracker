package com.babacode.walletexpensetracker.ui.home

import com.babacode.walletexpensetracker.data.model.Transaction

data class BudgetProgress(
    val tag: String,
    val spent: Double,
    val limitAmount: Double
)

data class HomeUiState(
    val isLoading: Boolean = false,
    val hasAnyTransactions: Boolean = false,
    val recentTransactions: List<Transaction> = emptyList(),
    val monthIncome: Double = 0.0,
    val monthExpense: Double = 0.0,
    val topBudgets: List<BudgetProgress> = emptyList()
) {
    companion object {
        val Loading = HomeUiState(isLoading = true)
    }
}
