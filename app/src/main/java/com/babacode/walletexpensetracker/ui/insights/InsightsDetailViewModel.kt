package com.babacode.walletexpensetracker.ui.insights

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.babacode.walletexpensetracker.repository.TransactionRepository
import com.babacode.walletexpensetracker.ui.navigation.InsightKind
import com.babacode.walletexpensetracker.utiles.recoverWithDefault
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.stateIn

private data class InsightsDetailParams(val kind: InsightKind, val value: String?, val offset: Int)

@HiltViewModel
class InsightsDetailViewModel @Inject constructor(
    private val repository: TransactionRepository
) : ViewModel() {

    private val params = MutableStateFlow<InsightsDetailParams?>(null)

    val uiState: StateFlow<InsightsDetailUiState> = combine(
        params.filterNotNull(),
        repository.getAllTransaction().recoverWithDefault(emptyList())
    ) { p, transactions -> buildInsightsDetailUiState(p.kind, p.value, p.offset, transactions) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), InsightsDetailUiState.Loading)

    fun setParams(kind: InsightKind, value: String?, offset: Int) {
        params.value = InsightsDetailParams(kind, value, offset)
    }
}
