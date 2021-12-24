package com.babacode.walletexpensetracker.ui.home

import androidx.lifecycle.*
import com.babacode.walletexpensetracker.data.model.Transaction
import com.babacode.walletexpensetracker.repository.TransactionRepository
import com.babacode.walletexpensetracker.ui.ADD_TRANSACTION_RESULT_OK
import com.babacode.walletexpensetracker.ui.EDIT_TRANSACTION_RESULT_OK
import com.babacode.walletexpensetracker.utiles.Extra.getStartDateAndEndDate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
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

    private val getStartAndEndDate = getStartDateAndEndDate()

    init {
        getStartDateAndEndDate()
    }


    private val _thisMonthTransaction = repository.getCurrentMonthTransaction(
        getStartAndEndDate.startDate, getStartAndEndDate.endDate
    ).asLiveData()
    val currentMonthTransaction: LiveData<List<Transaction>> get() = _thisMonthTransaction




    fun onAddNewTransactionClick() = viewModelScope.launch {
        transactionEventChannel.send(TransactionEvent.NavigateToAddTransactionScreen)
    }

    fun onTransactionSelected(transaction: Transaction) = viewModelScope.launch {
        transactionEventChannel.send(TransactionEvent.NavigateToEditScreen(transaction))
    }

    fun onTransactionDeleteClick(id: Int) = viewModelScope.launch {
        transactionEventChannel.send(TransactionEvent.NavigateToDeleteTransactionScreen(id))
    }

    fun onUndoDeleteClick(transaction: Transaction) = viewModelScope.launch {
        repository.insertNewTransaction(transaction)
    }


    fun onAddEditResult(result: Int) {
        when (result) {
            ADD_TRANSACTION_RESULT_OK -> showTransactionSavedConfirmationMsg("Transaction added")
            EDIT_TRANSACTION_RESULT_OK -> showTransactionSavedConfirmationMsg("Transaction updated")
        }
    }

    private fun showTransactionSavedConfirmationMsg(text: String) = viewModelScope.launch {
        transactionEventChannel.send(TransactionEvent.ShowTransactionSavedConfirmationMessage(text))
    }


    sealed class TransactionEvent {
        object NavigateToAddTransactionScreen : TransactionEvent()
        data class NavigateToEditScreen(val transaction: Transaction) : TransactionEvent()
        data class ShowTransactionSavedConfirmationMessage(val message: String) : TransactionEvent()
        data class NavigateToDeleteTransactionScreen(val id: Int) : TransactionEvent()
    }


}