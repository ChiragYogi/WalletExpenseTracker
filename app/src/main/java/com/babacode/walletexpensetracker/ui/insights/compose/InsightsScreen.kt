package com.babacode.walletexpensetracker.ui.insights.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.babacode.walletexpensetracker.R
import com.babacode.walletexpensetracker.data.model.PaymentMode
import com.babacode.walletexpensetracker.data.model.Transaction
import com.babacode.walletexpensetracker.data.model.TransactionType
import com.babacode.walletexpensetracker.ui.compose.EmptyState
import com.babacode.walletexpensetracker.ui.compose.ProgressBar
import com.babacode.walletexpensetracker.ui.compose.WalletTopAppBar
import com.babacode.walletexpensetracker.ui.compose.charts.BarChart
import com.babacode.walletexpensetracker.ui.compose.charts.BarChartEntry
import com.babacode.walletexpensetracker.ui.insights.CategoryBreakdown
import com.babacode.walletexpensetracker.ui.insights.InsightsUiState
import com.babacode.walletexpensetracker.ui.insights.InsightsViewModel
import com.babacode.walletexpensetracker.ui.insights.ModeBreakdown
import com.babacode.walletexpensetracker.ui.insights.TrendPoint
import com.babacode.walletexpensetracker.ui.theme.ShapeThreeExtraLarge
import com.babacode.walletexpensetracker.ui.theme.ShapeTwoExtraLarge
import com.babacode.walletexpensetracker.ui.theme.WalletExpenseTheme
import com.babacode.walletexpensetracker.ui.theme.WalletTheme
import com.babacode.walletexpensetracker.utiles.Extra
import com.babacode.walletexpensetracker.utiles.formatMoney
import kotlin.math.abs
import kotlin.math.roundToInt

@Composable
fun InsightsRoute(
    currencyCode: String,
    viewModel: InsightsViewModel,
    onCategoryClick: (String) -> Unit,
    onModeClick: (String) -> Unit,
    onMonthClick: (Int) -> Unit,
    onSeeAllTopSpendsClick: () -> Unit,
    onTransactionClick: (Transaction) -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier,
        topBar = { WalletTopAppBar(title = stringResource(R.string.insights_title)) }
    ) { innerPadding ->
        InsightsScreen(
            modifier = Modifier.padding(innerPadding),
            currencyCode = currencyCode,
            uiState = uiState,
            onCategoryClick = onCategoryClick,
            onModeClick = onModeClick,
            onMonthClick = onMonthClick,
            onSeeAllTopSpendsClick = onSeeAllTopSpendsClick,
            onTransactionClick = onTransactionClick
        )
    }
}

@Composable
fun InsightsScreen(
    currencyCode: String,
    uiState: InsightsUiState,
    onCategoryClick: (String) -> Unit,
    onModeClick: (String) -> Unit,
    onMonthClick: (Int) -> Unit,
    onSeeAllTopSpendsClick: () -> Unit,
    onTransactionClick: (Transaction) -> Unit,
    modifier: Modifier = Modifier
) {
    if (uiState.isLoading) return

    val spacing = WalletTheme.spacing

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(spacing.default),
        verticalArrangement = Arrangement.spacedBy(spacing.extraLarge)
    ) {
        item {
            HeroCard(
                currencyCode = currencyCode,
                uiState = uiState,
                onMonthClick = onMonthClick
            )
        }

        item {
            InsightsSection(title = stringResource(R.string.by_category_title)) {
                if (uiState.categories.isEmpty()) {
                    EmptyState(message = stringResource(R.string.no_expenses_this_month))
                } else {
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
                            uiState.categories.forEach { category ->
                                CategoryRow(
                                    category = category,
                                    currencyCode = currencyCode,
                                    categoryMax = uiState.categoryMax,
                                    currentExpense = uiState.currentExpenseForPercent,
                                    onClick = { onCategoryClick(category.tag) }
                                )
                            }
                        }
                    }
                }
            }
        }

        item {
            InsightsSection(title = stringResource(R.string.by_payment_mode_title)) {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(spacing.small),
                    verticalArrangement = Arrangement.spacedBy(spacing.small)
                ) {
                    uiState.modes.forEach { mode ->
                        ModeChip(
                            mode = mode,
                            modeTotal = uiState.modeTotal,
                            onClick = { onModeClick(mode.mode) }
                        )
                    }
                }
            }
        }

        item {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(stringResource(R.string.top_spends_title), style = MaterialTheme.typography.titleMedium)
                    Text(
                        text = stringResource(R.string.see_all_link),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.clickable(onClick = onSeeAllTopSpendsClick)
                    )
                }
                if (uiState.topSpends.isEmpty()) {
                    EmptyState(
                        message = stringResource(R.string.no_expenses_this_month),
                        modifier = Modifier.padding(top = spacing.medium)
                    )
                } else {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = spacing.medium),
                        shape = ShapeTwoExtraLarge,
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        Column {
                            uiState.topSpends.forEachIndexed { index, transaction ->
                                TopSpendRow(
                                    rank = index + 1,
                                    transaction = transaction,
                                    currencyCode = currencyCode,
                                    onClick = { onTransactionClick(transaction) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun InsightsSection(title: String, content: @Composable () -> Unit) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = WalletTheme.spacing.medium)
        )
        content()
    }
}

