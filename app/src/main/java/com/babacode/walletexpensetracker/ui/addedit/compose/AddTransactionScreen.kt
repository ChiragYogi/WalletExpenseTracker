package com.babacode.walletexpensetracker.ui.addedit.compose

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.babacode.walletexpensetracker.R
import com.babacode.walletexpensetracker.data.model.PaymentMode
import com.babacode.walletexpensetracker.data.model.TagCatalog
import com.babacode.walletexpensetracker.data.model.Transaction
import com.babacode.walletexpensetracker.data.model.TransactionType
import com.babacode.walletexpensetracker.ui.addedit.TransactionAddEditViewModel
import com.babacode.walletexpensetracker.ui.addedit.TransactionAddEditViewModel.AddEditTransactionEvent
import com.babacode.walletexpensetracker.ui.addedit.TransactionValidationError
import com.babacode.walletexpensetracker.ui.compose.TagChipPicker
import com.babacode.walletexpensetracker.ui.compose.WalletTopAppBar
import com.babacode.walletexpensetracker.ui.theme.ShapeExtraLarge
import com.babacode.walletexpensetracker.ui.theme.ShapeMedium
import com.babacode.walletexpensetracker.ui.theme.ShapeThreeExtraLarge
import com.babacode.walletexpensetracker.ui.theme.ShapeTwoExtraLarge
import com.babacode.walletexpensetracker.ui.theme.WalletExpenseTheme
import com.babacode.walletexpensetracker.ui.theme.WalletTheme
import com.babacode.walletexpensetracker.utiles.Extra

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
    onDeleteClick: (Transaction) -> Unit,
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
            onRepeatMonthlyChanged = viewModel::onRepeatMonthlyChanged,
            onSaveClick = viewModel::onSaveClicked,
            onDeleteClick = { editTransaction?.let(onDeleteClick) }
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
    modifier: Modifier = Modifier,
    onRepeatMonthlyChanged: (Boolean) -> Unit = {},
    onDeleteClick: () -> Unit = {}
) {
    val isEditing = uiState.transactionId != 0
    val spacing = WalletTheme.spacing

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(spacing.default),
        verticalArrangement = Arrangement.spacedBy(spacing.medium)
    ) {
        TypeToggle(selectedType = uiState.type, onTypeSelected = onTypeSelected)

        AmountCard(
            currencyCode = currencyCode,
            amount = uiState.amount,
            onAmountChange = { raw -> onAmountChange(raw.filter { it.isDigit() }) }
        )

        Column {
            FieldLabel(stringResource(R.string.add_a_note_to_self))
            OutlinedTextField(
                value = uiState.note,
                onValueChange = onNoteChange,
                placeholder = { Text(stringResource(R.string.what_s_this_for)) },
                singleLine = true,
                shape = ShapeMedium,
                colors = formFieldColors(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = spacing.extraSmall)
            )
        }

        DateField(
            label = stringResource(R.string.date),
            value = uiState.date,
            dateFormatPattern = dateFormatPattern,
            onDateSelected = onDateSelected
        )

        Column {
            Text(
                text = stringResource(R.string.transaction_type_tag),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            TagChipPicker(
                tags = TagCatalog.tagsFor(Extra.transactionType(uiState.type)),
                selectedTag = uiState.tag,
                onTagSelected = onTagSelected,
                modifier = Modifier.padding(top = spacing.small)
            )
        }

        DropdownField(
            label = stringResource(R.string.payment_mode),
            options = PaymentMode.entries.map { it.toString() },
            selected = uiState.paymentMode,
            onOptionSelected = onPaymentModeSelected
        )

        if (!isEditing) {
            RepeatMonthlyRow(checked = uiState.repeatMonthly, onCheckedChange = onRepeatMonthlyChanged)
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(spacing.small)
        ) {
            if (isEditing) {
                Button(
                    onClick = onDeleteClick,
                    shape = ShapeTwoExtraLarge,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = WalletTheme.extendedColors.expenseSoft,
                        contentColor = WalletTheme.extendedColors.expense
                    )
                ) {
                    Icon(
                        painter = painterResource(R.drawable.delete_vector),
                        contentDescription = null,
                        modifier = Modifier.padding(end = spacing.extraSmall)
                    )
                    Text(stringResource(R.string.delete_button))
                }
            }
            Button(
                onClick = onSaveClick,
                shape = ShapeTwoExtraLarge,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    painter = painterResource(R.drawable.save_vector),
                    contentDescription = null,
                    modifier = Modifier.padding(end = spacing.extraSmall)
                )
                Text(
                    text = stringResource(
                        if (isEditing) R.string.save_transaction_changes_button else R.string.save_new_transaction_button
                    )
                )
            }
        }
    }
}

