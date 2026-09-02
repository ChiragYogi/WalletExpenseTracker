package com.babacode.walletexpensetracker.ui.recurring.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.babacode.walletexpensetracker.R
import com.babacode.walletexpensetracker.data.model.Frequency
import com.babacode.walletexpensetracker.data.model.PaymentMode
import com.babacode.walletexpensetracker.data.model.RecurringRule
import com.babacode.walletexpensetracker.data.model.TagCatalog
import com.babacode.walletexpensetracker.data.model.TransactionType
import com.babacode.walletexpensetracker.ui.addedit.compose.DateField
import com.babacode.walletexpensetracker.ui.addedit.compose.DropdownField
import com.babacode.walletexpensetracker.ui.addedit.compose.formFieldColors
import com.babacode.walletexpensetracker.ui.compose.EmptyState
import com.babacode.walletexpensetracker.ui.compose.TagChipPicker
import com.babacode.walletexpensetracker.ui.compose.WalletTopAppBar
import com.babacode.walletexpensetracker.ui.recurring.RecurringFormState
import com.babacode.walletexpensetracker.ui.recurring.RecurringUiState
import com.babacode.walletexpensetracker.ui.recurring.RecurringViewModel
import com.babacode.walletexpensetracker.ui.recurring.nextOccurrence
import com.babacode.walletexpensetracker.ui.theme.ShapeExtraLarge
import com.babacode.walletexpensetracker.ui.theme.ShapeMedium
import com.babacode.walletexpensetracker.ui.theme.ShapeTwoExtraLarge
import com.babacode.walletexpensetracker.ui.theme.WalletTheme
import com.babacode.walletexpensetracker.utiles.Extra
import com.babacode.walletexpensetracker.utiles.formatMoney

@Composable
fun RecurringRoute(
    currencyCode: String,
    viewModel: RecurringViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dateFormatPattern = stringResource(R.string.date_formate)
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val formState by viewModel.formState.collectAsStateWithLifecycle()
    var showForm by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        modifier = modifier,
        topBar = {
            WalletTopAppBar(
                title = stringResource(R.string.recurring_title),
                onBack = onBack,
                actions = {
                    IconButton(onClick = { showForm = !showForm }) {
                        Icon(
                            painter = painterResource(R.drawable.add_transaction_vectore),
                            contentDescription = stringResource(R.string.add_recurring_rule_description)
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        RecurringScreen(
            modifier = Modifier.padding(innerPadding),
            currencyCode = currencyCode,
            dateFormatPattern = dateFormatPattern,
            uiState = uiState,
            showForm = showForm,
            formState = formState,
            onTypeSelected = viewModel::onTypeChanged,
            onAmountChanged = viewModel::onAmountChanged,
            onNoteChanged = viewModel::onNoteChanged,
            onTagSelected = viewModel::onTagChanged,
            onModeSelected = viewModel::onModeChanged,
            onFrequencySelected = viewModel::onFrequencyChanged,
            onNextDateChanged = viewModel::onNextDateChanged,
            onSaveRuleClicked = { viewModel.onSaveRuleClicked(onSaved = { showForm = false }) },
            onRunNowClicked = viewModel::onRunNowClicked,
            onToggleActive = viewModel::onToggleActive,
            onDeleteRule = viewModel::onDeleteRule
        )
    }
}

@Composable
fun RecurringScreen(
    currencyCode: String,
    dateFormatPattern: String,
    uiState: RecurringUiState,
    showForm: Boolean,
    formState: RecurringFormState,
    onTypeSelected: (String) -> Unit,
    onAmountChanged: (String) -> Unit,
    onNoteChanged: (String) -> Unit,
    onTagSelected: (String) -> Unit,
    onModeSelected: (String) -> Unit,
    onFrequencySelected: (Frequency) -> Unit,
    onNextDateChanged: (String) -> Unit,
    onSaveRuleClicked: () -> Unit,
    onRunNowClicked: (RecurringRule) -> Unit,
    onToggleActive: (RecurringRule) -> Unit,
    onDeleteRule: (RecurringRule) -> Unit,
    modifier: Modifier = Modifier
) {
    if (uiState.isLoading) return

    val spacing = WalletTheme.spacing

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(spacing.default),
        verticalArrangement = Arrangement.spacedBy(spacing.medium)
    ) {
        if (showForm) {
            item {
                AddRuleForm(
                    dateFormatPattern = dateFormatPattern,
                    formState = formState,
                    onTypeSelected = onTypeSelected,
                    onAmountChanged = onAmountChanged,
                    onNoteChanged = onNoteChanged,
                    onTagSelected = onTagSelected,
                    onModeSelected = onModeSelected,
                    onFrequencySelected = onFrequencySelected,
                    onNextDateChanged = onNextDateChanged,
                    onSaveRuleClicked = onSaveRuleClicked
                )
            }
        }

        if (uiState.rules.isEmpty()) {
            item { EmptyState(message = stringResource(R.string.no_recurring_entries_yet)) }
        } else {
            items(uiState.rules, key = { it.id }) { rule ->
                RuleCard(
                    rule = rule,
                    currencyCode = currencyCode,
                    onRunNowClicked = { onRunNowClicked(rule) },
                    onToggleActive = { onToggleActive(rule) },
                    onDeleteRule = { onDeleteRule(rule) }
                )
            }
        }
    }
}

@Composable
private fun AddRuleForm(
    dateFormatPattern: String,
    formState: RecurringFormState,
    onTypeSelected: (String) -> Unit,
    onAmountChanged: (String) -> Unit,
    onNoteChanged: (String) -> Unit,
    onTagSelected: (String) -> Unit,
    onModeSelected: (String) -> Unit,
    onFrequencySelected: (Frequency) -> Unit,
    onNextDateChanged: (String) -> Unit,
    onSaveRuleClicked: () -> Unit
) {
    val spacing = WalletTheme.spacing
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = ShapeTwoExtraLarge,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(spacing.default),
            verticalArrangement = Arrangement.spacedBy(spacing.medium)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = MaterialTheme.colorScheme.secondary, shape = ShapeExtraLarge)
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                TransactionType.entries.forEach { type ->
                    val label = type.toString()
                    val selected = formState.type == label
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .background(
                                color = if (selected) MaterialTheme.colorScheme.primary else Color.Transparent,
                                shape = ShapeMedium
                            )
                            .clickable { onTypeSelected(label) }
                            .padding(vertical = spacing.small),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelLarge,
                            color = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            TextField(
                value = formState.amount,
                onValueChange = onAmountChanged,
                placeholder = { Text(stringResource(R.string.amount_placeholder)) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = formFieldColors(),
                shape = ShapeMedium,
                modifier = Modifier.fillMaxWidth()
            )

            TextField(
                value = formState.note,
                onValueChange = onNoteChanged,
                placeholder = { Text(stringResource(R.string.note_placeholder)) },
                singleLine = true,
                colors = formFieldColors(),
                shape = ShapeMedium,
                modifier = Modifier.fillMaxWidth()
            )

            TagChipPicker(
                tags = TagCatalog.tagsFor(Extra.transactionType(formState.type)),
                selectedTag = formState.tag,
                onTagSelected = onTagSelected
            )

            DropdownField(
                label = stringResource(R.string.payment_mode),
                options = PaymentMode.entries.map { it.toString() },
                selected = formState.mode,
                onOptionSelected = onModeSelected
            )

            DropdownField(
                label = stringResource(R.string.frequency_label),
                options = Frequency.entries.map { it.toString() },
                selected = formState.frequency.toString(),
                onOptionSelected = { selected ->
                    Frequency.entries.firstOrNull { it.toString() == selected }?.let(onFrequencySelected)
                }
            )

            DateField(
                label = stringResource(R.string.next_run_date_label),
                value = formState.nextDate,
                dateFormatPattern = dateFormatPattern,
                onDateSelected = onNextDateChanged
            )

            Button(
                onClick = onSaveRuleClicked,
                shape = ShapeTwoExtraLarge,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.save_rule_button))
            }
        }
    }
}

