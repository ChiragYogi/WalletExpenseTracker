package com.babacode.walletexpensetracker.ui.detail.compose

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.babacode.walletexpensetracker.R
import com.babacode.walletexpensetracker.data.model.Transaction
import com.babacode.walletexpensetracker.data.model.TransactionType
import com.babacode.walletexpensetracker.ui.ADD_TRANSACTION_RESULT_OK
import com.babacode.walletexpensetracker.ui.EDIT_TRANSACTION_RESULT_OK
import com.babacode.walletexpensetracker.ui.detail.DetailPeriod
import com.babacode.walletexpensetracker.ui.detail.DetailViewViewModel
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
    modifier: Modifier = Modifier
) {
    val snackbarHostState = remember { SnackbarHostState() }

    val resultMessage = when (resultEvent) {
        ADD_TRANSACTION_RESULT_OK -> stringResource(R.string.transaction_added)
        EDIT_TRANSACTION_RESULT_OK -> stringResource(R.string.transaction_update)
        else -> null
    }

    LaunchedEffect(resultEvent) {
        if (resultMessage != null) {
            snackbarHostState.showSnackbar(resultMessage)
            onResultEventConsumed()
        }
    }

    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        TransactionTypeScreen(
            modifier = Modifier.padding(innerPadding),
            transactionType = transactionType,
            currencyCode = currencyCode,
            viewModel = viewModel,
            onTransactionClick = onTransactionClick,
            onLongPress = onLongPress
        )
    }
}

@Composable
fun TransactionTypeScreen(
    transactionType: TransactionType?,
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
        TabRow(selectedTabIndex = pagerState.currentPage) {
            periods.forEachIndexed { index, period ->
                Tab(
                    selected = pagerState.currentPage == index,
                    onClick = {
                        coroutineScope.launch { pagerState.animateScrollToPage(index) }
                    },
                    text = { Text(period.tabLabel) }
                )
            }
        }

        HorizontalPager(state = pagerState) { page ->
            val period = periods[page]
            PeriodDetailRoute(
                period = period,
                transactionType = transactionType,
                currencyCode = currencyCode,
                viewModel = viewModel,
                onTransactionClick = onTransactionClick,
                onLongPress = onLongPress
            )
        }
    }
}
