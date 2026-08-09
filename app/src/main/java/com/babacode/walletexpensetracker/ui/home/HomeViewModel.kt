package com.babacode.walletexpensetracker.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.babacode.walletexpensetracker.data.model.Transaction
import com.babacode.walletexpensetracker.repository.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: TransactionRepository
) : ViewModel() {

    val recentTransaction: StateFlow<List<Transaction>> = repository.getAllTransaction()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())


    fun deleteSingleTransaction(transaction: Transaction) = viewModelScope.launch {
        repository.deleteSingleTransaction(transaction)
    }


}