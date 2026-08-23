package com.babacode.walletexpensetracker.ui.insights

import androidx.compose.runtime.Immutable
import com.babacode.walletexpensetracker.data.model.Transaction
import com.babacode.walletexpensetracker.data.model.TransactionType
import com.babacode.walletexpensetracker.utiles.Extra
import com.babacode.walletexpensetracker.utiles.FinanceCompute
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.roundToInt

data class CategoryBreakdown(val tag: String, val amount: Double)
data class ModeBreakdown(val mode: String, val amount: Double)
data class TrendPoint(val label: String, val amount: Double)

// Always republished wholesale from the ViewModel's StateFlow pipeline, never mutated in
// place, so it's safe to mark @Immutable despite the plain List<T> fields — see HomeUiState.
@Immutable
data class InsightsUiState(
    val isLoading: Boolean = true,
    val currentMonthLabel: String = "",
    val previousMonthLabel: String = "",
    val currentExpense: Double = 0.0,
    val previousExpense: Double = 0.0,
    val deltaPercent: Int = 0,
    val categories: List<CategoryBreakdown> = emptyList(),
    val categoryMax: Double = 1.0,
    val currentExpenseForPercent: Double = 1.0,
    val modes: List<ModeBreakdown> = emptyList(),
    val modeTotal: Double = 1.0,
    val topSpends: List<Transaction> = emptyList(),
    val trend: List<TrendPoint> = emptyList(),
    val projectedSpend: Double = 0.0
) {
    companion object {
        val Loading = InsightsUiState(isLoading = true)
    }
}

private val monthAbbrevFormatter = DateTimeFormatter.ofPattern("MMM", Locale.US)
private val fullMonthFormatter = DateTimeFormatter.ofPattern("MMMM", Locale.US)

// Port of the reference design's Insights computations (refrence/src/routes/insights.tsx):
// month-over-month expense delta, category/payment-mode breakdowns for the current month,
// top-5 expenses, a 6-month expense trend, and a simple spend-pace projection.
fun buildInsightsUiState(transactions: List<Transaction>, now: LocalDate = LocalDate.now()): InsightsUiState {
    val currentRange = Extra.getLocalDateStartEndDateMonth(now)
    val previousMonth = now.minusMonths(1)
    val previousRange = Extra.getLocalDateStartEndDateMonth(previousMonth)

    val thisMonthTxs = FinanceCompute.inRange(transactions, currentRange.startDate, currentRange.endDate)
    val lastMonthTxs = FinanceCompute.inRange(transactions, previousRange.startDate, previousRange.endDate)

    val cur = FinanceCompute.totals(thisMonthTxs)
    val prev = FinanceCompute.totals(lastMonthTxs)
    val delta = if (prev.expense != 0.0) {
        ((cur.expense - prev.expense) / prev.expense * 100).roundToInt()
    } else {
        0
    }

    val categories = FinanceCompute.byTag(thisMonthTxs, TransactionType.EXPENSE)
        .map { (tag, amount) -> CategoryBreakdown(tag, amount) }
    val categoryMax = (categories.maxOfOrNull { it.amount } ?: 1.0).coerceAtLeast(1.0)

    val modes = FinanceCompute.byMode(thisMonthTxs).map { (mode, amount) -> ModeBreakdown(mode, amount) }
    val modeTotal = modes.sumOf { it.amount }.coerceAtLeast(1.0)

    val topSpends = thisMonthTxs
        .filter { it.transactionType == TransactionType.EXPENSE }
        .sortedByDescending { it.amount }
        .take(5)

    val trend = (5 downTo 0).map { monthsAgo ->
        val monthDate = now.minusMonths(monthsAgo.toLong())
        val range = Extra.getLocalDateStartEndDateMonth(monthDate)
        val monthTxs = FinanceCompute.inRange(transactions, range.startDate, range.endDate)
        TrendPoint(label = monthDate.format(monthAbbrevFormatter), amount = FinanceCompute.totals(monthTxs).expense)
    }

    val daysElapsed = now.dayOfMonth
    val daysInMonth = now.lengthOfMonth()
    val projected = if (daysElapsed > 0) (cur.expense / daysElapsed * daysInMonth) else 0.0

    return InsightsUiState(
        isLoading = false,
        currentMonthLabel = now.format(fullMonthFormatter),
        previousMonthLabel = previousMonth.format(fullMonthFormatter),
        currentExpense = cur.expense,
        previousExpense = prev.expense,
        deltaPercent = delta,
        categories = categories,
        categoryMax = categoryMax,
        currentExpenseForPercent = cur.expense.coerceAtLeast(1.0),
        modes = modes,
        modeTotal = modeTotal,
        topSpends = topSpends,
        trend = trend,
        projectedSpend = projected
    )
}
