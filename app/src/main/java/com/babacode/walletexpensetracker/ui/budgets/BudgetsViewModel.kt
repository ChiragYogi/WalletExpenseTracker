package com.babacode.walletexpensetracker.ui.budgets

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.babacode.walletexpensetracker.data.model.Budget
import com.babacode.walletexpensetracker.data.model.TagCatalog
import com.babacode.walletexpensetracker.repository.BudgetRepository
import com.babacode.walletexpensetracker.repository.TransactionRepository
import com.babacode.walletexpensetracker.utiles.recoverWithDefault
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class BudgetFormState(
    val tag: String = TagCatalog.EXPENSE_TAGS.first(),
    val limit: String = ""
)

@HiltViewModel
class BudgetsViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val budgetRepository: BudgetRepository
) : ViewModel() {

    val uiState: StateFlow<BudgetsUiState> = combine(
        transactionRepository.getAllTransaction().recoverWithDefault(emptyList()),
        budgetRepository.getAllBudgets().recoverWithDefault(emptyList())
    ) { transactions, budgets -> buildBudgetsUiState(transactions, budgets) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), BudgetsUiState.Loading)

    private val _formState = MutableStateFlow(BudgetFormState())
    val formState: StateFlow<BudgetFormState> = _formState.asStateFlow()

    fun onTagSelected(tag: String) = _formState.update { it.copy(tag = tag) }

    fun onLimitChanged(value: String) = _formState.update { it.copy(limit = value) }

    fun onSaveBudgetClicked() {
        val state = _formState.value
        val limitValue = state.limit.toDoubleOrNull()
        if (limitValue == null || limitValue <= 0) return

        viewModelScope.launch {
            budgetRepository.upsertBudget(state.tag, limitValue)
            _formState.update { it.copy(limit = "") }
        }
    }

    fun onDeleteBudget(row: BudgetRow) {
        viewModelScope.launch {
            budgetRepository.deleteBudget(Budget(tag = row.tag, limitAmount = row.limitAmount, id = row.id))
        }
    }
}
