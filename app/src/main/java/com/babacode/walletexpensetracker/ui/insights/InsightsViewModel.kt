package com.babacode.walletexpensetracker.ui.insights

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.babacode.walletexpensetracker.repository.TransactionRepository
import com.babacode.walletexpensetracker.utiles.recoverWithDefault
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class InsightsViewModel @Inject constructor(
    repository: TransactionRepository
) : ViewModel() {

    val uiState: StateFlow<InsightsUiState> = repository.getAllTransaction()
        .recoverWithDefault(emptyList())
        .map { transactions -> buildInsightsUiState(transactions) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), InsightsUiState.Loading)
}
