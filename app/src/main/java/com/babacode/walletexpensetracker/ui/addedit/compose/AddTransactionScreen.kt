package com.babacode.walletexpensetracker.ui.addedit.compose

import android.content.Context
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
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.babacode.walletexpensetracker.R
import com.babacode.walletexpensetracker.data.model.PaymentType
import com.babacode.walletexpensetracker.data.model.Transaction
import com.babacode.walletexpensetracker.data.model.TransactionTag
import com.babacode.walletexpensetracker.data.model.TransactionType
import com.babacode.walletexpensetracker.ui.addedit.TransactionAddEditViewModel
import com.babacode.walletexpensetracker.ui.addedit.TransactionAddEditViewModel.AddEditTransactionEvent
import com.babacode.walletexpensetracker.ui.addedit.TransactionValidationError
import com.babacode.walletexpensetracker.ui.compose.WalletTopAppBar
import com.babacode.walletexpensetracker.ui.theme.WalletExpenseTheme

private fun Context.messageFor(error: TransactionValidationError): String = when (error) {
    TransactionValidationError.SELECT_TRANSACTION_TYPE -> getString(R.string.error_select_transaction_type)
    TransactionValidationError.ENTER_AMOUNT -> getString(R.string.error_enter_amount)
    TransactionValidationError.AMOUNT_TOO_LARGE -> getString(R.string.error_amount_too_large)
    TransactionValidationError.INVALID_AMOUNT -> getString(R.string.error_invalid_amount)
    TransactionValidationError.ENTER_NOTE -> getString(R.string.error_enter_note)
    TransactionValidationError.NOTE_TOO_LONG -> getString(R.string.error_note_too_long)
    TransactionValidationError.SELECT_TAG -> getString(R.string.error_select_tag)
    TransactionValidationError.SELECT_PAYMENT_MODE -> getString(R.string.error_select_payment_mode)
    TransactionValidationError.INVALID_DATE -> getString(R.string.error_invalid_date)
    TransactionValidationError.SAVE_ERROR -> getString(R.string.database_error)
}

@Composable
fun AddTransactionRoute(
    viewModel: TransactionAddEditViewModel,
    editTransaction: Transaction?,
    currencyCode: String,
    title: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    onNavigateBackWithResult: (Int) -> Unit
) {
    val dateFormatPattern = stringResource(R.string.date_formate)
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.initialize(editTransaction)
    }

    LaunchedEffect(Unit) {
        viewModel.addEditTransactionEvent.collect { event ->
            when (event) {
                is AddEditTransactionEvent.ShowInvalidAmount ->
                    snackbarHostState.showSnackbar(context.messageFor(event.error))
                is AddEditTransactionEvent.ShowInvalidNote ->
                    snackbarHostState.showSnackbar(context.messageFor(event.error))
                is AddEditTransactionEvent.ShowInvalidDate ->
                    snackbarHostState.showSnackbar(context.messageFor(event.error))
                is AddEditTransactionEvent.ShowSaveError ->
                    snackbarHostState.showSnackbar(context.messageFor(event.error))
                is AddEditTransactionEvent.ShowSelectTransactionType ->
                    snackbarHostState.showSnackbar(context.messageFor(event.error))
                is AddEditTransactionEvent.ShowSelectTransactionTag ->
                    snackbarHostState.showSnackbar(context.messageFor(event.error))
                is AddEditTransactionEvent.ShowSelectTransactionPaymentMode ->
                    snackbarHostState.showSnackbar(context.messageFor(event.error))
                is AddEditTransactionEvent.NavigateBackWithResult ->
                    onNavigateBackWithResult(event.result)
            }
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = { WalletTopAppBar(title = title, onBack = onBack) },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        AddTransactionScreen(
            modifier = Modifier.padding(innerPadding),
            currencyCode = currencyCode,
            dateFormatPattern = dateFormatPattern,
            uiState = uiState,
            onTypeSelected = viewModel::onTypeChanged,
            onAmountChange = viewModel::onAmountChanged,
            onNoteChange = viewModel::onNoteChanged,
            onDateSelected = viewModel::onDateChanged,
            onTagSelected = viewModel::onTagChanged,
            onPaymentModeSelected = viewModel::onPaymentModeChanged,
            onSaveClick = viewModel::onSaveClicked
        )
    }
}

@Composable
fun AddTransactionScreen(
    currencyCode: String,
    dateFormatPattern: String,
    uiState: TransactionAddEditUiState,
    onTypeSelected: (String) -> Unit,
    onAmountChange: (String) -> Unit,
    onNoteChange: (String) -> Unit,
    onDateSelected: (String) -> Unit,
    onTagSelected: (String) -> Unit,
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
            selected = uiState.type,
            onOptionSelected = onTypeSelected,
            modifier = Modifier.padding(16.dp)
        )

        TextField(
            value = uiState.amount,
            onValueChange = onAmountChange,
            label = { Text(stringResource(R.string.amount)) },
            prefix = { Text(currencyCode) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            colors = formFieldColors(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )

        TextField(
            value = uiState.note,
            onValueChange = onNoteChange,
            label = { Text(stringResource(R.string.add_a_note_to_self)) },
            singleLine = true,
            colors = formFieldColors(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )

        DateField(
            label = stringResource(R.string.date),
            value = uiState.date,
            dateFormatPattern = dateFormatPattern,
            onDateSelected = onDateSelected,
            modifier = Modifier.padding(16.dp)
        )

        DropdownField(
            label = stringResource(R.string.transaction_type_tag),
            options = TransactionTag.entries.map { it.toString() },
            selected = uiState.tag,
            onOptionSelected = onTagSelected,
            modifier = Modifier.padding(16.dp)
        )

        DropdownField(
            label = stringResource(R.string.payment_mode),
            options = PaymentType.entries.map { it.toString() },
            selected = uiState.paymentMode,
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
            uiState = TransactionAddEditUiState(
                type = "Expense",
                amount = "",
                note = "",
                date = "08 Aug, 2026",
                tag = "",
                paymentMode = ""
            ),
            onTypeSelected = {},
            onAmountChange = {},
            onNoteChange = {},
            onDateSelected = {},
            onTagSelected = {},
            onPaymentModeSelected = {},
            onSaveClick = {}
        )
    }
}