@Composable
private fun TypeToggle(selectedType: String, onTypeSelected: (String) -> Unit) {
    val extendedColors = WalletTheme.extendedColors
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = MaterialTheme.colorScheme.secondary, shape = ShapeExtraLarge)
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        TransactionType.entries.forEach { type ->
            val label = type.toString()
            val selected = selectedType == label
            val (background, contentColor) = when {
                !selected -> Color.Transparent to MaterialTheme.colorScheme.onSurfaceVariant
                type == TransactionType.INCOME -> extendedColors.incomeSoft to extendedColors.income
                else -> extendedColors.expenseSoft to extendedColors.expense
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(ShapeMedium)
                    .background(color = background, shape = ShapeMedium)
                    .clickable { onTypeSelected(label) }
                    .padding(vertical = WalletTheme.spacing.small),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelLarge,
                    color = contentColor
                )
            }
        }
    }
}

@Composable
private fun AmountCard(
    currencyCode: String,
    amount: String,
    onAmountChange: (String) -> Unit
) {
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
                text = stringResource(R.string.amount),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = WalletTheme.spacing.extraSmall)
            ) {
                Text(
                    text = currencyCode,
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                TextField(
                    value = amount,
                    onValueChange = onAmountChange,
                    textStyle = MaterialTheme.typography.headlineLarge.copy(textAlign = TextAlign.Center),
                    placeholder = {
                        Text(
                            text = "0",
                            style = MaterialTheme.typography.headlineLarge.copy(textAlign = TextAlign.Center),
                            modifier = Modifier.fillMaxWidth()
                        )
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = amountFieldColors(),
                    modifier = Modifier
                        .padding(start = WalletTheme.spacing.extraSmall)
                        .width(160.dp)
                )
            }
        }
    }
}

@Composable
private fun amountFieldColors() = TextFieldDefaults.colors(
    focusedContainerColor = Color.Transparent,
    unfocusedContainerColor = Color.Transparent,
    disabledContainerColor = Color.Transparent,
    focusedIndicatorColor = Color.Transparent,
    unfocusedIndicatorColor = Color.Transparent,
    disabledIndicatorColor = Color.Transparent
)

@Composable
private fun RepeatMonthlyRow(checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = MaterialTheme.colorScheme.surface, shape = ShapeTwoExtraLarge)
            .padding(horizontal = WalletTheme.spacing.default, vertical = WalletTheme.spacing.small),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = stringResource(R.string.repeat_monthly_label), style = MaterialTheme.typography.bodyMedium)
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.primary)
        )
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
                tag = "Food",
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

@Preview(showBackground = true)
@Composable
private fun EditTransactionScreenPreview() {
    WalletExpenseTheme {
        AddTransactionScreen(
            currencyCode = "$",
            dateFormatPattern = "dd MMM, yyyy",
            uiState = TransactionAddEditUiState(
                transactionId = 5,
                type = "Expense",
                amount = "450",
                note = "Groceries",
                date = "08 Aug, 2026",
                tag = "Food",
                paymentMode = "Cash"
            ),
            onTypeSelected = {},
            onAmountChange = {},
            onNoteChange = {},
            onDateSelected = {},
            onTagSelected = {},
            onPaymentModeSelected = {},
            onSaveClick = {},
            onDeleteClick = {}
        )
    }
}
