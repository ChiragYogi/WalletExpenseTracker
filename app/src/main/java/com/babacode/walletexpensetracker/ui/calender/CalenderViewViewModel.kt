package com.babacode.walletexpensetracker.ui.calender

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.babacode.walletexpensetracker.data.model.Transaction
import com.babacode.walletexpensetracker.data.model.TransactionType
import com.babacode.walletexpensetracker.repository.TransactionRepository
import com.babacode.walletexpensetracker.utiles.Extra
import com.babacode.walletexpensetracker.utiles.recoverWithDefault
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class CalenderViewViewModel @Inject constructor(
    private val repository: TransactionRepository
) : ViewModel() {

    private val _visibleMonth = MutableStateFlow(LocalDate.now().withDayOfMonth(1))
    val visibleMonth: StateFlow<LocalDate> = _visibleMonth.asStateFlow()

    private val _selectedDate = MutableStateFlow(LocalDate.now())
    val selectedDate: StateFlow<LocalDate> = _selectedDate.asStateFlow()

    // Spend-by-day intensity map for the whole calendar heatmap, keyed by the same
    // Long representation used for Transaction.date (Extra.convertLocalDateToLong) —
    // matches the reference design's global (not per-month) intensity scale
    // (refrence/src/routes/calendar.tsx).
    val spendByDay: StateFlow<Map<Long, Double>> = repository.getAllTransaction()
        .recoverWithDefault(emptyList())
        .map { transactions ->
            transactions
                .filter { it.transactionType == TransactionType.EXPENSE }
                .groupBy { it.date }
                .mapValues { (_, txs) -> txs.sumOf { it.amount } }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    val selectedDateTransactions: StateFlow<List<Transaction>> = _selectedDate
        .map { Extra.convertLocalDateToLong(it) }
        .flatMapLatest { date -> repository.getSingleDayTransaction(date) }
        .recoverWithDefault(emptyList())
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun onPreviousMonth() {
        _visibleMonth.update { it.minusMonths(1) }
    }

    fun onNextMonth() {
        _visibleMonth.update { it.plusMonths(1) }
    }

    fun onDaySelected(date: LocalDate) {
        _selectedDate.value = date
    }
}
