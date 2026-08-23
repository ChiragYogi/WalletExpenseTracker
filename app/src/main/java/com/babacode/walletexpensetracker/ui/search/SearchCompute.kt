package com.babacode.walletexpensetracker.ui.search

import androidx.compose.runtime.Immutable
import com.babacode.walletexpensetracker.data.model.PaymentMode
import com.babacode.walletexpensetracker.data.model.Transaction
import com.babacode.walletexpensetracker.data.model.TransactionType
import com.babacode.walletexpensetracker.utiles.FinanceCompute

// null in any field below means "all" — matches the reference design's "all"-sentinel
// select options (refrence/src/routes/search.tsx).
data class SearchFilters(
    val query: String = "",
    val type: TransactionType? = null,
    val tag: String? = null,
    val mode: PaymentMode? = null,
    val fromDate: Long? = null,
    val toDate: Long? = null,
    // Kept as raw typed strings (parsed only inside filterTransactions) rather than
    // Double?, so the field can echo back exactly what the user typed instead of a
    // round-tripped "100.0"-style reformat.
    val minAmount: String = "",
    val maxAmount: String = ""
)

// Always republished wholesale from the ViewModel's StateFlow pipeline, never mutated in
// place, so it's safe to mark @Immutable despite the plain List<Transaction> field — see
// HomeUiState.
@Immutable
data class SearchUiState(
    val results: List<Transaction> = emptyList(),
    val totals: FinanceCompute.Totals = FinanceCompute.Totals(0.0, 0.0, 0.0)
)

// Port of the reference design's search filtering (refrence/src/routes/search.tsx).
fun filterTransactions(transactions: List<Transaction>, filters: SearchFilters): List<Transaction> {
    val needle = filters.query.trim().lowercase()
    val minAmount = filters.minAmount.toDoubleOrNull()
    val maxAmount = filters.maxAmount.toDoubleOrNull()

    val filtered = transactions.filter { transaction ->
        if (needle.isNotEmpty()) {
            val haystack = "${transaction.note} ${transaction.tag} ${transaction.paymentType}".lowercase()
            if (!haystack.contains(needle)) return@filter false
        }
        if (filters.type != null && transaction.transactionType != filters.type) return@filter false
        if (filters.tag != null && transaction.tag != filters.tag) return@filter false
        if (filters.mode != null && transaction.paymentType != filters.mode) return@filter false
        if (filters.fromDate != null && transaction.date < filters.fromDate) return@filter false
        if (filters.toDate != null && transaction.date > filters.toDate) return@filter false
        if (minAmount != null && transaction.amount < minAmount) return@filter false
        if (maxAmount != null && transaction.amount > maxAmount) return@filter false
        true
    }
    return FinanceCompute.sortByDateDesc(filtered)
}
