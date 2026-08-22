package com.babacode.walletexpensetracker.ui.detail

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.babacode.walletexpensetracker.R
import com.babacode.walletexpensetracker.data.model.Transaction
import com.babacode.walletexpensetracker.data.model.TransactionType
import com.babacode.walletexpensetracker.ui.ADD_TRANSACTION_RESULT_OK
import com.babacode.walletexpensetracker.ui.EDIT_TRANSACTION_RESULT_OK
import com.babacode.walletexpensetracker.ui.compose.WalletTopAppBar
import kotlinx.coroutines.launch

@Composable
fun TransactionTypeRoute(
    transactionType: TransactionType?,
    currencyCode: String,
    viewModel: DetailViewViewModel,
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

    Scaffold(
        modifier = modifier,
        topBar = { WalletTopAppBar(title = stringResource(R.string.detail_view_title), onBack = onBack) }
    ) { innerPadding ->
        TransactionTypeScreen(
            modifier = Modifier.padding(innerPadding),
            currencyCode = currencyCode,
            viewModel = viewModel,
            onTransactionClick = onTransactionClick,
            onLongPress = onLongPress
        )
    }
}

@Composable
fun TransactionTypeScreen(
    currencyCode: String,
    viewModel: DetailViewViewModel,
    onTransactionClick: (Transaction) -> Unit,
    onLongPress: (Transaction) -> Unit,
    modifier: Modifier = Modifier
) {
    val periods = DetailPeriod.entries
    val pagerState = rememberPagerState(pageCount = { periods.size })
    val coroutineScope = rememberCoroutineScope()

    Column(modifier = modifier) {
        PeriodPillTabs(
            labels = periods.map { it.tabLabel },
            selectedIndex = pagerState.currentPage,
            onSelected = { index ->
                coroutineScope.launch { pagerState.animateScrollToPage(index) }
            },
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
        )

        HorizontalPager(state = pagerState) { page ->
            val period = periods[page]
            val currentDate by viewModel.currentDate(period).collectAsStateWithLifecycle()
            val transactions by viewModel.transactions(period).collectAsStateWithLifecycle()
            val trendBuckets by viewModel.trendBuckets(period).collectAsStateWithLifecycle()

            PeriodDetailScreen(
                period = period,
                dateLabel = period.dateLabel(currentDate),
                currencyCode = currencyCode,
                transactions = transactions,
                trendBuckets = trendBuckets,
                onPrevious = { viewModel.onPrevious(period) },
                onNext = { viewModel.onNext(period) },
                onTransactionClick = onTransactionClick,
                onLongPress = onLongPress
            )
        }
    }
}