@Composable
private fun HeroCard(
    currencyCode: String,
    uiState: InsightsUiState,
    onMonthClick: (Int) -> Unit
) {
    val spacing = WalletTheme.spacing
    val extendedColors = WalletTheme.extendedColors
    val increased = uiState.deltaPercent > 0
    val trendEntries = remember(uiState.trend) {
        uiState.trend.map { BarChartEntry(it.label, it.amount.toFloat()) }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = ShapeThreeExtraLarge,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(spacing.large)) {
            Text(
                text = stringResource(R.string.spent_in_month, uiState.currentMonthLabel),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = formatMoney(uiState.currentExpense, currencyCode),
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(top = spacing.extraSmall)
            )

            Row(
                modifier = Modifier.padding(top = spacing.small),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(
                        if (increased) R.drawable.trending_up_vector else R.drawable.trending_down_vector
                    ),
                    contentDescription = null,
                    tint = if (increased) extendedColors.expense else extendedColors.income,
                    modifier = Modifier.padding(end = spacing.extraSmall)
                )
                Text(
                    text = stringResource(
                        R.string.delta_vs_last_month,
                        abs(uiState.deltaPercent),
                        uiState.previousMonthLabel,
                        formatMoney(uiState.previousExpense, currencyCode)
                    ),
                    style = MaterialTheme.typography.labelMedium,
                    color = if (increased) extendedColors.expense else extendedColors.income
                )
            }

            BarChart(
                entries = trendEntries,
                barColor = { index, _ ->
                    if (index == uiState.trend.lastIndex) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.tertiary
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = spacing.medium)
            )

            Text(
                text = stringResource(R.string.spend_pace_projection, formatMoney(uiState.projectedSpend, currencyCode)),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = spacing.small)
            )

            InsightsLinkRow(
                label = stringResource(R.string.see_every_transaction_in_month, uiState.currentMonthLabel),
                onClick = { onMonthClick(0) },
                modifier = Modifier.padding(top = spacing.medium)
            )
            InsightsLinkRow(
                label = stringResource(R.string.compare_with_month, uiState.previousMonthLabel),
                onClick = { onMonthClick(1) },
                modifier = Modifier.padding(top = spacing.small)
            )
        }
    }
}

@Composable
private fun InsightsLinkRow(label: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(color = MaterialTheme.colorScheme.secondary, shape = ShapeTwoExtraLarge)
            .clickable(onClick = onClick)
            .padding(horizontal = WalletTheme.spacing.default, vertical = WalletTheme.spacing.small),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, style = MaterialTheme.typography.labelMedium)
        Icon(
            painter = painterResource(R.drawable.next_vector),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun CategoryRow(
    category: CategoryBreakdown,
    currencyCode: String,
    categoryMax: Double,
    currentExpense: Double,
    onClick: () -> Unit
) {
    Column(modifier = Modifier.clickable(onClick = onClick)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = category.tag, style = MaterialTheme.typography.labelMedium)
                Icon(
                    painter = painterResource(R.drawable.next_vector),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .padding(start = 2.dp)
                        .size(14.dp)
                )
            }
            Text(
                text = "${formatMoney(category.amount, currencyCode)} · ${(category.amount / currentExpense * 100).roundToInt()}%",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        ProgressBar(
            progress = (category.amount / categoryMax).toFloat(),
            modifier = Modifier.padding(top = WalletTheme.spacing.extraSmall)
        )
    }
}

@Composable
private fun ModeChip(mode: ModeBreakdown, modeTotal: Double, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .background(color = MaterialTheme.colorScheme.surface, shape = ShapeTwoExtraLarge)
            .clickable(onClick = onClick)
            .padding(horizontal = WalletTheme.spacing.medium, vertical = WalletTheme.spacing.small)
    ) {
        Text(
            text = "${mode.mode} · ",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = "${(mode.amount / modeTotal * 100).roundToInt()}%",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold)
        )
    }
}

@Composable
private fun TopSpendRow(rank: Int, transaction: Transaction, currencyCode: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = WalletTheme.spacing.default, vertical = WalletTheme.spacing.medium),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = rank.toString(),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(end = WalletTheme.spacing.small)
        )
        Text(
            text = transaction.note.ifBlank { transaction.tag },
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f),
            maxLines = 1
        )
        Text(
            text = formatMoney(transaction.amount, currencyCode),
            style = MaterialTheme.typography.labelMedium,
            color = WalletTheme.extendedColors.expense
        )
        Icon(
            painter = painterResource(R.drawable.next_vector),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(start = WalletTheme.spacing.small)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun InsightsScreenPreview() {
    WalletExpenseTheme {
        InsightsScreen(
            currencyCode = "$",
            uiState = InsightsUiState(
                isLoading = false,
                currentMonthLabel = "August",
                previousMonthLabel = "July",
                currentExpense = 6500.0,
                previousExpense = 5200.0,
                deltaPercent = 25,
                categories = listOf(
                    CategoryBreakdown("Food", 2500.0),
                    CategoryBreakdown("Rent", 3200.0),
                    CategoryBreakdown("Travel", 800.0)
                ),
                categoryMax = 3200.0,
                currentExpenseForPercent = 6500.0,
                modes = listOf(
                    ModeBreakdown("Cash", 3000.0),
                    ModeBreakdown("UPI", 3500.0)
                ),
                modeTotal = 6500.0,
                topSpends = listOf(
                    Transaction(
                        note = "Groceries",
                        date = Extra.currentDayDate(),
                        transactionType = TransactionType.EXPENSE,
                        amount = 450.0,
                        tag = "Food",
                        paymentType = PaymentMode.CASH,
                        id = 1
                    )
                ),
                trend = listOf(
                    TrendPoint("Mar", 4000.0),
                    TrendPoint("Apr", 4500.0),
                    TrendPoint("May", 3800.0),
                    TrendPoint("Jun", 5000.0),
                    TrendPoint("Jul", 5200.0),
                    TrendPoint("Aug", 6500.0)
                ),
                projectedSpend = 9800.0
            ),
            onCategoryClick = {},
            onModeClick = {},
            onMonthClick = {},
            onSeeAllTopSpendsClick = {},
            onTransactionClick = {}
        )
    }
}
