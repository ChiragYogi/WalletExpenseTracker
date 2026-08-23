package com.babacode.walletexpensetracker.ui.calender.compose

import android.widget.Toast
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.babacode.walletexpensetracker.R
import com.babacode.walletexpensetracker.data.model.PaymentMode
import com.babacode.walletexpensetracker.data.model.Transaction
import com.babacode.walletexpensetracker.data.model.TransactionType
import com.babacode.walletexpensetracker.ui.ADD_TRANSACTION_RESULT_OK
import com.babacode.walletexpensetracker.ui.EDIT_TRANSACTION_RESULT_OK
import com.babacode.walletexpensetracker.ui.calender.CalenderViewViewModel
import com.babacode.walletexpensetracker.ui.compose.CursorHeader
import com.babacode.walletexpensetracker.ui.compose.EmptyState
import com.babacode.walletexpensetracker.ui.compose.TransactionRow
import com.babacode.walletexpensetracker.ui.compose.WalletTopAppBar
import com.babacode.walletexpensetracker.ui.theme.ShapeThreeExtraLarge
import com.babacode.walletexpensetracker.ui.theme.ShapeTwoExtraLarge
import com.babacode.walletexpensetracker.ui.theme.WalletExpenseTheme
import com.babacode.walletexpensetracker.ui.theme.WalletTheme
import com.babacode.walletexpensetracker.utiles.Extra
import com.babacode.walletexpensetracker.utiles.FinanceCompute
import com.babacode.walletexpensetracker.utiles.formatMoney
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

private val monthYearFormatter = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.US)
private val weekdayLabels = listOf("S", "M", "T", "W", "T", "F", "S")

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

    val visibleMonth by viewModel.visibleMonth.collectAsStateWithLifecycle()
    val selectedDate by viewModel.selectedDate.collectAsStateWithLifecycle()
    val spendByDay by viewModel.spendByDay.collectAsStateWithLifecycle()
    val transactions by viewModel.selectedDateTransactions.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier,
        topBar = { WalletTopAppBar(title = stringResource(R.string.calendar_screen_title), onBack = onBack) }
    ) { innerPadding ->
        CalenderScreen(
            modifier = Modifier.padding(innerPadding),
            visibleMonth = visibleMonth,
            selectedDate = selectedDate,
            spendByDay = spendByDay,
            currencyCode = currencyCode,
            transactions = transactions,
            onPreviousMonth = viewModel::onPreviousMonth,
            onNextMonth = viewModel::onNextMonth,
            onDaySelected = viewModel::onDaySelected,
            onTransactionClick = onTransactionClick,
            onLongPress = onLongPress
        )
    }
}

