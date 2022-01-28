package com.babacode.walletexpensetracker.ui.home

import androidx.lifecycle.*
import com.babacode.walletexpensetracker.data.model.Transaction
import com.babacode.walletexpensetracker.repository.TransactionRepository
import com.babacode.walletexpensetracker.utiles.Extra.getLocalDateStartEndDateMonth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: TransactionRepository
) : ViewModel() {

    private val _recentTransaction = repository.getAllTransaction().asLiveData()
    val recentTransaction: LiveData<List<Transaction>> get() = _recentTransaction

    private val currentDate = LocalDate.now()
    private val getStartAndEndDate = getLocalDateStartEndDateMonth(currentDate)


    private val _thisMonthTransaction = repository.getTransactionForSelectedDate(
        getStartAndEndDate.startDate, getStartAndEndDate.endDate
    ).asLiveData()
    val currentMonthTransaction: LiveData<List<Transaction>> get() = _thisMonthTransaction



    init {
        getLocalDateStartEndDateMonth(currentDate)
      }


    fun deleteSingleTransaction(transaction: Transaction) = viewModelScope.launch {
        repository.deleteSingleTransaction(transaction)
    }


    fun onAllTransactionDeleted() = viewModelScope.launch {
        repository.deleteAllTheTransaction()
    }




}