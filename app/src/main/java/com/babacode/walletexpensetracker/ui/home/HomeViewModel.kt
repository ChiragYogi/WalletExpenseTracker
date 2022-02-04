package com.babacode.walletexpensetracker.ui.home

import androidx.lifecycle.*
import com.babacode.walletexpensetracker.data.model.Transaction
import com.babacode.walletexpensetracker.repository.TransactionRepository
import com.babacode.walletexpensetracker.utiles.Extra
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


    fun deleteSingleTransaction(transaction: Transaction) = viewModelScope.launch {
        repository.deleteSingleTransaction(transaction)
    }


}