@Composable
fun CalenderScreen(
    visibleMonth: LocalDate,
    selectedDate: LocalDate,
    spendByDay: Map<Long, Double>,
    currencyCode: String,
    transactions: List<Transaction>,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onDaySelected: (LocalDate) -> Unit,
    onTransactionClick: (Transaction) -> Unit,
    onLongPress: (Transaction) -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = WalletTheme.spacing
    val totals = remember(transactions) { FinanceCompute.totals(transactions) }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(spacing.default),
        verticalArrangement = Arrangement.spacedBy(spacing.medium)
    ) {
        item {
            MonthCalendarCard(
                visibleMonth = visibleMonth,
                selectedDate = selectedDate,
                spendByDay = spendByDay,
                onPreviousMonth = onPreviousMonth,
                onNextMonth = onNextMonth,
                onDaySelected = onDaySelected
            )
        }

        item {
            SelectedDaySummaryRow(
                selectedDate = selectedDate,
                currencyCode = currencyCode,
                totals = totals
            )
        }

        if (transactions.isEmpty()) {
            item {
                val sameMonth = selectedDate.year == visibleMonth.year && selectedDate.month == visibleMonth.month
                EmptyState(
                    message = stringResource(
                        if (sameMonth) R.string.no_transactions_this_day else R.string.pick_day_in_month
                    )
                )
            }
        } else {
            items(transactions, key = { it.id }) { transaction ->
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

@Composable
private fun MonthCalendarCard(
    visibleMonth: LocalDate,
    selectedDate: LocalDate,
    spendByDay: Map<Long, Double>,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onDaySelected: (LocalDate) -> Unit
) {
    val spacing = WalletTheme.spacing
    // A month grid is at most 6x7 cells — small enough to lay out directly with
    // Row/Column rather than androidx.compose.foundation.lazy.grid.LazyVerticalGrid,
    // which would nest one scrollable/lazy layout inside the outer LazyColumn above.
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = ShapeThreeExtraLarge,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(spacing.large)) {
            CursorHeader(
                label = visibleMonth.format(monthYearFormatter),
                onPrevious = onPreviousMonth,
                onNext = onNextMonth,
                previousDescription = stringResource(R.string.previous_month),
                nextDescription = stringResource(R.string.next_month)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = spacing.medium)
            ) {
                weekdayLabels.forEach { label ->
                    Text(
                        text = label,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            val firstOfMonth = visibleMonth.withDayOfMonth(1)
            val daysInMonth = visibleMonth.lengthOfMonth()
            // Sunday-start grid, matching the reference exactly (date-fns getDay: Sunday=0).
            val leadingBlanks = firstOfMonth.dayOfWeek.value % 7
            val totalCells = leadingBlanks + daysInMonth
            val rowCount = (totalCells + 6) / 7
            val maxSpend = (spendByDay.values.maxOrNull() ?: 1.0).coerceAtLeast(1.0)

            Column(modifier = Modifier.padding(top = spacing.small)) {
                for (row in 0 until rowCount) {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        for (column in 0 until 7) {
                            val cellIndex = row * 7 + column
                            val dayNumber = cellIndex - leadingBlanks + 1
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f),
                                contentAlignment = Alignment.Center
                            ) {
                                if (dayNumber in 1..daysInMonth) {
                                    val day = firstOfMonth.withDayOfMonth(dayNumber)
                                    val spend = spendByDay[Extra.convertLocalDateToLong(day)] ?: 0.0
                                    DayCell(
                                        day = day,
                                        selected = day == selectedDate,
                                        spend = spend,
                                        maxSpend = maxSpend,
                                        onClick = { onDaySelected(day) }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DayCell(
    day: LocalDate,
    selected: Boolean,
    spend: Double,
    maxSpend: Double,
    onClick: () -> Unit
) {
    val intensity = if (spend > 0) (0.2f + (spend / maxSpend).toFloat() * 0.8f) else 0f
    val dotColor = when {
        spend <= 0 -> Color.Transparent
        selected -> WalletTheme.extendedColors.expense
        else -> WalletTheme.extendedColors.expense.copy(alpha = intensity)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(2.dp)
            .background(
                color = if (selected) MaterialTheme.colorScheme.primary else Color.Transparent,
                shape = CircleShape
            )
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = day.dayOfMonth.toString(),
            style = MaterialTheme.typography.labelMedium,
            color = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
        )
        Box(
            modifier = Modifier
                .padding(top = 2.dp)
                .size(5.dp)
                .background(color = dotColor, shape = CircleShape)
        )
    }
}

@Composable
private fun SelectedDaySummaryRow(
    selectedDate: LocalDate,
    currencyCode: String,
    totals: FinanceCompute.Totals
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = WalletTheme.extendedColors.elevated, shape = ShapeTwoExtraLarge)
            .padding(horizontal = WalletTheme.spacing.default, vertical = WalletTheme.spacing.small),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = Extra.convertLongDateToStringDate(Extra.convertLocalDateToLong(selectedDate)),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
        Row {
            Text(
                text = formatMoney(totals.income, currencyCode),
                style = MaterialTheme.typography.bodyMedium,
                color = WalletTheme.extendedColors.income
            )
            Text(
                text = " / ",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = formatMoney(totals.expense, currencyCode),
                style = MaterialTheme.typography.bodyMedium,
                color = WalletTheme.extendedColors.expense
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CalenderScreenPreview() {
    WalletExpenseTheme {
        val today = LocalDate.now()
        CalenderScreen(
            visibleMonth = today.withDayOfMonth(1),
            selectedDate = today,
            spendByDay = mapOf(Extra.convertLocalDateToLong(today) to 450.0),
            currencyCode = "$",
            transactions = listOf(
                Transaction(
                    note = "Groceries",
                    date = Extra.convertLocalDateToLong(today),
                    transactionType = TransactionType.EXPENSE,
                    amount = 450.0,
                    tag = "Food",
                    paymentType = PaymentMode.CASH,
                    id = 1
                )
            ),
            onPreviousMonth = {},
            onNextMonth = {},
            onDaySelected = {},
            onTransactionClick = {},
            onLongPress = {}
        )
    }
}
