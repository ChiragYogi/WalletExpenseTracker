package com.babacode.walletexpensetracker.ui.insights.compose

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.babacode.walletexpensetracker.R
import com.babacode.walletexpensetracker.data.model.Transaction
import com.babacode.walletexpensetracker.ui.compose.EmptyState
import com.babacode.walletexpensetracker.ui.compose.StatCard
import com.babacode.walletexpensetracker.ui.compose.TransactionRow
import com.babacode.walletexpensetracker.ui.compose.WalletTopAppBar
import com.babacode.walletexpensetracker.ui.insights.InsightsDetailSubtitleKind
import com.babacode.walletexpensetracker.ui.insights.InsightsDetailUiState
import com.babacode.walletexpensetracker.ui.insights.InsightsDetailViewModel
import com.babacode.walletexpensetracker.ui.theme.ShapeThreeExtraLarge
import com.babacode.walletexpensetracker.ui.theme.WalletTheme
import com.babacode.walletexpensetracker.utiles.formatMoney

@Composable
fun InsightsDetailRoute(
    currencyCode: String,
    viewModel: InsightsDetailViewModel,
    onBack: () -> Unit,
    onTransactionClick: (Transaction) -> Unit,
    onLongPress: (Transaction) -> Unit,
    onSearchAllClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val title = when (uiState.subtitleKind) {
        InsightsDetailSubtitleKind.TOP_SPENDS -> stringResource(R.string.insights_detail_top_spends_title)
        else -> uiState.title
    }

    Scaffold(
        modifier = modifier,
        topBar = { WalletTopAppBar(title = title, onBack = onBack) }
    ) { innerPadding ->
        InsightsDetailScreen(
            modifier = Modifier.padding(innerPadding),
            currencyCode = currencyCode,
            uiState = uiState,
            onTransactionClick = onTransactionClick,
            onLongPress = onLongPress,
            onSearchAllClick = onSearchAllClick
        )
    }
}

@Composable
fun InsightsDetailScreen(
    currencyCode: String,
    uiState: InsightsDetailUiState,
    onTransactionClick: (Transaction) -> Unit,
    onSearchAllClick: () -> Unit,
    modifier: Modifier = Modifier,
    onLongPress: (Transaction) -> Unit = {}
) {
    if (uiState.isLoading) return

    val spacing = WalletTheme.spacing
    val subtitle = when (uiState.subtitleKind) {
        InsightsDetailSubtitleKind.CATEGORY -> stringResource(R.string.insights_detail_category_subtitle, uiState.monthLabel)
        InsightsDetailSubtitleKind.PAYMENT_MODE -> stringResource(R.string.insights_detail_mode_subtitle, uiState.monthLabel)
        InsightsDetailSubtitleKind.TOP_SPENDS -> uiState.monthLabel
        InsightsDetailSubtitleKind.ALL_TRANSACTIONS -> stringResource(R.string.insights_detail_all_transactions_subtitle)
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(spacing.default),
        verticalArrangement = Arrangement.spacedBy(spacing.medium)
    ) {
        item {
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = ShapeThreeExtraLarge,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(modifier = Modifier.padding(spacing.large)) {
                    Text(
                        text = stringResource(
                            if (uiState.isNetHeadline) R.string.insights_detail_net_for_month else R.string.insights_detail_total_spent
                        ),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = formatMoney(uiState.headlineAmount, currencyCode),
                        style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier.padding(top = spacing.extraSmall)
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = spacing.medium),
                        horizontalArrangement = Arrangement.spacedBy(spacing.small)
                    ) {
                        StatCard(
                            label = stringResource(R.string.transactions_label),
                            value = uiState.transactionCount.toString(),
                            modifier = Modifier.weight(1f)
                        )
                        StatCard(
                            label = stringResource(R.string.average_label),
                            value = formatMoney(uiState.average, currencyCode),
                            modifier = Modifier.weight(1f)
                        )
                        StatCard(
                            label = stringResource(R.string.of_month_label),
                            value = "${uiState.percentOfMonth}%",
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(stringResource(R.string.transactions_label), style = MaterialTheme.typography.titleMedium)
                Text(
                    text = stringResource(R.string.search_all_link),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clickable(onClick = onSearchAllClick)
                )
            }
        }

        if (uiState.transactions.isEmpty()) {
            item { EmptyState(message = stringResource(R.string.insights_detail_no_matches)) }
        } else {
            items(uiState.transactions, key = { it.id }) { transaction ->
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
