package com.babacode.walletexpensetracker.ui.detail


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.babacode.walletexpensetracker.data.model.DailyQueryForTransaction
import com.babacode.walletexpensetracker.data.model.QueryForTransaction
import com.babacode.walletexpensetracker.data.model.Transaction
import com.babacode.walletexpensetracker.repository.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class DetailViewViewModel @Inject constructor(
    private val repo: TransactionRepository
) : ViewModel() {

    private val queryDataForDB = MutableStateFlow<QueryForTransaction?>(null)
    private val queryForToday = MutableStateFlow<DailyQueryForTransaction?>(null)

    // for week,month,year
    val allDataBetweenStartAndEndDate: StateFlow<List<Transaction>> = queryDataForDB
        .filterNotNull()
        .flatMapLatest { query ->
            if (query.transactionType != null) {
                repo.getTransactionByTransactionType(query.transactionType, query.startDate, query.endDate)
            } else {
                repo.getTransactionForSelectedDate(query.startDate, query.endDate)
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    //for daily
    val dailyData: StateFlow<List<Transaction>> = queryForToday
        .filterNotNull()
        .flatMapLatest { todayQuery ->
            if (todayQuery.transactionType != null) {
                repo.getSingleDayTransactionByType(todayQuery.transactionType, todayQuery.dateForToday)
            } else {
                repo.getSingleDayTransaction(todayQuery.dateForToday)
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())


    // for week,month,year
    fun getTheQueryDate(queryData: QueryForTransaction){
        queryDataForDB.value = queryData
           }

    //for daily
    fun getDailyDateForQuery(queryForTransaction: DailyQueryForTransaction){
        queryForToday.value = queryForTransaction
    }




}