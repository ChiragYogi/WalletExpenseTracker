package com.babacode.walletexpensetracker.ui.setting

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
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    val uiState: StateFlow<SettingsUiState> = combine(
        settingsRepository.currency,
        settingsRepository.notificationsEnabled
    ) { currency, notifications ->
        SettingsUiState(
            currencyValue = currency,
            notificationsEnabled = notifications
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), SettingsUiState.Default)

    fun onCurrencySelected(value: String) {
        viewModelScope.launch { settingsRepository.setCurrency(value) }
    }

    fun onNotificationToggle(enabled: Boolean) {
        viewModelScope.launch { settingsRepository.setNotificationsEnabled(enabled) }
    }
}
