package com.babacode.walletexpensetracker.ui.setting.compose

data class SettingsUiState(
    val currencyValue: String,
    val notificationsEnabled: Boolean
) {
    companion object {
        val Default = SettingsUiState(
            currencyValue = "$",
            notificationsEnabled = true
        )
    }
}
