package com.babacode.walletexpensetracker.ui.calender.compose

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.babacode.walletexpensetracker.R
import com.babacode.walletexpensetracker.data.model.PaymentType
import com.babacode.walletexpensetracker.data.model.Transaction
import com.babacode.walletexpensetracker.data.model.TransactionTag
import com.babacode.walletexpensetracker.data.model.TransactionType
import com.babacode.walletexpensetracker.ui.ADD_TRANSACTION_RESULT_OK
import com.babacode.walletexpensetracker.ui.EDIT_TRANSACTION_RESULT_OK
import com.babacode.walletexpensetracker.ui.calender.CalenderViewViewModel
import com.babacode.walletexpensetracker.ui.compose.TransactionRow
import com.babacode.walletexpensetracker.ui.compose.WalletTopAppBar
import com.babacode.walletexpensetracker.ui.theme.WalletExpenseTheme
import com.babacode.walletexpensetracker.utiles.Extra
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

private fun todayUtcMillis(): Long =
    LocalDate.now().atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()

@Composable
fun CalenderRoute(
    currencyCode: String,
    viewModel: CalenderViewViewModel,
    resultEvent: Int?,
    onResultEventConsumed: () -> Unit,
    onTransactionClick: (Transaction) -> Unit,
    onLongPress: (Transaction) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val snackbarHostState = remember { SnackbarHostState() }

    val resultMessage = when (resultEvent) {
        ADD_TRANSACTION_RESULT_OK -> stringResource(R.string.transaction_added)
        EDIT_TRANSACTION_RESULT_OK -> stringResource(R.string.transaction_update)
        else -> null
    }

    LaunchedEffect(resultEvent) {
        if (resultMessage != null) {
            snackbarHostState.showSnackbar(resultMessage)
            onResultEventConsumed()
        }
    }

    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = todayUtcMillis())

    LaunchedEffect(datePickerState.selectedDateMillis) {
        val millis = datePickerState.selectedDateMillis ?: return@LaunchedEffect
        val localDate = Instant.ofEpochMilli(millis).atZone(ZoneOffset.UTC).toLocalDate()
        viewModel.getSelectedDateFromCalender(Extra.convertLocalDateToLong(localDate))
    }

    val transactions by viewModel.selectedDateTransaction.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier,
        topBar = { WalletTopAppBar(title = stringResource(R.string.calender), onBack = onBack) },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        CalenderScreen(
            modifier = Modifier.padding(innerPadding),
            datePickerState = datePickerState,
            currencyCode = currencyCode,
            transactions = transactions,
            onTransactionClick = onTransactionClick,
            onLongPress = onLongPress
        )
    }
}

@Composable
fun CalenderScreen(
    datePickerState: DatePickerState,
    currencyCode: String,
    transactions: List<Transaction>,
    onTransactionClick: (Transaction) -> Unit,
    onLongPress: (Transaction) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        DatePicker(
            state = datePickerState,
            title = null,
            headline = null,
            showModeToggle = false
        )

        if (transactions.isEmpty()) {
            Text(
                text = stringResource(R.string.noTransaction),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onBackground
            )
        } else {
            Column(modifier = Modifier.padding(horizontal = 8.dp)) {
                transactions.forEach { transaction ->
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

@Preview(showBackground = true)
@Composable
private fun CalenderScreenPreview() {
    WalletExpenseTheme {
        CalenderScreen(
            datePickerState = rememberDatePickerState(initialSelectedDateMillis = todayUtcMillis()),
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
                )
            ),
            onTransactionClick = {},
            onLongPress = {}
        )
    }
}