@Composable
private fun RuleCard(
    rule: RecurringRule,
    currencyCode: String,
    onRunNowClicked: () -> Unit,
    onToggleActive: () -> Unit,
    onDeleteRule: () -> Unit
) {
    val spacing = WalletTheme.spacing
    val extendedColors = WalletTheme.extendedColors

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = ShapeTwoExtraLarge,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(spacing.default)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = rule.note.ifBlank { rule.tag },
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.weight(1f),
                    maxLines = 1
                )
                val amountColor = if (rule.type == TransactionType.INCOME) extendedColors.income else extendedColors.expense
                val sign = if (rule.type == TransactionType.INCOME) "+" else "-"
                Text(
                    text = "$sign${formatMoney(rule.amount, currencyCode)}",
                    style = MaterialTheme.typography.labelLarge,
                    color = amountColor
                )
            }

            Row(
                modifier = Modifier.padding(top = spacing.extraSmall),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(R.drawable.weekly_calender_vector),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = "${rule.frequency} · next ${Extra.convertLongDateToStringDate(rule.nextDate)}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(start = spacing.extraSmall)
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = spacing.medium),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(spacing.small)
            ) {
                Text(
                    text = stringResource(if (rule.active) R.string.active_label else R.string.paused_label),
                    style = MaterialTheme.typography.labelSmall,
                    color = if (rule.active) extendedColors.income else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .background(
                            color = if (rule.active) extendedColors.incomeSoft else MaterialTheme.colorScheme.secondary,
                            shape = ShapeExtraLarge
                        )
                        .clickable(onClick = onToggleActive)
                        .padding(horizontal = spacing.medium, vertical = spacing.extraSmall)
                )
                Text(
                    text = stringResource(R.string.run_now_button),
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier
                        .background(color = MaterialTheme.colorScheme.secondary, shape = ShapeExtraLarge)
                        .clickable(onClick = onRunNowClicked)
                        .padding(horizontal = spacing.medium, vertical = spacing.extraSmall)
                )
                Text(
                    text = stringResource(
                        R.string.then_date_prefix,
                        Extra.convertLongDateToStringDate(nextOccurrence(rule.nextDate, rule.frequency))
                    ),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = onDeleteRule) {
                    Icon(
                        painter = painterResource(R.drawable.delete_vector),
                        contentDescription = stringResource(R.string.delete_rule_description),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
