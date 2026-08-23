package com.babacode.walletexpensetracker.ui.search.compose

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.babacode.walletexpensetracker.R
import com.babacode.walletexpensetracker.data.model.PaymentMode
import com.babacode.walletexpensetracker.data.model.TagCatalog
import com.babacode.walletexpensetracker.data.model.Transaction
import com.babacode.walletexpensetracker.data.model.TransactionType
import com.babacode.walletexpensetracker.ui.addedit.compose.DateField
import com.babacode.walletexpensetracker.ui.addedit.compose.DropdownField
import com.babacode.walletexpensetracker.ui.addedit.compose.formFieldColors
import com.babacode.walletexpensetracker.ui.compose.EmptyState
import com.babacode.walletexpensetracker.ui.compose.transactionDateGroups
import com.babacode.walletexpensetracker.ui.compose.WalletTopAppBar
import com.babacode.walletexpensetracker.ui.search.SearchFilters
import com.babacode.walletexpensetracker.ui.search.SearchUiState
import com.babacode.walletexpensetracker.ui.search.SearchViewModel
import com.babacode.walletexpensetracker.ui.theme.ShapeTwoExtraLarge
import com.babacode.walletexpensetracker.ui.theme.WalletTheme
import com.babacode.walletexpensetracker.utiles.Extra
import com.babacode.walletexpensetracker.utiles.FinanceCompute
import com.babacode.walletexpensetracker.utiles.formatMoney
import kotlinx.coroutines.launch

