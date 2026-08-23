package com.babacode.walletexpensetracker.ui.insights

import androidx.compose.runtime.Immutable
import com.babacode.walletexpensetracker.data.model.Transaction
import com.babacode.walletexpensetracker.data.model.TransactionType
import com.babacode.walletexpensetracker.ui.navigation.InsightKind
import com.babacode.walletexpensetracker.utiles.Extra
import com.babacode.walletexpensetracker.utiles.FinanceCompute
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.roundToInt

// Which subtitle/title format applies — resolved to actual (localized) strings by the
// Composable layer via string resources, keeping this file free of Android string lookups.
enum class InsightsDetailSubtitleKind { CATEGORY, PAYMENT_MODE, TOP_SPENDS, ALL_TRANSACTIONS }

// Always republished wholesale from the ViewModel's StateFlow pipeline, never mutated in
// place, so it's safe to mark @Immutable despite the plain List<Transaction> field — see
// HomeUiState.
@Immutable
data class InsightsDetailUiState(
    val isLoading: Boolean = true,
    val title: String = "",
    val subtitleKind: InsightsDetailSubtitleKind = InsightsDetailSubtitleKind.ALL_TRANSACTIONS,
    val monthLabel: String = "",
    val isNetHeadline: Boolean = false,
    val headlineAmount: Double = 0.0,
    val transactionCount: Int = 0,
    val average: Double = 0.0,
    val percentOfMonth: Int = 0,
    val transactions: List<Transaction> = emptyList()
) {
    companion object {
        val Loading = InsightsDetailUiState(isLoading = true)
    }
}

private val monthYearFormatter = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.US)
private val monthAbbrevYearFormatter = DateTimeFormatter.ofPattern("MMM yyyy", Locale.US)

// Port of the reference design's Insights-Detail filtering (refrence/src/routes/insights-detail.tsx),
// driven by the {kind, value, offset} nav args carried from Insights/Home (ui/navigation/Routes.kt).
fun buildInsightsDetailUiState(
    kind: InsightKind,
    value: String?,
    offset: Int,
    transactions: List<Transaction>,
    now: LocalDate = LocalDate.now()
): InsightsDetailUiState {
    val monthDate = now.minusMonths(offset.toLong())
    val monthRange = Extra.getLocalDateStartEndDateMonth(monthDate)
    val monthTxs = FinanceCompute.inRange(transactions, monthRange.startDate, monthRange.endDate)
    val monthLabel = monthDate.format(monthYearFormatter)

    val expensesThisMonth = monthTxs.filter { it.transactionType == TransactionType.EXPENSE }

    val title: String
    val subtitleKind: InsightsDetailSubtitleKind
    val list: List<Transaction>
    val isNetHeadline: Boolean

    when {
        kind == InsightKind.TAG && !value.isNullOrEmpty() -> {
            list = expensesThisMonth.filter { it.tag == value }
            title = value
            subtitleKind = InsightsDetailSubtitleKind.CATEGORY
            isNetHeadline = false
        }
        kind == InsightKind.MODE && !value.isNullOrEmpty() -> {
            list = expensesThisMonth.filter { it.paymentType.toString() == value }
            title = value
            subtitleKind = InsightsDetailSubtitleKind.PAYMENT_MODE
            isNetHeadline = false
        }
        kind == InsightKind.TOP -> {
            list = expensesThisMonth.sortedByDescending { it.amount }.take(10)
            title = ""
            subtitleKind = InsightsDetailSubtitleKind.TOP_SPENDS
            isNetHeadline = false
        }
        else -> {
            list = monthTxs
            title = monthDate.format(monthAbbrevYearFormatter)
            subtitleKind = InsightsDetailSubtitleKind.ALL_TRANSACTIONS
            isNetHeadline = true
        }
    }

    val sum = FinanceCompute.totals(list)
    val monthExpense = FinanceCompute.totals(monthTxs).expense
    val share = if (monthExpense != 0.0) (sum.expense / monthExpense * 100).roundToInt() else 0
    val average = if (list.isNotEmpty()) sum.expense / list.size else 0.0
    val rows = if (kind == InsightKind.TOP) list else FinanceCompute.sortByDateDesc(list)

    return InsightsDetailUiState(
        isLoading = false,
        title = title,
        subtitleKind = subtitleKind,
        monthLabel = monthLabel,
        isNetHeadline = isNetHeadline,
        headlineAmount = if (isNetHeadline) sum.net else sum.expense,
        transactionCount = rows.size,
        average = average,
        percentOfMonth = share,
        transactions = rows
    )
}
