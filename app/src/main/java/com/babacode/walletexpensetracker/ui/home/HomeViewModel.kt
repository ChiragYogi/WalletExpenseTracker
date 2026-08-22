package com.babacode.walletexpensetracker.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.babacode.walletexpensetracker.data.model.Transaction
import com.babacode.walletexpensetracker.data.model.TransactionType
import com.babacode.walletexpensetracker.repository.BudgetRepository
import com.babacode.walletexpensetracker.repository.TransactionRepository
import com.babacode.walletexpensetracker.utiles.Extra
import com.babacode.walletexpensetracker.utiles.FinanceCompute
import com.babacode.walletexpensetracker.utiles.recoverWithDefault
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

private const val MAX_RECENT_TRANSACTIONS = 6
private const val MAX_TOP_BUDGETS = 3

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: TransactionRepository,
    private val budgetRepository: BudgetRepository
) : ViewModel() {

    val uiState: StateFlow<HomeUiState> = combine(
        repository.getAllTransaction().recoverWithDefault(emptyList()),
        budgetRepository.getAllBudgets().recoverWithDefault(emptyList())
    ) { transactions, budgets ->
        val monthRange = Extra.getLocalDateStartEndDateMonth(LocalDate.now())
        val monthTransactions = FinanceCompute.inRange(transactions, monthRange.startDate, monthRange.endDate)
        val totals = FinanceCompute.totals(monthTransactions)
        val spentByExpenseTag = FinanceCompute.byTag(monthTransactions, TransactionType.EXPENSE).toMap()

        HomeUiState(
            isLoading = false,
            hasAnyTransactions = transactions.isNotEmpty(),
            recentTransactions = FinanceCompute.sortByDateDesc(transactions).take(MAX_RECENT_TRANSACTIONS),
            monthIncome = totals.income,
            monthExpense = totals.expense,
            topBudgets = budgets.take(MAX_TOP_BUDGETS).map { budget ->
                BudgetProgress(
                    tag = budget.tag,
                    spent = spentByExpenseTag[budget.tag] ?: 0.0,
                    limitAmount = budget.limitAmount
                )
            }
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), HomeUiState.Loading)

    suspend fun deleteSingleTransaction(transaction: Transaction): Result<Unit> =
        runCatching { repository.deleteSingleTransaction(transaction) }
            .onFailure { exception -> FirebaseCrashlytics.getInstance().recordException(exception) }

}
