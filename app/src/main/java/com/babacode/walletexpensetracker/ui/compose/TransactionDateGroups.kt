package com.babacode.walletexpensetracker.ui.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.babacode.walletexpensetracker.data.model.Transaction
import com.babacode.walletexpensetracker.ui.theme.ShapeLarge
import com.babacode.walletexpensetracker.ui.theme.WalletTheme
import com.babacode.walletexpensetracker.utiles.Extra
import com.babacode.walletexpensetracker.utiles.FinanceCompute
import com.babacode.walletexpensetracker.utiles.formatMoney
import java.time.LocalDate

// Always built fresh by rememberTransactionDateGroups, never mutated in place, so it's safe
// to mark @Immutable despite the plain List<Transaction> field — see HomeUiState for the
// full rationale.
@Immutable
data class TransactionDateGroup(
    val date: LocalDate,
    val transactions: List<Transaction>,
    val dayNet: Double
)

// Sorting/grouping/summing a transaction list is real work that shouldn't re-run on every
// recomposition — call this once in the enclosing screen composable (before the LazyColumn)
// and pass the memoized result into transactionDateGroups() below.
@Composable
fun rememberTransactionDateGroups(transactions: List<Transaction>): List<TransactionDateGroup> =
    remember(transactions) {
        FinanceCompute.sortByDateDesc(transactions)
            .groupBy { Extra.convertLocalLongDateToStringAndGetLocalDate(it.date) }
            .map { (date, dayTransactions) ->
                TransactionDateGroup(date, dayTransactions, FinanceCompute.totals(dayTransactions).net)
            }
    }

// Renders pre-grouped transactions into per-day sections (date header + one card per day,
// rows separated by dividers) instead of one standalone card per transaction — matches the
// "Recent activity" grouped list from the reference design.
fun LazyListScope.transactionDateGroups(
    groups: List<TransactionDateGroup>,
    currencyCode: String,
    onClick: (Transaction) -> Unit,
    onLongPress: (Transaction) -> Unit
) {
    groups.forEach { group ->
        item(key = "date_header_${group.date}") {
            DateGroupHeader(
                date = group.date,
                currencyCode = currencyCode,
                dayNet = group.dayNet
            )
        }
        item(key = "date_group_${group.date}") {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = ShapeLarge,
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column {
                    group.transactions.forEachIndexed { index, transaction ->
                        TransactionRow(
                            transaction = transaction,
                            currencyCode = currencyCode,
                            onClick = onClick,
                            onLongPress = onLongPress,
                            showCard = false
                        )
                        if (index != group.transactions.lastIndex) {
                            HorizontalDivider(
                                modifier = Modifier.padding(start = 72.dp),
                                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DateGroupHeader(date: LocalDate, currencyCode: String, dayNet: Double) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = WalletTheme.spacing.extraSmall),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = Extra.convertLocalDateToStringDayHeader(date),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
