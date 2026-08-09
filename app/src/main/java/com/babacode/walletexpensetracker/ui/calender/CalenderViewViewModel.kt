package com.babacode.walletexpensetracker.ui.calender

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.babacode.walletexpensetracker.data.model.Transaction
import com.babacode.walletexpensetracker.repository.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn


@HiltViewModel
class CalenderViewViewModel @Inject constructor(
    private val repository: TransactionRepository
) : ViewModel() {

    private val currentDate = MutableStateFlow<Long?>(null)

    val selectedDateTransaction: StateFlow<List<Transaction>> = currentDate
        .filterNotNull()
        .flatMapLatest { date -> repository.getSingleDayTransaction(date) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun getSelectedDateFromCalender(date: Long) {
        currentDate.value = date
    }


}