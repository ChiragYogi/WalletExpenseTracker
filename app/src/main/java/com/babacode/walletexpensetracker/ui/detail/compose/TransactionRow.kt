package com.babacode.walletexpensetracker.ui.detail.compose

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.babacode.walletexpensetracker.R
import com.babacode.walletexpensetracker.data.model.PaymentType
import com.babacode.walletexpensetracker.data.model.Transaction
import com.babacode.walletexpensetracker.data.model.TransactionTag
import com.babacode.walletexpensetracker.data.model.TransactionType
import com.babacode.walletexpensetracker.ui.theme.WalletExpenseTheme
import com.babacode.walletexpensetracker.utiles.Extra

private fun tagIcon(tag: TransactionTag): Int = when (tag) {
    TransactionTag.OTHER -> R.drawable.other_vector
    TransactionTag.FOOD -> R.drawable.food_vector
    TransactionTag.SHOPPING -> R.drawable.shopping_vector
    TransactionTag.TRAVELLING -> R.drawable.traveling_vector
    TransactionTag.ENTERTAINMENT -> R.drawable.entertainment_vector
    TransactionTag.HEALTH -> R.drawable.medical_vector
    TransactionTag.EDUCATION -> R.drawable.education_vector
    TransactionTag.RENT -> R.drawable.rent_vector
    TransactionTag.BILLS -> R.drawable.bill_vector
    TransactionTag.GIFT -> R.drawable.gift_vector
    TransactionTag.INVESTMENT -> R.drawable.investment_vector
    TransactionTag.UTILS -> R.drawable.utiles_vector
    TransactionTag.SALARY -> R.drawable.money_vector
    TransactionTag.COUPONS -> R.drawable.bill_vector
    TransactionTag.CASHBACK -> R.drawable.gift_vector
}

private fun typeTint(type: TransactionType): Color = when (type) {
    TransactionType.EXPENSE -> Color(0xFFEF2727)
    TransactionType.INCOME -> Color(0xFF86DF3B)
}

@Composable
fun TransactionRow(
    transaction: Transaction,
    currencyCode: String,
    onClick: (Transaction) -> Unit,
    onLongPress: (Transaction) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(4.dp)
            .combinedClickable(
                onClick = { onClick(transaction) },
                onLongClick = { onLongPress(transaction) }
            ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
                    .padding(12.dp)
            ) {
                Icon(
                    painter = painterResource(tagIcon(transaction.tag)),
                    contentDescription = stringResource(R.string.transaction_type_tag),
                    tint = typeTint(transaction.transactionType)
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
                        modifier = Modifier.weight(1f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = Extra.convertLongDateToStringDate(transaction.date),
                        color = MaterialTheme.colorScheme.onBackground
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
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = transaction.paymentType.toString(),
                        color = MaterialTheme.colorScheme.onBackground
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
                tag = TransactionTag.FOOD,
                paymentType = PaymentType.CASH
            ),
            currencyCode = "$",
            onClick = {},
            onLongPress = {}
        )
    }
}
