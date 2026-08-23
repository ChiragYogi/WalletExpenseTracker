package com.babacode.walletexpensetracker.ui.home.compose

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.babacode.walletexpensetracker.R
import com.babacode.walletexpensetracker.data.model.PaymentMode
import com.babacode.walletexpensetracker.data.model.Transaction
import com.babacode.walletexpensetracker.data.model.TransactionType
import com.babacode.walletexpensetracker.ui.ADD_TRANSACTION_RESULT_OK
import com.babacode.walletexpensetracker.ui.EDIT_TRANSACTION_RESULT_OK
import com.babacode.walletexpensetracker.ui.compose.EmptyState
import com.babacode.walletexpensetracker.ui.compose.ThresholdProgressBar
import com.babacode.walletexpensetracker.ui.compose.rememberTransactionDateGroups
import com.babacode.walletexpensetracker.ui.compose.transactionDateGroups
import com.babacode.walletexpensetracker.ui.compose.WalletTopAppBar
import com.babacode.walletexpensetracker.ui.compose.charts.DonutChart
import com.babacode.walletexpensetracker.ui.compose.charts.DonutSlice
import com.babacode.walletexpensetracker.ui.home.BudgetProgress
import com.babacode.walletexpensetracker.ui.home.HomeUiState
import com.babacode.walletexpensetracker.ui.home.HomeViewModel
import com.babacode.walletexpensetracker.ui.theme.ShapeThreeExtraLarge
import com.babacode.walletexpensetracker.ui.theme.ShapeTwoExtraLarge
import com.babacode.walletexpensetracker.ui.theme.WalletExpenseTheme
import com.babacode.walletexpensetracker.ui.theme.WalletTheme
import com.babacode.walletexpensetracker.utiles.Extra
import com.babacode.walletexpensetracker.utiles.formatMoney
import kotlin.math.roundToInt

