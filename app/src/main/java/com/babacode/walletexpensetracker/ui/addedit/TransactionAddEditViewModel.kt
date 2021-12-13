package com.babacode.walletexpensetracker.ui.addedit


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.babacode.walletexpensetracker.data.model.Transaction
import com.babacode.walletexpensetracker.repository.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class TransactionAddEditViewModel @Inject constructor(
    private val repository: TransactionRepository

) : ViewModel() {


    fun insertTransaction(transaction: Transaction) = viewModelScope.launch {
        repository.insertNewTransaction(transaction)
    }





}







