package com.babacode.walletexpensetracker.ui.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.babacode.walletexpensetracker.R
import com.babacode.walletexpensetracker.data.model.PaymentType
import com.babacode.walletexpensetracker.data.model.Transaction
import com.babacode.walletexpensetracker.data.model.TransactionTag
import com.babacode.walletexpensetracker.data.model.TransactionType
import com.babacode.walletexpensetracker.ui.compose.TransactionRow
import com.babacode.walletexpensetracker.ui.theme.WalletExpenseTheme
import com.babacode.walletexpensetracker.utiles.Extra

@Composable
fun PeriodDetailScreen(
    dateLabel: String,
    transactionTypeLabel: String,
    currencyCode: String,
    transactions: List<Transaction>,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onTransactionClick: (Transaction) -> Unit,
    onLongPress: (Transaction) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onPrevious) {
                Icon(
                    painter = painterResource(R.drawable.previous_vector),
                    contentDescription = stringResource(R.string.previousButton),
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
            Text(
                text = dateLabel,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground
            )
            IconButton(onClick = onNext) {
                Icon(
                    painter = painterResource(R.drawable.next_vector),
                    contentDescription = stringResource(R.string.nextButton),
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
        }

        if (transactions.isEmpty()) {
            Box(modifier = Modifier.fillMaxWidth().padding(top = 12.dp), contentAlignment = Alignment.TopCenter) {
                Text(
                    text = stringResource(R.string.noTransaction),
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        } else {
            TotalSummaryCard(
                transactionTypeLabel = transactionTypeLabel,
                currencyCode = currencyCode,
                total = transactions.sumOf { it.amount },
                modifier = Modifier.padding(12.dp)
            )
            LazyColumn(modifier = Modifier.padding(horizontal = 8.dp)) {
                items(transactions, key = { it.id }) { transaction ->
                    TransactionRow(
                        transaction = transaction,
                        currencyCode = currencyCode,
                        onClick = onTransactionClick,
                        onLongPress = onLongPress
                    )
                }
            }
        }
    }
}

@Composable
private fun TotalSummaryCard(
    transactionTypeLabel: String,
    currencyCode: String,
    total: Double,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Row {
                Text(
                    text = stringResource(R.string.total),
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = " $transactionTypeLabel",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
            Row(
                modifier = Modifier.padding(top = 4.dp),
                horizontalArrangement = Arrangement.Start
            ) {
                Text(text = currencyCode, fontWeight = FontWeight.Bold)
                Text(
                    text = " $total",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PeriodDetailScreenPreview() {
    WalletExpenseTheme {
        PeriodDetailScreen(
            dateLabel = "03 Jan to 09 Jan",
            transactionTypeLabel = "This Week",
            currencyCode = "$",
            transactions = listOf(
                Transaction(
                    note = "Groceries",
                    date = Extra.currentDayDate(),
                    transactionType = TransactionType.EXPENSE,
                    amount = 450.0,
                    tag = TransactionTag.FOOD,
                    paymentType = PaymentType.CASH,
                    id = 1
                ),
                Transaction(
                    note = "Salary",
                    date = Extra.currentDayDate(),
                    transactionType = TransactionType.INCOME,
                    amount = 50000.0,
                    tag = TransactionTag.SALARY,
                    paymentType = PaymentType.ONLINE,
                    id = 2
                )
            ),
            onPrevious = {},
            onNext = {},
            onTransactionClick = {},
            onLongPress = {}
        )
    }
}
