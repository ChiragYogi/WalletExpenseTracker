package com.babacode.walletexpensetracker.ui.search

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.babacode.walletexpensetracker.data.model.PaymentMode
import com.babacode.walletexpensetracker.data.model.TransactionType
import com.babacode.walletexpensetracker.repository.TransactionRepository
import com.babacode.walletexpensetracker.utiles.CsvExporter
import com.babacode.walletexpensetracker.utiles.FinanceCompute
import com.babacode.walletexpensetracker.utiles.recoverWithDefault
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext

@HiltViewModel
class SearchViewModel @Inject constructor(
    transactionRepository: TransactionRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _filters = MutableStateFlow(SearchFilters())
    val filters: StateFlow<SearchFilters> = _filters.asStateFlow()

    val uiState: StateFlow<SearchUiState> = combine(
        transactionRepository.getAllTransaction().recoverWithDefault(emptyList()),
        _filters
    ) { transactions, filters ->
        val results = filterTransactions(transactions, filters)
        SearchUiState(results = results, totals = FinanceCompute.totals(results))
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SearchUiState())

    fun onQueryChanged(value: String) = _filters.update { it.copy(query = value) }
    fun onTypeSelected(value: TransactionType?) = _filters.update { it.copy(type = value) }
    fun onTagSelected(value: String?) = _filters.update { it.copy(tag = value) }
    fun onModeSelected(value: PaymentMode?) = _filters.update { it.copy(mode = value) }
    fun onFromDateChanged(value: Long?) = _filters.update { it.copy(fromDate = value) }
    fun onToDateChanged(value: Long?) = _filters.update { it.copy(toDate = value) }
    fun onMinAmountChanged(value: String) = _filters.update { it.copy(minAmount = value.filter(Char::isDigit)) }
    fun onMaxAmountChanged(value: String) = _filters.update { it.copy(maxAmount = value.filter(Char::isDigit)) }

    suspend fun exportCsv(uri: Uri): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            val csv = CsvExporter.buildCsv(uiState.value.results)
            context.contentResolver.openOutputStream(uri)?.use { stream ->
                stream.write(csv.toByteArray())
            } ?: error("Unable to open output stream for $uri")
        }
    }
}
