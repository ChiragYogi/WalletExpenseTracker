package com.babacode.walletexpensetracker.ui.addedit.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.babacode.walletexpensetracker.R
import com.babacode.walletexpensetracker.data.model.PaymentType
import com.babacode.walletexpensetracker.data.model.Transaction
import com.babacode.walletexpensetracker.data.model.TransactionTag
import com.babacode.walletexpensetracker.data.model.TransactionType
import com.babacode.walletexpensetracker.ui.addedit.TransactionAddEditViewModel
import com.babacode.walletexpensetracker.ui.addedit.TransactionAddEditViewModel.AddEditTransactionEvent
import com.babacode.walletexpensetracker.ui.theme.WalletExpenseTheme
import com.babacode.walletexpensetracker.utiles.Extra.convertLongDateToStringDate
import com.babacode.walletexpensetracker.utiles.Extra.currentDayDate

@Composable
fun AddTransactionRoute(
    viewModel: TransactionAddEditViewModel,
    editTransaction: Transaction?,
    currencyCode: String,
    modifier: Modifier = Modifier,
    onNavigateBackWithResult: (Int) -> Unit
) {
    val dateFormatPattern = stringResource(R.string.date_formate)

    var type by rememberSaveable { mutableStateOf(editTransaction?.transactionType?.toString().orEmpty()) }
    var amount by rememberSaveable {
        mutableStateOf(editTransaction?.amount?.toInt()?.toString().orEmpty())
    }
    var note by rememberSaveable { mutableStateOf(editTransaction?.note.orEmpty()) }
    var date by rememberSaveable {
        mutableStateOf(
            editTransaction?.let { convertLongDateToStringDate(it.date) }
                ?: convertLongDateToStringDate(currentDayDate())
        )
    }
    var tag by rememberSaveable { mutableStateOf(editTransaction?.tag?.toString().orEmpty()) }
    var paymentMode by rememberSaveable { mutableStateOf(editTransaction?.paymentType?.toString().orEmpty()) }

    var amountError by rememberSaveable { mutableStateOf<String?>(null) }
    var noteError by rememberSaveable { mutableStateOf<String?>(null) }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.addEditTransactionEvent.collect { event ->
            when (event) {
                is AddEditTransactionEvent.ShowInvalidAmount -> {
                    amountError = event.msg
                    snackbarHostState.showSnackbar(event.msg)
                }
                is AddEditTransactionEvent.ShowInvalidNote -> {
                    noteError = event.msg
                    snackbarHostState.showSnackbar(event.msg)
                }
                is AddEditTransactionEvent.ShowSelectTransactionType ->
                    snackbarHostState.showSnackbar(event.msg)
                is AddEditTransactionEvent.ShowSelectTransactionTag ->
                    snackbarHostState.showSnackbar(event.msg)
                is AddEditTransactionEvent.ShowSelectTransactionPaymentMode ->
                    snackbarHostState.showSnackbar(event.msg)
                is AddEditTransactionEvent.NavigateBackWithResult ->
                    onNavigateBackWithResult(event.result)
            }
        }
    }

    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        AddTransactionScreen(
            modifier = Modifier.padding(innerPadding),
            currencyCode = currencyCode,
            dateFormatPattern = dateFormatPattern,
            type = type,
            onTypeSelected = { type = it },
            amount = amount,
            onAmountChange = { amount = it },
            amountError = amountError,
            note = note,
            onNoteChange = { if (it.length <= 40) note = it },
            noteError = noteError,
            date = date,
            onDateSelected = { date = it },
            tag = tag,
            onTagSelected = { tag = it },
            paymentMode = paymentMode,
            onPaymentModeSelected = { paymentMode = it },
            onSaveClick = {
                val editId = editTransaction?.id ?: 0
                viewModel.validateAndInsertOrUpdate(type, amount, note, date, tag, paymentMode, editId)
            }
        )
    }
}

@Composable
fun AddTransactionScreen(
    currencyCode: String,
    dateFormatPattern: String,
    type: String,
    onTypeSelected: (String) -> Unit,
    amount: String,
    onAmountChange: (String) -> Unit,
    amountError: String?,
    note: String,
    onNoteChange: (String) -> Unit,
    noteError: String?,
    date: String,
    onDateSelected: (String) -> Unit,
    tag: String,
    onTagSelected: (String) -> Unit,
    paymentMode: String,
    onPaymentModeSelected: (String) -> Unit,
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        DropdownField(
            label = stringResource(R.string.transaction_type),
            options = TransactionType.entries.map { it.toString() },
            selected = type,
            onOptionSelected = onTypeSelected,
            modifier = Modifier.padding(16.dp)
        )

        TextField(
            value = amount,
            onValueChange = onAmountChange,
            label = { Text(stringResource(R.string.amount)) },
            prefix = { Text(currencyCode) },
            isError = amountError != null,
            supportingText = amountError?.let { { Text(it) } },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            colors = formFieldColors(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )

        TextField(
            value = note,
            onValueChange = onNoteChange,
            label = { Text(stringResource(R.string.add_a_note_to_self)) },
            isError = noteError != null,
            supportingText = noteError?.let { { Text(it) } },
            singleLine = true,
            colors = formFieldColors(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )

        DateField(
            label = stringResource(R.string.date),
            value = date,
            dateFormatPattern = dateFormatPattern,
            onDateSelected = onDateSelected,
            modifier = Modifier.padding(16.dp)
        )

        DropdownField(
            label = stringResource(R.string.transaction_type_tag),
            options = TransactionTag.entries.map { it.toString() },
            selected = tag,
            onOptionSelected = onTagSelected,
            modifier = Modifier.padding(16.dp)
        )

        DropdownField(
            label = stringResource(R.string.payment_mode),
            options = PaymentType.entries.map { it.toString() },
            selected = paymentMode,
            onOptionSelected = onPaymentModeSelected,
            modifier = Modifier.padding(16.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp, end = 16.dp),
            horizontalArrangement = Arrangement.End
        ) {
            FloatingActionButton(
                onClick = onSaveClick,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    painter = painterResource(R.drawable.save_vector),
                    contentDescription = stringResource(R.string.save_transaction_button)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AddTransactionScreenPreview() {
    WalletExpenseTheme {
        AddTransactionScreen(
            currencyCode = "$",
            dateFormatPattern = "dd MMM, yyyy",
            type = "Expense",
            onTypeSelected = {},
            amount = "",
            onAmountChange = {},
            amountError = null,
            note = "",
            onNoteChange = {},
            noteError = null,
            date = "08 Aug, 2026",
            onDateSelected = {},
            tag = "",
            onTagSelected = {},
            paymentMode = "",
            onPaymentModeSelected = {},
            onSaveClick = {}
        )
    }
}
