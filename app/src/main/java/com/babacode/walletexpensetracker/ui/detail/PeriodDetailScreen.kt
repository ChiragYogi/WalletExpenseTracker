package com.babacode.walletexpensetracker.ui.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.babacode.walletexpensetracker.R
import com.babacode.walletexpensetracker.data.model.PaymentMode
import com.babacode.walletexpensetracker.data.model.Transaction
import com.babacode.walletexpensetracker.data.model.TransactionType
import com.babacode.walletexpensetracker.ui.compose.CursorHeader
import com.babacode.walletexpensetracker.ui.compose.EmptyState
import com.babacode.walletexpensetracker.ui.compose.transactionDateGroups
import com.babacode.walletexpensetracker.ui.compose.charts.BarChart
import com.babacode.walletexpensetracker.ui.compose.charts.BarChartEntry
import com.babacode.walletexpensetracker.ui.theme.ShapeThreeExtraLarge
import com.babacode.walletexpensetracker.ui.theme.WalletExpenseTheme
import com.babacode.walletexpensetracker.ui.theme.WalletTheme
import com.babacode.walletexpensetracker.utiles.Extra
import com.babacode.walletexpensetracker.utiles.FinanceCompute
import com.babacode.walletexpensetracker.utiles.formatMoney

@Composable
fun PeriodDetailScreen(
    period: DetailPeriod,
    dateLabel: String,
    currencyCode: String,
    transactions: List<Transaction>,
    trendBuckets: List<DetailBucket>,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onTransactionClick: (Transaction) -> Unit,
    onLongPress: (Transaction) -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = WalletTheme.spacing

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(spacing.default),
        verticalArrangement = Arrangement.spacedBy(spacing.medium)
    ) {
        item {
            CursorHeader(label = dateLabel, onPrevious = onPrevious, onNext = onNext)
        }

        item {
            PeriodSummaryCard(
                totalLabel = stringResource(period.totalLabelRes),
                currencyCode = currencyCode,
                totals = FinanceCompute.totals(transactions),
                trendBuckets = trendBuckets
            )
        }

        if (transactions.isEmpty()) {
            item {
                EmptyState(message = stringResource(R.string.nothing_recorded_this_period))
            }
        } else {
            transactionDateGroups(
                transactions = transactions,
                currencyCode = currencyCode,
                onClick = onTransactionClick,
                onLongPress = onLongPress
            )
        }
    }
}

@Composable
private fun PeriodSummaryCard(
    totalLabel: String,
    currencyCode: String,
    totals: FinanceCompute.Totals,
    trendBuckets: List<DetailBucket>
) {
    val extendedColors = WalletTheme.extendedColors

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = ShapeThreeExtraLarge,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(WalletTheme.spacing.large),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = totalLabel,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = formatMoney(totals.net, currencyCode),
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(top = WalletTheme.spacing.extraSmall)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = WalletTheme.spacing.medium),
                horizontalArrangement = Arrangement.spacedBy(WalletTheme.spacing.medium)
            ) {
                MiniStatChip(
                    label = stringResource(R.string.income),
                    value = formatMoney(totals.income, currencyCode),
                    background = extendedColors.incomeSoft,
                    contentColor = extendedColors.income,
                    modifier = Modifier.weight(1f)
                )
                MiniStatChip(
                    label = stringResource(R.string.expense),
                    value = formatMoney(totals.expense, currencyCode),
                    background = extendedColors.expenseSoft,
                    contentColor = extendedColors.expense,
                    modifier = Modifier.weight(1f)
                )
            }

            BarChart(
                entries = trendBuckets.map { BarChartEntry(it.label, it.amount.toFloat()) },
                barColor = { _, entry ->
                    if (entry.value > 0f) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = WalletTheme.spacing.medium)
            )
        }
    }
}

@Composable
private fun MiniStatChip(
    label: String,
    value: String,
    background: Color,
    contentColor: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(color = background, shape = RoundedCornerShape(14.dp))
            .padding(horizontal = WalletTheme.spacing.medium, vertical = WalletTheme.spacing.small)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.labelLarge,
            color = contentColor
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PeriodDetailScreenPreview() {
    WalletExpenseTheme {
        PeriodDetailScreen(
            period = DetailPeriod.WEEKLY,
            dateLabel = "03 Jan to 09 Jan",
            currencyCode = "$",
            transactions = listOf(
                Transaction(
                    note = "Groceries",
                    date = Extra.currentDayDate(),
                    transactionType = TransactionType.EXPENSE,
                    amount = 450.0,
                    tag = "Food",
                    paymentType = PaymentMode.CASH,
                    id = 1
                ),
                Transaction(
                    note = "Salary",
                    date = Extra.currentDayDate(),
                    transactionType = TransactionType.INCOME,
                    amount = 50000.0,
                    tag = "Salary",
                    paymentType = PaymentMode.ONLINE_BANKING,
                    id = 2
                )
            ),
            trendBuckets = listOf(
                DetailBucket("M", 100.0),
                DetailBucket("T", 0.0),
                DetailBucket("W", 250.0),
                DetailBucket("T", 40.0),
                DetailBucket("F", 450.0),
                DetailBucket("S", 0.0),
                DetailBucket("S", 0.0)
            ),
            onPrevious = {},
            onNext = {},
            onTransactionClick = {},
            onLongPress = {}
        )
    }
}
