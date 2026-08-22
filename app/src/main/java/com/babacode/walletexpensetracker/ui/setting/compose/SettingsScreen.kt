package com.babacode.walletexpensetracker.ui.setting.compose

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.babacode.walletexpensetracker.R
import com.babacode.walletexpensetracker.ui.compose.WalletTopAppBar
import com.babacode.walletexpensetracker.ui.setting.SettingsViewModel
import com.babacode.walletexpensetracker.ui.theme.ShapeTwoExtraLarge
import com.babacode.walletexpensetracker.ui.theme.WalletExpenseTheme
import com.babacode.walletexpensetracker.ui.theme.WalletTheme

@Composable
fun SettingsRoute(
    viewModel: SettingsViewModel,
    onBack: () -> Unit,
    onPrivacyPolicyClick: () -> Unit,
    onContactSupportClick: () -> Unit,
    onReportBugClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    Scaffold(
        modifier = modifier,
        topBar = { WalletTopAppBar(title = stringResource(R.string.setting), onBack = onBack) }
    ) { innerPadding ->
        SettingsScreen(
            uiState = uiState,
            onCurrencySelected = viewModel::onCurrencySelected,
            onNotificationToggle = viewModel::onNotificationToggle,
            onPrivacyPolicyClick = onPrivacyPolicyClick,
            onContactSupportClick = onContactSupportClick,
            onReportBugClick = onReportBugClick,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Composable
fun SettingsScreen(
    uiState: SettingsUiState,
    onCurrencySelected: (String) -> Unit,
    onNotificationToggle: (Boolean) -> Unit,
    onPrivacyPolicyClick: () -> Unit,
    onContactSupportClick: () -> Unit,
    onReportBugClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currencyEntries = stringArrayResource(R.array.currency_entries)
    val currencyValues = stringArrayResource(R.array.currency_values)

    val spacing = WalletTheme.spacing

    Surface(modifier = modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        LazyColumn(
            contentPadding = PaddingValues(spacing.default),
            verticalArrangement = Arrangement.spacedBy(spacing.medium)
        ) {
            item { SectionHeader(stringResource(R.string.general)) }
            item {
                SettingsCard {
                    SettingsDropdownCard(
                        icon = painterResource(R.drawable.currency_code_vector),
                        title = stringResource(R.string.currency),
                        options = currencyEntries.toList(),
                        optionValues = currencyValues.toList(),
                        selectedValue = uiState.currencyValue,
                        onOptionSelected = onCurrencySelected
                    )
                }
            }

            item { SectionHeader(stringResource(R.string.notifications)) }
            item {
                SettingsCard {
                    SettingsCheckboxRow(
                        icon = painterResource(R.drawable.notifications_vector),
                        title = stringResource(R.string.notificationTitle),
                        checked = uiState.notificationsEnabled,
                        onCheckedChange = onNotificationToggle
                    )
                }
            }

            item { SectionHeader(stringResource(R.string.sync)) }
            item {
                SettingsCard {
                    SettingsInfoRow(
                        icon = painterResource(R.drawable.cloud_sync_vector),
                        title = stringResource(R.string.claude_sync_title),
                        subtitle = stringResource(R.string.claude_sync_subtitle),
                        badge = stringResource(R.string.soon_badge)
                    )
                }
            }

            item { SectionHeader(stringResource(R.string.links)) }
            item {
                SettingsCard {
                    Column {
                        SettingsListItem(
                            icon = painterResource(R.drawable.bill_vector),
                            title = stringResource(R.string.showPrivacyPolicy),
                            summary = null,
                            onClick = onPrivacyPolicyClick
                        )
                        HorizontalDivider()
                        SettingsListItem(
                            icon = painterResource(R.drawable.ic_baseline_chat_bubble_24),
                            title = stringResource(R.string.contactSupportTitle),
                            summary = stringResource(R.string.contactSummery),
                            onClick = onContactSupportClick
                        )
                        HorizontalDivider()
                        SettingsListItem(
                            icon = painterResource(R.drawable.ic_baseline_bug_report_24),
                            title = stringResource(R.string.raiseBugTitle),
                            summary = stringResource(R.string.raiseBugKeySummery),
                            onClick = onReportBugClick
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingsCard(content: @Composable () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = ShapeTwoExtraLarge,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        content = { content() }
    )
}

@Preview(showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
private fun SettingsScreenPreview() {
    WalletExpenseTheme {
        SettingsScreen(
            uiState = SettingsUiState(
                currencyValue = "$",
                notificationsEnabled = true
            ),
            onCurrencySelected = {},
            onNotificationToggle = {},
            onPrivacyPolicyClick = {},
            onContactSupportClick = {},
            onReportBugClick = {}
        )
    }
}
