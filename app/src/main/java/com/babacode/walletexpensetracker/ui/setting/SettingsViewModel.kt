package com.babacode.walletexpensetracker.ui.setting

import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.babacode.walletexpensetracker.repository.SettingsRepository
import com.babacode.walletexpensetracker.ui.setting.compose.SettingsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val themeProvider: ThemeProvider
) : ViewModel() {

    val uiState: StateFlow<SettingsUiState> = combine(
        settingsRepository.theme,
        settingsRepository.currency,
        settingsRepository.notificationsEnabled
    ) { theme, currency, notifications ->
        SettingsUiState(
            themeValue = theme,
            themeDescription = themeProvider.getThemeDescriptionFromPreference(theme),
            currencyValue = currency,
            notificationsEnabled = notifications
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), SettingsUiState.Default)

    fun onThemeSelected(value: String) {
        viewModelScope.launch { settingsRepository.setTheme(value) }
        AppCompatDelegate.setDefaultNightMode(themeProvider.getTheme(value))
    }

    fun onCurrencySelected(value: String) {
        viewModelScope.launch { settingsRepository.setCurrency(value) }
    }

    fun onNotificationToggle(enabled: Boolean) {
        viewModelScope.launch { settingsRepository.setNotificationsEnabled(enabled) }
    }
}
