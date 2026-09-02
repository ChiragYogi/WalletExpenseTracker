package com.babacode.walletexpensetracker.ui.recurring

import androidx.compose.runtime.Immutable
import com.babacode.walletexpensetracker.data.model.Frequency
import com.babacode.walletexpensetracker.data.model.RecurringRule
import com.babacode.walletexpensetracker.data.model.TagCatalog

// Always republished wholesale from the ViewModel's StateFlow pipeline, never mutated in
// place, so it's safe to mark @Immutable despite the plain List<RecurringRule> field — see
// HomeUiState (ui/home/HomeUiState.kt) for the full rationale.
@Immutable
data class RecurringUiState(
    val isLoading: Boolean = true,
    val rules: List<RecurringRule> = emptyList()
) {
    companion object {
        val Loading = RecurringUiState(isLoading = true)
    }
}

data class RecurringFormState(
    val type: String = "Expense",
    val amount: String = "",
    val note: String = "",
    val tag: String = TagCatalog.EXPENSE_TAGS.first(),
    val mode: String = "UPI",
    val frequency: Frequency = Frequency.MONTHLY,
    val nextDate: String = ""
)