@Composable
fun HomeRoute(
    currencyCode: String,
    viewModel: HomeViewModel,
    resultEvent: Int?,
    onResultEventConsumed: () -> Unit,
    onIncomeClick: () -> Unit,
    onExpenseClick: () -> Unit,
    onTransactionClick: (Transaction) -> Unit,
    onLongPress: (Transaction) -> Unit,
    onOpenCalenderClick: () -> Unit,
    onOpenSettingsClick: () -> Unit,
    onOpenSearchClick: () -> Unit,
    onOpenRecurringClick: () -> Unit,
    onManageBudgetsClick: () -> Unit,
    onSeeAllTransactionsClick: () -> Unit,
    showNotificationPermissionSnackbar: Boolean,
    onNotificationPermissionSnackbarShown: () -> Unit,
    onOpenNotificationSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    LaunchedEffect(resultEvent) {
        val messageRes = when (resultEvent) {
            ADD_TRANSACTION_RESULT_OK -> R.string.transaction_added
            EDIT_TRANSACTION_RESULT_OK -> R.string.transaction_update
            else -> null
        }
        if (messageRes != null) {
            Toast.makeText(context, messageRes, Toast.LENGTH_LONG).show()
            onResultEventConsumed()
        }
    }

    val notificationPermissionMessage = stringResource(R.string.notification_permission_text)
    val notificationPermissionActionLabel = stringResource(R.string.open)
    LaunchedEffect(showNotificationPermissionSnackbar) {
        if (showNotificationPermissionSnackbar) {
            val result = snackbarHostState.showSnackbar(
                message = notificationPermissionMessage,
                actionLabel = notificationPermissionActionLabel,
                duration = SnackbarDuration.Long
            )
            if (result == SnackbarResult.ActionPerformed) {
                onOpenNotificationSettings()
            }
            onNotificationPermissionSnackbarShown()
        }
    }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier,
        topBar = {
            WalletTopAppBar(
                title = stringResource(R.string.home_title),
                actions = {
                    IconButton(onClick = onOpenSearchClick) {
                        Icon(
                            painter = painterResource(R.drawable.search_vector),
                            contentDescription = stringResource(R.string.search_icon_description)
                        )
                    }
                    IconButton(onClick = onOpenRecurringClick) {
                        Icon(
                            painter = painterResource(R.drawable.repeat_vector),
                            contentDescription = stringResource(R.string.recurring_icon_description)
                        )
                    }
                    IconButton(onClick = onOpenSettingsClick) {
                        Icon(
                            painter = painterResource(R.drawable.setting_vector),
                            contentDescription = stringResource(R.string.setting_icon)
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        HomeScreen(
            modifier = Modifier.padding(innerPadding),
            currencyCode = currencyCode,
            uiState = uiState,
            onIncomeClick = onIncomeClick,
            onExpenseClick = onExpenseClick,
            onTransactionClick = onTransactionClick,
            onLongPress = onLongPress,
            onOpenCalenderClick = onOpenCalenderClick,
            onManageBudgetsClick = onManageBudgetsClick,
            onSeeAllTransactionsClick = onSeeAllTransactionsClick
        )
    }
}

@Composable
fun HomeScreen(
    currencyCode: String,
    uiState: HomeUiState,
    onIncomeClick: () -> Unit,
    onExpenseClick: () -> Unit,
    onTransactionClick: (Transaction) -> Unit,
    onLongPress: (Transaction) -> Unit,
    modifier: Modifier = Modifier,
    onOpenCalenderClick: () -> Unit = {},
    onManageBudgetsClick: () -> Unit = {},
    onSeeAllTransactionsClick: () -> Unit = {}
) {
    if (uiState.isLoading) {
        LoadingHomeState(modifier = modifier.fillMaxSize())
        return
    }

    if (!uiState.hasAnyTransactions) {
        EmptyHomeState(modifier = modifier.fillMaxSize())
        return
    }

    val spacing = WalletTheme.spacing
    val recentTransactionGroups = rememberTransactionDateGroups(uiState.recentTransactions)

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(spacing.default),
        verticalArrangement = Arrangement.spacedBy(spacing.medium)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(spacing.medium)
            ) {
                SummaryCard(
                    testTagKey = stringResource(R.string.income),
                    label = stringResource(R.string.income_this_month),
                    currencyCode = currencyCode,
                    amount = uiState.monthIncome,
                    amountColor = WalletTheme.extendedColors.income,
                    onClick = onIncomeClick,
                    modifier = Modifier.weight(1f)
                )
                SummaryCard(
                    testTagKey = stringResource(R.string.expense),
                    label = stringResource(R.string.expense_this_month),
                    currencyCode = currencyCode,
                    amount = uiState.monthExpense,
                    amountColor = WalletTheme.extendedColors.expense,
                    onClick = onExpenseClick,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            NetBalanceBanner(
                currencyCode = currencyCode,
                net = uiState.monthIncome - uiState.monthExpense
            )
        }

        item {
            SpendingDonutCard(
                currencyCode = currencyCode,
                income = uiState.monthIncome,
                expense = uiState.monthExpense
            )
        }

        item {
            CalendarLinkRow(onClick = onOpenCalenderClick)
        }

        if (uiState.topBudgets.isNotEmpty()) {
            item {
                BudgetsSection(
                    currencyCode = currencyCode,
                    topBudgets = uiState.topBudgets,
                    onManageClick = onManageBudgetsClick
                )
            }
        }

        item {
            SectionHeaderRow(
                title = stringResource(R.string.recent_transactions_title),
                actionLabel = stringResource(R.string.see_all_link),
                onActionClick = onSeeAllTransactionsClick
            )
        }

        if (uiState.recentTransactions.isEmpty()) {
            item {
                EmptyState(message = stringResource(R.string.no_recent_transactions))
            }
        } else {
            transactionDateGroups(
                groups = recentTransactionGroups,
                currencyCode = currencyCode,
                onClick = onTransactionClick,
                onLongPress = onLongPress
            )
        }
    }
}

@Composable
private fun SummaryCard(
    testTagKey: String,
    label: String,
    currencyCode: String,
    amount: Double,
    amountColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.testTag("summary_card_$testTagKey"),
        shape = ShapeTwoExtraLarge,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(WalletTheme.spacing.default)) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = formatMoney(amount, currencyCode),
                style = MaterialTheme.typography.titleLarge,
                color = amountColor,
                modifier = Modifier.padding(top = WalletTheme.spacing.extraSmall)
            )
        }
    }
}

@Composable
private fun NetBalanceBanner(currencyCode: String, net: Double) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                shape = ShapeTwoExtraLarge
            )
            .padding(horizontal = WalletTheme.spacing.default, vertical = WalletTheme.spacing.medium),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = stringResource(R.string.net_balance),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = formatMoney(net, currencyCode),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun SpendingDonutCard(currencyCode: String, income: Double, expense: Double) {
    val extendedColors = WalletTheme.extendedColors
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = ShapeThreeExtraLarge,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(WalletTheme.spacing.large).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            DonutChart(
                slices = listOf(
                    DonutSlice(value = expense.toFloat(), color = extendedColors.expense),
                    DonutSlice(value = income.toFloat(), color = extendedColors.income)
                ),
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .aspectRatio(1f)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(R.string.spent_label),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = formatMoney(expense, currencyCode),
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = if (income > 0) {
                            stringResource(R.string.percent_of_income, (expense / income * 100).roundToInt())
                        } else {
                            stringResource(R.string.no_income_yet)
                        },
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Row(
                modifier = Modifier.padding(top = WalletTheme.spacing.medium),
                horizontalArrangement = Arrangement.spacedBy(WalletTheme.spacing.large)
            ) {
                DonutLegendEntry(color = extendedColors.income, label = stringResource(R.string.income))
                DonutLegendEntry(color = extendedColors.expense, label = stringResource(R.string.expense))
            }
        }
    }
}

