package com.babacode.walletexpensetracker.ui.detail

import androidx.lifecycle.*
import com.babacode.walletexpensetracker.data.model.Transaction
import com.babacode.walletexpensetracker.data.model.TransactionType
import com.babacode.walletexpensetracker.repository.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewViewModel @Inject constructor(
    private val repo: TransactionRepository
): ViewModel() {

    private val currentTransactionType = MutableLiveData<TransactionType>()

    val allTransaction = currentTransactionType.switchMap { type ->
        repo.getTransactionByTransactionType(type).asLiveData()
    }

    fun getTransactionByTransactionType(type: TransactionType){
        currentTransactionType.value = type
    }



}