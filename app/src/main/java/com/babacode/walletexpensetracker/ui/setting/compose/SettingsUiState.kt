package com.babacode.walletexpensetracker.ui.setting.compose

data class SettingsUiState(
    val themeValue: String,
    val themeDescription: String,
    val currencyValue: String,
    val notificationsEnabled: Boolean
) {
    companion object {
        val Default = SettingsUiState(
            themeValue = "-1",
            themeDescription = "",
            currencyValue = "$",
            notificationsEnabled = true
        )
    }
}