@Composable
fun SearchRoute(
    currencyCode: String,
    viewModel: SearchViewModel,
    onBack: () -> Unit,
    onTransactionClick: (Transaction) -> Unit,
    onLongPress: (Transaction) -> Unit,
    modifier: Modifier = Modifier
) {
    val dateFormatPattern = stringResource(R.string.date_formate)
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val filters by viewModel.filters.collectAsStateWithLifecycle()
    var showFilters by rememberSaveable { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val successMessage = stringResource(R.string.csv_export_success)
    val failureMessage = stringResource(R.string.csv_export_failure)

    val exportLauncher = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("text/csv")) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult
        coroutineScope.launch {
            val result = viewModel.exportCsv(uri)
            snackbarHostState.showSnackbar(if (result.isSuccess) successMessage else failureMessage)
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            WalletTopAppBar(
                title = stringResource(R.string.search_title),
                onBack = onBack,
                actions = {
                    IconButton(onClick = { showFilters = !showFilters }) {
                        Icon(
                            painter = painterResource(R.drawable.tune_vector),
                            contentDescription = stringResource(R.string.toggle_filters_description)
                        )
                    }
                    IconButton(onClick = { exportLauncher.launch("transactions.csv") }) {
                        Icon(
                            painter = painterResource(R.drawable.download_vector),
                            contentDescription = stringResource(R.string.export_csv_description)
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        SearchScreen(
            modifier = Modifier.padding(innerPadding),
            currencyCode = currencyCode,
            dateFormatPattern = dateFormatPattern,
            uiState = uiState,
            filters = filters,
            showFilters = showFilters,
            onQueryChanged = viewModel::onQueryChanged,
            onTypeSelected = viewModel::onTypeSelected,
            onTagSelected = viewModel::onTagSelected,
            onModeSelected = viewModel::onModeSelected,
            onFromDateChanged = viewModel::onFromDateChanged,
            onToDateChanged = viewModel::onToDateChanged,
            onMinAmountChanged = viewModel::onMinAmountChanged,
            onMaxAmountChanged = viewModel::onMaxAmountChanged,
            onTransactionClick = onTransactionClick,
            onLongPress = onLongPress
        )
    }
}

@Composable
fun SearchScreen(
    currencyCode: String,
    dateFormatPattern: String,
    uiState: SearchUiState,
    filters: SearchFilters,
    showFilters: Boolean,
    onQueryChanged: (String) -> Unit,
    onTypeSelected: (TransactionType?) -> Unit,
    onTagSelected: (String?) -> Unit,
    onModeSelected: (PaymentMode?) -> Unit,
    onFromDateChanged: (Long?) -> Unit,
    onToDateChanged: (Long?) -> Unit,
    onMinAmountChanged: (String) -> Unit,
    onMaxAmountChanged: (String) -> Unit,
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
            TextField(
                value = filters.query,
                onValueChange = onQueryChanged,
                placeholder = { Text(stringResource(R.string.search_hint)) },
                leadingIcon = { Icon(painterResource(R.drawable.search_vector), contentDescription = null) },
                singleLine = true,
                shape = ShapeTwoExtraLarge,
                colors = formFieldColors(),
                modifier = Modifier.fillMaxWidth()
            )
        }

        if (showFilters) {
            item {
                FilterPanel(
                    dateFormatPattern = dateFormatPattern,
                    filters = filters,
                    onTypeSelected = onTypeSelected,
                    onTagSelected = onTagSelected,
                    onModeSelected = onModeSelected,
                    onFromDateChanged = onFromDateChanged,
                    onToDateChanged = onToDateChanged,
                    onMinAmountChanged = onMinAmountChanged,
                    onMaxAmountChanged = onMaxAmountChanged
                )
            }
        }

        item {
            ResultsSummaryRow(currencyCode = currencyCode, resultCount = uiState.results.size, totals = uiState.totals)
        }

        if (uiState.results.isEmpty()) {
            item { EmptyState(message = stringResource(R.string.no_transactions_match_filters)) }
        } else {
            transactionDateGroups(
                transactions = uiState.results,
                currencyCode = currencyCode,
                onClick = onTransactionClick,
                onLongPress = onLongPress
            )
        }
    }
}

@Composable
private fun FilterPanel(
    dateFormatPattern: String,
    filters: SearchFilters,
    onTypeSelected: (TransactionType?) -> Unit,
    onTagSelected: (String?) -> Unit,
    onModeSelected: (PaymentMode?) -> Unit,
    onFromDateChanged: (Long?) -> Unit,
    onToDateChanged: (Long?) -> Unit,
    onMinAmountChanged: (String) -> Unit,
    onMaxAmountChanged: (String) -> Unit
) {
    val spacing = WalletTheme.spacing
    val allTypesLabel = stringResource(R.string.all_types_option)
    val allTagsLabel = stringResource(R.string.all_tags_option)
    val allModesLabel = stringResource(R.string.all_payment_modes_option)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = ShapeTwoExtraLarge,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(spacing.default),
            verticalArrangement = Arrangement.spacedBy(spacing.small)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(spacing.small)) {
                DropdownField(
                    label = stringResource(R.string.transaction_type),
                    options = listOf(allTypesLabel) + TransactionType.entries.map { it.toString() },
                    selected = filters.type?.toString() ?: allTypesLabel,
                    onOptionSelected = { label ->
                        onTypeSelected(TransactionType.entries.firstOrNull { it.toString() == label })
                    },
                    modifier = Modifier.weight(1f)
                )
                DropdownField(
                    label = stringResource(R.string.category_label),
                    options = listOf(allTagsLabel) + TagCatalog.ALL_TAGS,
                    selected = filters.tag ?: allTagsLabel,
                    onOptionSelected = { label -> onTagSelected(if (label == allTagsLabel) null else label) },
                    modifier = Modifier.weight(1f)
                )
            }

            DropdownField(
                label = stringResource(R.string.payment_mode),
                options = listOf(allModesLabel) + PaymentMode.entries.map { it.toString() },
                selected = filters.mode?.toString() ?: allModesLabel,
                onOptionSelected = { label ->
                    onModeSelected(PaymentMode.entries.firstOrNull { it.toString() == label })
                }
            )

            Row(horizontalArrangement = Arrangement.spacedBy(spacing.small)) {
                DateField(
                    label = stringResource(R.string.from_date_label),
                    value = filters.fromDate?.let { Extra.convertLongDateToStringDate(it) }.orEmpty(),
                    dateFormatPattern = dateFormatPattern,
                    onDateSelected = { value -> onFromDateChanged(Extra.convertStringDateToLong(value)) },
                    modifier = Modifier.weight(1f)
                )
                DateField(
                    label = stringResource(R.string.to_date_label),
                    value = filters.toDate?.let { Extra.convertLongDateToStringDate(it) }.orEmpty(),
                    dateFormatPattern = dateFormatPattern,
                    onDateSelected = { value -> onToDateChanged(Extra.convertStringDateToLong(value)) },
                    modifier = Modifier.weight(1f)
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(spacing.small)) {
                TextField(
                    value = filters.minAmount,
                    onValueChange = onMinAmountChanged,
                    placeholder = { Text(stringResource(R.string.min_amount_placeholder)) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = formFieldColors(),
                    modifier = Modifier.weight(1f)
                )
                TextField(
                    value = filters.maxAmount,
                    onValueChange = onMaxAmountChanged,
                    placeholder = { Text(stringResource(R.string.max_amount_placeholder)) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = formFieldColors(),
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun ResultsSummaryRow(currencyCode: String, resultCount: Int, totals: FinanceCompute.Totals) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = WalletTheme.extendedColors.elevated, shape = ShapeTwoExtraLarge)
            .padding(horizontal = WalletTheme.spacing.default, vertical = WalletTheme.spacing.small),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.results_count, resultCount),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Row {
            Text(
                text = formatMoney(totals.income, currencyCode),
                style = MaterialTheme.typography.labelSmall,
                color = WalletTheme.extendedColors.income
            )
            Text(
                text = " / ",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = formatMoney(totals.expense, currencyCode),
                style = MaterialTheme.typography.labelSmall,
                color = WalletTheme.extendedColors.expense
            )
        }
    }
}
