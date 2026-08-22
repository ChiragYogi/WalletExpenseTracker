package com.babacode.walletexpensetracker.ui.budgets

import com.babacode.walletexpensetracker.data.model.Budget
import com.babacode.walletexpensetracker.data.model.Transaction
import com.babacode.walletexpensetracker.data.model.TransactionType
import com.babacode.walletexpensetracker.utiles.Extra
import com.babacode.walletexpensetracker.utiles.FinanceCompute
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.roundToInt

data class BudgetRow(
    val id: Int,
    val tag: String,
    val used: Double,
    val limitAmount: Double,
    val percent: Int,
    val remaining: Double,
    val isOver: Boolean
)

data class BudgetsUiState(
    val isLoading: Boolean = true,
    val monthLabel: String = "",
    val daysLeft: Int = 1,
    val overspentTags: List<String> = emptyList(),
    val rows: List<BudgetRow> = emptyList()
) {
    companion object {
        val Loading = BudgetsUiState(isLoading = true)
    }
}

private val monthYearFormatter = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.US)

// Port of the reference design's Budgets computations (refrence/src/routes/budgets.tsx):
// per-tag spend-this-month vs. limit, days remaining in the month, and which budgets
// are currently over.
fun buildBudgetsUiState(
    transactions: List<Transaction>,
    budgets: List<Budget>,
    now: LocalDate = LocalDate.now()
): BudgetsUiState {
    val monthRange = Extra.getLocalDateStartEndDateMonth(now)
    val monthTxs = FinanceCompute.inRange(transactions, monthRange.startDate, monthRange.endDate)
    val spentByTag = FinanceCompute.byTag(monthTxs, TransactionType.EXPENSE).toMap()

    val daysLeft = (now.lengthOfMonth() - now.dayOfMonth + 1).coerceAtLeast(1)

    val rows = budgets.map { budget ->
        val used = spentByTag[budget.tag] ?: 0.0
        val percent = if (budget.limitAmount > 0) (used / budget.limitAmount * 100).roundToInt() else 0
        BudgetRow(
            id = budget.id,
            tag = budget.tag,
            used = used,
            limitAmount = budget.limitAmount,
            percent = percent,
            remaining = budget.limitAmount - used,
            isOver = used > budget.limitAmount
        )
    }

    return BudgetsUiState(
        isLoading = false,
        monthLabel = now.format(monthYearFormatter),
        daysLeft = daysLeft,
        overspentTags = rows.filter { it.isOver }.map { it.tag },
        rows = rows
    )
}
