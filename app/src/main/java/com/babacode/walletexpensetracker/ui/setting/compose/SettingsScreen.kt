package com.babacode.walletexpensetracker.ui.setting.compose

import android.content.res.Configuration
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.babacode.walletexpensetracker.R
import com.babacode.walletexpensetracker.ui.setting.SettingsViewModel
import com.babacode.walletexpensetracker.ui.theme.WalletExpenseTheme

@Composable
fun SettingsRoute(
    viewModel: SettingsViewModel,
    onPrivacyPolicyClick: () -> Unit,
    onContactSupportClick: () -> Unit,
    onReportBugClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    SettingsScreen(
        uiState = uiState,
        onThemeSelected = viewModel::onThemeSelected,
        onCurrencySelected = viewModel::onCurrencySelected,
        onNotificationToggle = viewModel::onNotificationToggle,
        onPrivacyPolicyClick = onPrivacyPolicyClick,
        onContactSupportClick = onContactSupportClick,
        onReportBugClick = onReportBugClick,
        modifier = modifier
    )
}

@Composable
fun SettingsScreen(
    uiState: SettingsUiState,
    onThemeSelected: (String) -> Unit,
    onCurrencySelected: (String) -> Unit,
    onNotificationToggle: (Boolean) -> Unit,
    onPrivacyPolicyClick: () -> Unit,
    onContactSupportClick: () -> Unit,
    onReportBugClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val themeEntries = stringArrayResource(R.array.themes_entries)
    val themeValues = stringArrayResource(R.array.themes_values)
    val currencyEntries = stringArrayResource(R.array.currency_entries)
    val currencyValues = stringArrayResource(R.array.currency_values)

    var showThemeDialog by rememberSaveable { mutableStateOf(false) }
    var showCurrencyDialog by rememberSaveable { mutableStateOf(false) }

    val currencySummary = remember(uiState.currencyValue, currencyEntries, currencyValues) {
        val index = currencyValues.indexOf(uiState.currencyValue)
        if (index >= 0) currencyEntries[index] else uiState.currencyValue
    }

    Surface(modifier = modifier.fillMaxSize()) {
        LazyColumn {
            item { SectionHeader(stringResource(R.string.general)) }
            item {
                SettingsListItem(
                    icon = painterResource(R.drawable.theme_vector),
                    title = stringResource(R.string.theme),
                    summary = uiState.themeDescription,
                    onClick = { showThemeDialog = true }
                )
            }
            item {
                SettingsListItem(
                    icon = painterResource(R.drawable.currency_code_vector),
                    title = stringResource(R.string.currency),
                    summary = currencySummary,
                    onClick = { showCurrencyDialog = true }
                )
            }
            item { HorizontalDivider() }
            item { SectionHeader(stringResource(R.string.notifications)) }
            item {
                SettingsSwitchRow(
                    icon = painterResource(R.drawable.notifications_vector),
                    title = stringResource(R.string.notificationTitle),
                    checked = uiState.notificationsEnabled,
                    onCheckedChange = onNotificationToggle
                )
            }
            item { HorizontalDivider() }
            item { SectionHeader(stringResource(R.string.links)) }
            item {
                SettingsListItem(
                    icon = painterResource(R.drawable.bill_vector),
                    title = stringResource(R.string.showPrivacyPolicy),
                    summary = null,
                    onClick = onPrivacyPolicyClick
                )
            }
            item {
                SettingsListItem(
                    icon = painterResource(R.drawable.ic_baseline_chat_bubble_24),
                    title = stringResource(R.string.contactSupportTitle),
                    summary = stringResource(R.string.contactSummery),
                    onClick = onContactSupportClick
                )
            }
            item {
                SettingsListItem(
                    icon = painterResource(R.drawable.ic_baseline_bug_report_24),
                    title = stringResource(R.string.raiseBugTitle),
                    summary = stringResource(R.string.raiseBugKeySummery),
                    onClick = onReportBugClick
                )
            }
        }
    }

    if (showThemeDialog) {
        RadioListDialog(
            title = stringResource(R.string.theme),
            options = themeEntries.toList(),
            optionValues = themeValues.toList(),
            selectedValue = uiState.themeValue,
            onOptionSelected = onThemeSelected,
            onDismissRequest = { showThemeDialog = false }
        )
    }

    if (showCurrencyDialog) {
        RadioListDialog(
            title = stringResource(R.string.currency),
            options = currencyEntries.toList(),
            optionValues = currencyValues.toList(),
            selectedValue = uiState.currencyValue,
            onOptionSelected = onCurrencySelected,
            onDismissRequest = { showCurrencyDialog = false }
        )
    }
}

@Preview(showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
private fun SettingsScreenPreview() {
    WalletExpenseTheme {
        SettingsScreen(
            uiState = SettingsUiState(
                themeValue = "-1",
                themeDescription = "System default",
                currencyValue = "$",
                notificationsEnabled = true
            ),
            onThemeSelected = {},
            onCurrencySelected = {},
            onNotificationToggle = {},
            onPrivacyPolicyClick = {},
            onContactSupportClick = {},
            onReportBugClick = {}
        )
    }
}
