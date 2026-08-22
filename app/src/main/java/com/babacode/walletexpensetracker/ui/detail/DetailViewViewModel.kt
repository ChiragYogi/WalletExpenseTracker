package com.babacode.walletexpensetracker.ui.detail


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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

@HiltViewModel
class DetailViewViewModel @Inject constructor(
    private val repo: TransactionRepository
) : ViewModel() {

    private val transactionType = MutableStateFlow<TransactionType?>(null)

    private val currentDates: Map<DetailPeriod, MutableStateFlow<LocalDate>> =
        DetailPeriod.entries.associateWith { MutableStateFlow(LocalDate.now()) }

    private val transactions: Map<DetailPeriod, StateFlow<List<Transaction>>> =
        DetailPeriod.entries.associateWith { period ->
            combine(currentDates.getValue(period), transactionType) { date, type -> date to type }
                .flatMapLatest { (date, type) -> queryFlow(period, date, type) }
                .recoverWithDefault(emptyList())
                .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
        }

    private val allTransactions: StateFlow<List<Transaction>> = repo.getAllTransaction()
        .recoverWithDefault(emptyList())
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val trendBuckets: Map<DetailPeriod, StateFlow<List<DetailBucket>>> =
        DetailPeriod.entries.associateWith { period ->
            combine(currentDates.getValue(period), transactionType, allTransactions) { date, type, all ->
                buildTrendBuckets(period, date, all, type ?: TransactionType.EXPENSE)
            }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
        }

    fun setTransactionType(type: TransactionType?) {
        transactionType.value = type
    }

    fun currentDate(period: DetailPeriod): StateFlow<LocalDate> = currentDates.getValue(period)

    fun transactions(period: DetailPeriod): StateFlow<List<Transaction>> = transactions.getValue(period)

    fun trendBuckets(period: DetailPeriod): StateFlow<List<DetailBucket>> = trendBuckets.getValue(period)

    fun onPrevious(period: DetailPeriod) {
        currentDates.getValue(period).update { period.step(it, -1) }
    }

    fun onNext(period: DetailPeriod) {
        currentDates.getValue(period).update { period.step(it, 1) }
    }

    private fun queryFlow(period: DetailPeriod, date: LocalDate, type: TransactionType?): Flow<List<Transaction>> {
        if (period == DetailPeriod.DAILY) {
            val dateLong = Extra.convertLocalDateToLong(date)
            return if (type != null) {
                repo.getSingleDayTransactionByType(type, dateLong)
            } else {
                repo.getSingleDayTransaction(dateLong)
            }
        }

        val range = when (period) {
            DetailPeriod.WEEKLY -> Extra.getLocalDateStartEndDateWeek(date)
            DetailPeriod.MONTHLY -> Extra.getLocalDateStartEndDateMonth(date)
            DetailPeriod.YEARLY -> Extra.getLocalDateStartEndDateYear(date)
            DetailPeriod.DAILY -> error("unreachable")
        }
        return if (type != null) {
            repo.getTransactionByTransactionType(type, range.startDate, range.endDate)
        } else {
            repo.getTransactionForSelectedDate(range.startDate, range.endDate)
        }
    }
}
