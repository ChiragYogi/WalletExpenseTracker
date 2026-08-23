package com.babacode.walletexpensetracker.ui.home

import androidx.compose.runtime.Immutable
import com.babacode.walletexpensetracker.data.model.Transaction

data class BudgetProgress(
    val tag: String,
    val spent: Double,
    val limitAmount: Double
)

// The ViewModel always publishes a fresh instance (never mutates recentTransactions/topBudgets
// in place), so this is safe to mark @Immutable even though it holds plain List<T> fields —
// without it, Compose can't prove that and treats the whole state (and anything reading it) as
// unstable, disabling recomposition-skipping wherever it's passed as a parameter.
@Immutable
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
