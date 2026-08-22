package com.babacode.walletexpensetracker.ui.home.compose

import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.babacode.walletexpensetracker.R
import com.babacode.walletexpensetracker.data.model.PaymentType
import com.babacode.walletexpensetracker.data.model.Transaction
import com.babacode.walletexpensetracker.data.model.TransactionTag
import com.babacode.walletexpensetracker.data.model.TransactionType
import com.babacode.walletexpensetracker.ui.ADD_TRANSACTION_RESULT_OK
import com.babacode.walletexpensetracker.ui.EDIT_TRANSACTION_RESULT_OK
import com.babacode.walletexpensetracker.ui.compose.TransactionRow
import com.babacode.walletexpensetracker.ui.compose.WalletTopAppBar
import com.babacode.walletexpensetracker.ui.home.HomeViewModel
import com.babacode.walletexpensetracker.ui.theme.WalletExpenseTheme
import com.babacode.walletexpensetracker.utiles.Extra
import java.time.LocalDate
import kotlin.math.cos
import kotlin.math.sin

private val ExpenseColor = Color(0xFFEF2727)
private val IncomeColor = Color(0xFF86DF3B)

@Composable
fun HomeRoute(
    currencyCode: String,
    viewModel: HomeViewModel,
    resultEvent: Int?,
    onResultEventConsumed: () -> Unit,
    onAddClick: () -> Unit,
    onIncomeClick: () -> Unit,
    onExpenseClick: () -> Unit,
    onTransactionClick: (Transaction) -> Unit,
    onLongPress: (Transaction) -> Unit,
    onOpenAnalysisClick: () -> Unit,
    onOpenCalenderClick: () -> Unit,
    onOpenSettingsClick: () -> Unit,
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

    val transactions by viewModel.recentTransaction.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier,
        topBar = {
            WalletTopAppBar(
                title = stringResource(R.string.home_title),
                actions = {
                    IconButton(onClick = onOpenAnalysisClick) {
                        Icon(
                            painter = painterResource(R.drawable.ic_baseline_bar_chart_24),
                            contentDescription = stringResource(R.string.calender)
                        )
                    }
                    IconButton(onClick = onOpenCalenderClick) {
                        Icon(
                            painter = painterResource(R.drawable.yearly_calender),
                            contentDescription = stringResource(R.string.calender)
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
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddClick,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    painter = painterResource(R.drawable.add_transaction_vectore),
                    contentDescription = stringResource(R.string.add_transaction_for_income_or_expense)
                )
            }
        }
    ) { innerPadding ->
        HomeScreen(
            modifier = Modifier.padding(innerPadding),
            currencyCode = currencyCode,
            transactions = transactions,
            onIncomeClick = onIncomeClick,
            onExpenseClick = onExpenseClick,
            onTransactionClick = onTransactionClick,
            onLongPress = onLongPress
        )
    }
}

@Composable
fun HomeScreen(
    currencyCode: String,
    transactions: List<Transaction>?,
    onIncomeClick: () -> Unit,
    onExpenseClick: () -> Unit,
    onTransactionClick: (Transaction) -> Unit,
    onLongPress: (Transaction) -> Unit,
    modifier: Modifier = Modifier
) {
    if (transactions == null) {
        LoadingHomeState(modifier = modifier.fillMaxSize())
        return
    }

    if (transactions.isEmpty()) {
        EmptyHomeState(modifier = modifier.fillMaxSize())
        return
    }

    val monthRange = Extra.getLocalDateStartEndDateMonth(LocalDate.now())
    val monthTransactions = transactions.filter { it.date in monthRange.startDate..monthRange.endDate }
    val (expenseThisMonth, incomeThisMonth) = monthTransactions.partition { it.transactionType == TransactionType.EXPENSE }
    val incomeTotal = incomeThisMonth.sumOf { it.amount }
    val expenseTotal = expenseThisMonth.sumOf { it.amount }

    LazyColumn(modifier = modifier.fillMaxSize()) {
        item {
            Row(modifier = Modifier.fillMaxWidth().padding(4.dp)) {
                SummaryCard(
                    title = stringResource(R.string.income),
                    currencyCode = currencyCode,
                    total = incomeTotal,
                    onClick = onIncomeClick,
                    modifier = Modifier.weight(1f).padding(4.dp)
                )
                SummaryCard(
                    title = stringResource(R.string.expense),
                    currencyCode = currencyCode,
                    total = expenseTotal,
                    onClick = onExpenseClick,
                    modifier = Modifier.weight(1f).padding(4.dp)
                )
            }
        }

        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                ExpenseIncomePieChart(
                    incomeTotal = incomeTotal,
                    expenseTotal = expenseTotal,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp)
                )
            }
        }

        item {
            Text(
                text = stringResource(R.string.recent_transaction),
                modifier = Modifier.padding(start = 8.dp, top = 16.dp),
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

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

@Composable
private fun SummaryCard(
    title: String,
    currencyCode: String,
    total: Double,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.testTag("summary_card_$title"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Row {
                Text(text = title, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                Text(
                    text = " " + stringResource(R.string.thisMonthTxt),
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
            Row(modifier = Modifier.padding(top = 4.dp)) {
                Text(text = currencyCode, fontWeight = FontWeight.Bold)
                Text(text = " $total", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
            }
        }
    }
}

@Composable
    private fun ExpenseIncomePieChart(
    incomeTotal: Double,
    expenseTotal: Double,
    modifier: Modifier = Modifier
) {
    val total = incomeTotal + expenseTotal
    if (total <= 0.0) {
        Row(modifier = modifier, horizontalArrangement = Arrangement.Center) {
            Text(
                text = stringResource(R.string.no_chart_data_available),
                color = Color(0xFFFFA500)
            )
        }
        return
    }

    val expenseFraction = (expenseTotal / total).toFloat()
    val incomeFraction = (incomeTotal / total).toFloat()

    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Canvas(
            modifier = Modifier
                .weight(1f)
                .aspectRatio(1f)
        ) {
            val diameter = minOf(size.width, size.height)
            val topLeft = Offset((size.width - diameter) / 2f, (size.height - diameter) / 2f)
            val arcSize = Size(diameter, diameter)
            val startAngle = -90f
            val expenseSweep = expenseFraction * 360f
            val incomeSweep = incomeFraction * 360f

            drawArc(
                color = ExpenseColor,
                startAngle = startAngle,
                sweepAngle = expenseSweep,
                useCenter = true,
                topLeft = topLeft,
                size = arcSize
            )
            drawArc(
                color = IncomeColor,
                startAngle = startAngle + expenseSweep,
                sweepAngle = incomeSweep,
                useCenter = true,
                topLeft = topLeft,
                size = arcSize
            )

            val center = Offset(topLeft.x + diameter / 2f, topLeft.y + diameter / 2f)
            val labelRadius = diameter / 2f * 0.6f

            drawIntoCanvas { canvas ->
                val paint = android.graphics.Paint().apply {
                    color = android.graphics.Color.BLACK
                    textSize = diameter * 0.07f
                    textAlign = android.graphics.Paint.Align.CENTER
                    isAntiAlias = true
                }

                fun drawPercentLabel(midAngleDeg: Float, fraction: Float) {
                    val rad = Math.toRadians(midAngleDeg.toDouble())
                    val x = center.x + labelRadius * cos(rad).toFloat()
                    val y = center.y + labelRadius * sin(rad).toFloat()
                    canvas.nativeCanvas.drawText("%.1f%%".format(fraction * 100f), x, y, paint)
                }

                drawPercentLabel(startAngle + expenseSweep / 2f, expenseFraction)
                drawPercentLabel(startAngle + expenseSweep + incomeSweep / 2f, incomeFraction)
            }
        }

        Column(modifier = Modifier.padding(start = 8.dp)) {
            PieLegendEntry(color = ExpenseColor, label = stringResource(R.string.expense))
            PieLegendEntry(color = IncomeColor, label = stringResource(R.string.income))
        }
    }
}

@Composable
private fun PieLegendEntry(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 2.dp)) {
        Box(modifier = Modifier.size(12.dp).background(color))
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = label, color = MaterialTheme.colorScheme.onBackground)
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
        modifier = modifier,
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
            fontSize = 24.sp,
            modifier = Modifier.padding(top = 8.dp),
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = stringResource(R.string.add_a_new_transaction_to_get_started),
            modifier = Modifier.padding(top = 8.dp, start = 8.dp, end = 8.dp),
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeScreenPreview() {
    WalletExpenseTheme {
        HomeScreen(
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
                ),
                Transaction(
                    note = "Salary",
                    date = Extra.currentDayDate(),
                    transactionType = TransactionType.INCOME,
                    amount = 50000.0,
                    tag = TransactionTag.SALARY,
                    paymentType = PaymentType.ONLINE,
                    id = 2
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
            transactions = emptyList(),
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
            transactions = null,
            onIncomeClick = {},
            onExpenseClick = {},
            onTransactionClick = {},
            onLongPress = {}
        )
    }
}
