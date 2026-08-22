package com.babacode.walletexpensetracker.ui.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.AndroidUiModes.UI_MODE_NIGHT_YES
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.babacode.walletexpensetracker.R
import com.babacode.walletexpensetracker.data.model.PaymentMode
import com.babacode.walletexpensetracker.data.model.Transaction
import com.babacode.walletexpensetracker.data.model.TransactionType
import com.babacode.walletexpensetracker.ui.theme.ShapeLarge
import com.babacode.walletexpensetracker.ui.theme.WalletExpenseTheme
import com.babacode.walletexpensetracker.ui.theme.WalletTheme
import com.babacode.walletexpensetracker.utiles.Extra

private fun tagIcon(tag: String): Int = when (tag) {
    "Rent" -> R.drawable.rent_vector
    "Food" -> R.drawable.food_vector
    "Utils" -> R.drawable.utiles_vector
    "Travel" -> R.drawable.traveling_vector
    "Shopping" -> R.drawable.shopping_vector
    "Health" -> R.drawable.medical_vector
    "Entertainment" -> R.drawable.entertainment_vector
    "Salary" -> R.drawable.money_vector
    "Freelance" -> R.drawable.income_vector
    "Interest" -> R.drawable.investment_vector
    "Gift" -> R.drawable.gift_vector
    else -> R.drawable.other_vector
}

@Composable
fun TransactionRow(
    transaction: Transaction,
    currencyCode: String,
    onClick: (Transaction) -> Unit,
    onLongPress: (Transaction) -> Unit,
    modifier: Modifier = Modifier
) {
    val extendedColors = WalletTheme.extendedColors
    val (tint, softBackground) = when (transaction.transactionType) {
        TransactionType.EXPENSE -> extendedColors.expense to extendedColors.expenseSoft
        TransactionType.INCOME -> extendedColors.income to extendedColors.incomeSoft
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(4.dp)
            .combinedClickable(
                onClick = { onClick(transaction) },
                onLongClick = { onLongPress(transaction) }
            ),
        shape = ShapeLarge,
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(softBackground, CircleShape)
                    .padding(12.dp)
            ) {
                Icon(
                    painter = painterResource(tagIcon(transaction.tag)),
                    contentDescription = stringResource(R.string.transaction_type_tag),
                    tint = tint
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = transaction.note,
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.weight(1f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = Extra.convertLongDateToStringDate(transaction.date),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                ) {
                    Text(
                        text = "$currencyCode ${transaction.amount}",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = transaction.paymentType.toString(),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TransactionRowPreview() {
    WalletExpenseTheme {
        TransactionRow(
            transaction = Transaction(
                note = "Groceries",
                date = Extra.currentDayDate(),
                transactionType = TransactionType.EXPENSE,
                amount = 450.0,
                tag = "Food",
                paymentType = PaymentMode.CASH
            ),
            currencyCode = "$",
            onClick = {},
            onLongPress = {}
        )
    }
}

@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun TransactionRowPreviewNight() {
    WalletExpenseTheme {
        TransactionRow(
            transaction = Transaction(
                note = "Groceries",
                date = Extra.currentDayDate(),
                transactionType = TransactionType.EXPENSE,
                amount = 450.0,
                tag = "Food",
                paymentType = PaymentMode.CASH
            ),
            currencyCode = "$",
            onClick = {},
            onLongPress = {}
        )
    }
}
