package com.babacode.walletexpensetracker.ui.home

import androidx.lifecycle.*
import com.babacode.walletexpensetracker.data.model.Month
import com.babacode.walletexpensetracker.data.model.Transaction
import com.babacode.walletexpensetracker.repository.TransactionRepository
import com.babacode.walletexpensetracker.utiles.Extra
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: TransactionRepository
) : ViewModel() {

    private val _recentTransaction = repository.getAllTransaction().asLiveData()
    val recentTransaction: LiveData<List<Transaction>> get() = _recentTransaction

    private val transactionEventChannel = Channel<TransactionEvent>()
    val transactionEvent = transactionEventChannel.receiveAsFlow()

    private val transactionEditEventChannel = Channel<TransactionEditEvent>()
    val transactionEditEvent = transactionEditEventChannel.receiveAsFlow()

    private val monthDate = Extra.getStartDateAndEndDate()
     val startDate = Extra.convertDateToLong(monthDate.startDate)
     val endDate = Extra.convertDateToLong(monthDate.endDate)

    val thisMonthTransaction = repository.getCurrentMonthTransaction(startDate,endDate).asLiveData()

    fun onAddNewTransactionClick() = viewModelScope.launch {
        transactionEventChannel.send(TransactionEvent.NavigateToAddTransactionScreen)
    }

    fun onTransactionSelected(transaction: Transaction) = viewModelScope.launch {
        transactionEventChannel.send(TransactionEvent.NavigateToDetailScreen(transaction))
    }

    fun onEditTransactionClick(transaction: Transaction?) = viewModelScope.launch {
        transactionEditEventChannel.send(TransactionEditEvent.NavigateToEditTransaction(transaction))
    }



    sealed class TransactionEvent {
        object NavigateToAddTransactionScreen : TransactionEvent()
        data class NavigateToDetailScreen(val transaction: Transaction) : TransactionEvent()
        data class ShowTransactionSavedConfirmationMessage(val message: String) : TransactionEvent()
    }


    sealed class TransactionEditEvent {
        data class NavigateToEditTransaction(val transaction: Transaction?) : TransactionEditEvent()
    }


}