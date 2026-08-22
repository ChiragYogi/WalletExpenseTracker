package com.babacode.walletexpensetracker.ui.budgets.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.babacode.walletexpensetracker.R
import com.babacode.walletexpensetracker.data.model.TagCatalog
import com.babacode.walletexpensetracker.ui.addedit.compose.DropdownField
import com.babacode.walletexpensetracker.ui.addedit.compose.formFieldColors
import com.babacode.walletexpensetracker.ui.budgets.BudgetFormState
import com.babacode.walletexpensetracker.ui.budgets.BudgetRow
import com.babacode.walletexpensetracker.ui.budgets.BudgetsUiState
import com.babacode.walletexpensetracker.ui.budgets.BudgetsViewModel
import com.babacode.walletexpensetracker.ui.compose.EmptyState
import com.babacode.walletexpensetracker.ui.compose.ThresholdProgressBar
import com.babacode.walletexpensetracker.ui.compose.WalletTopAppBar
import com.babacode.walletexpensetracker.ui.theme.ShapeTwoExtraLarge
import com.babacode.walletexpensetracker.ui.theme.WalletTheme
import com.babacode.walletexpensetracker.utiles.formatMoney
import kotlin.math.abs

@Composable
fun BudgetsRoute(
    currencyCode: String,
    viewModel: BudgetsViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val formState by viewModel.formState.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier,
        topBar = { WalletTopAppBar(title = stringResource(R.string.budgets_title)) }
    ) { innerPadding ->
        BudgetsScreen(
            modifier = Modifier.padding(innerPadding),
            currencyCode = currencyCode,
            uiState = uiState,
            formState = formState,
            onTagSelected = viewModel::onTagSelected,
            onLimitChanged = viewModel::onLimitChanged,
            onSaveBudgetClicked = viewModel::onSaveBudgetClicked,
            onDeleteBudget = viewModel::onDeleteBudget
        )
    }
}

@Composable
fun BudgetsScreen(
    currencyCode: String,
    uiState: BudgetsUiState,
    formState: BudgetFormState,
    onTagSelected: (String) -> Unit,
    onLimitChanged: (String) -> Unit,
    onSaveBudgetClicked: () -> Unit,
    onDeleteBudget: (BudgetRow) -> Unit,
    modifier: Modifier = Modifier
) {
    if (uiState.isLoading) return

    val spacing = WalletTheme.spacing

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(spacing.default),
        verticalArrangement = Arrangement.spacedBy(spacing.medium)
    ) {
        item {
            Text(
                text = stringResource(R.string.budgets_month_days_left, uiState.monthLabel, uiState.daysLeft),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        if (uiState.overspentTags.isNotEmpty()) {
            item { OverspendBanner(overspentTags = uiState.overspentTags) }
        }

        if (uiState.rows.isEmpty()) {
            item { EmptyState(message = stringResource(R.string.no_budgets_yet)) }
        } else {
            uiState.rows.forEach { row ->
                item(key = row.id) {
                    BudgetRowCard(
                        row = row,
                        currencyCode = currencyCode,
                        daysLeft = uiState.daysLeft,
                        onDelete = { onDeleteBudget(row) }
                    )
                }
            }
        }

        item {
            AddBudgetForm(
                formState = formState,
                onTagSelected = onTagSelected,
                onLimitChanged = onLimitChanged,
                onSaveBudgetClicked = onSaveBudgetClicked
            )
        }
    }
}

@Composable
private fun OverspendBanner(overspentTags: List<String>) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = WalletTheme.extendedColors.expenseSoft, shape = ShapeTwoExtraLarge)
            .padding(WalletTheme.spacing.default)
    ) {
        Icon(
            painter = painterResource(R.drawable.warning_vector),
            contentDescription = null,
            tint = WalletTheme.extendedColors.expense,
            modifier = Modifier
                .padding(top = 2.dp)
                .size(20.dp)
        )
        Text(
            text = stringResource(R.string.overspent_warning, overspentTags.joinToString(", ")),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(start = WalletTheme.spacing.small)
        )
    }
}

@Composable
private fun BudgetRowCard(
    row: BudgetRow,
    currencyCode: String,
    daysLeft: Int,
    onDelete: () -> Unit
) {
    val spacing = WalletTheme.spacing
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = ShapeTwoExtraLarge,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(spacing.default)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = row.tag, style = MaterialTheme.typography.labelLarge)
                IconButton(onClick = onDelete) {
                    Icon(
                        painter = painterResource(R.drawable.delete_vector),
                        contentDescription = stringResource(R.string.budget_delete_description, row.tag),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            ThresholdProgressBar(
                progress = if (row.limitAmount > 0) (row.used / row.limitAmount).toFloat() else 0f,
                modifier = Modifier.padding(top = spacing.extraSmall)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = spacing.small),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${formatMoney(row.used, currencyCode)} of ${formatMoney(row.limitAmount, currencyCode)}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = if (row.isOver) {
                        stringResource(R.string.over_by, formatMoney(abs(row.remaining), currencyCode))
                    } else {
                        stringResource(R.string.remaining_per_day, formatMoney(row.remaining / daysLeft, currencyCode))
                    },
                    style = MaterialTheme.typography.labelSmall,
                    color = if (row.isOver) WalletTheme.extendedColors.expense else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun AddBudgetForm(
    formState: BudgetFormState,
    onTagSelected: (String) -> Unit,
    onLimitChanged: (String) -> Unit,
    onSaveBudgetClicked: () -> Unit
) {
    val spacing = WalletTheme.spacing
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = ShapeTwoExtraLarge,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(spacing.default)) {
            Text(text = stringResource(R.string.set_a_budget_title), style = MaterialTheme.typography.labelLarge)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = spacing.medium),
                horizontalArrangement = Arrangement.spacedBy(spacing.small),
                verticalAlignment = Alignment.CenterVertically
            ) {
                DropdownField(
                    label = stringResource(R.string.category_label),
                    options = TagCatalog.EXPENSE_TAGS,
                    selected = formState.tag,
                    onOptionSelected = onTagSelected,
                    modifier = Modifier.weight(1f)
                )
                TextField(
                    value = formState.limit,
                    onValueChange = { raw -> onLimitChanged(raw.filter { it.isDigit() }) },
                    placeholder = { Text(stringResource(R.string.limit_placeholder)) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = formFieldColors(),
                    modifier = Modifier.width(96.dp)
                )
                FilledIconButton(
                    onClick = onSaveBudgetClicked,
                    colors = IconButtonDefaults.filledIconButtonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.add_transaction_vectore),
                        contentDescription = stringResource(R.string.save_budget_description)
                    )
                }
            }
        }
    }
}