@Composable
private fun DonutLegendEntry(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .background(color = color, shape = CircleShape)
        )
        Text(
            text = label,
            modifier = Modifier.padding(start = WalletTheme.spacing.extraSmall),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun CalendarLinkRow(onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = MaterialTheme.colorScheme.surface, shape = ShapeTwoExtraLarge)
            .clickable(onClick = onClick)
            .padding(WalletTheme.spacing.default),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painter = painterResource(R.drawable.weekly_calender_vector),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(18.dp)
            )
            Text(
                text = stringResource(R.string.view_spending_calendar),
                modifier = Modifier.padding(start = WalletTheme.spacing.small),
                style = MaterialTheme.typography.bodyMedium
            )
        }
        Text(
            text = stringResource(R.string.see_by_day),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun BudgetsSection(
    currencyCode: String,
    topBudgets: List<BudgetProgress>,
    onManageClick: () -> Unit
) {
    Column {
        SectionHeaderRow(
            title = stringResource(R.string.budgets_section_title),
            actionLabel = stringResource(R.string.budgets_manage_link),
            onActionClick = onManageClick
        )
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = ShapeTwoExtraLarge,
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(
                modifier = Modifier.padding(WalletTheme.spacing.default),
                verticalArrangement = Arrangement.spacedBy(WalletTheme.spacing.medium)
            ) {
                topBudgets.forEach { budget -> BudgetProgressRow(currencyCode = currencyCode, budget = budget) }
            }
        }
    }
}

@Composable
private fun BudgetProgressRow(currencyCode: String, budget: BudgetProgress) {
    val over = budget.spent > budget.limitAmount
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = budget.tag, style = MaterialTheme.typography.labelMedium)
            Text(
                text = "${formatMoney(budget.spent, currencyCode)} / ${formatMoney(budget.limitAmount, currencyCode)}",
                style = MaterialTheme.typography.labelSmall,
                color = if (over) WalletTheme.extendedColors.expense else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        ThresholdProgressBar(
            progress = if (budget.limitAmount > 0) (budget.spent / budget.limitAmount).toFloat() else 0f,
            modifier = Modifier.padding(top = WalletTheme.spacing.extraSmall)
        )
    }
}

@Composable
private fun SectionHeaderRow(title: String, actionLabel: String, onActionClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = title, style = MaterialTheme.typography.titleMedium)
        Text(
            text = actionLabel,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.clickable(onClick = onActionClick)
        )
    }
}

@Composable
private fun LoadingHomeState(modifier: Modifier = Modifier) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
private fun EmptyHomeState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(WalletTheme.spacing.default),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(R.drawable.noentryimage),
            contentDescription = stringResource(R.string.image_for_no_transaction),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp)
        )
        Text(
            text = stringResource(R.string.no_transaction_yet),
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(top = WalletTheme.spacing.small)
        )
        Text(
            text = stringResource(R.string.add_a_new_transaction_to_get_started),
            modifier = Modifier.padding(top = WalletTheme.spacing.small),
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeScreenPreview() {
    WalletExpenseTheme {
        HomeScreen(
            currencyCode = "$",
            uiState = HomeUiState(
                hasAnyTransactions = true,
                monthIncome = 50000.0,
                monthExpense = 6500.0,
                recentTransactions = listOf(
                    Transaction(
                        note = "Groceries",
                        date = Extra.currentDayDate(),
                        transactionType = TransactionType.EXPENSE,
                        amount = 450.0,
                        tag = "Food",
                        paymentType = PaymentMode.CASH,
                        id = 1
                    ),
                    Transaction(
                        note = "Salary",
                        date = Extra.currentDayDate(),
                        transactionType = TransactionType.INCOME,
                        amount = 50000.0,
                        tag = "Salary",
                        paymentType = PaymentMode.ONLINE_BANKING,
                        id = 2
                    )
                ),
                topBudgets = listOf(
                    BudgetProgress(tag = "Food", spent = 450.0, limitAmount = 3000.0),
                    BudgetProgress(tag = "Rent", spent = 3200.0, limitAmount = 3000.0)
                )
            ),
            onIncomeClick = {},
            onExpenseClick = {},
            onTransactionClick = {},
            onLongPress = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeScreenEmptyPreview() {
    WalletExpenseTheme {
        HomeScreen(
            currencyCode = "$",
            uiState = HomeUiState(hasAnyTransactions = false),
            onIncomeClick = {},
            onExpenseClick = {},
            onTransactionClick = {},
            onLongPress = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeScreenLoadingPreview() {
    WalletExpenseTheme {
        HomeScreen(
            currencyCode = "$",
            uiState = HomeUiState.Loading,
            onIncomeClick = {},
            onExpenseClick = {},
            onTransactionClick = {},
            onLongPress = {}
        )
    }
}
