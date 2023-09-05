package com.babacode.walletexpensetracker.ui.addedit


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.babacode.walletexpensetracker.data.model.Transaction
import com.babacode.walletexpensetracker.repository.TransactionRepository
import com.babacode.walletexpensetracker.ui.ADD_TRANSACTION_RESULT_OK
import com.babacode.walletexpensetracker.ui.EDIT_TRANSACTION_RESULT_OK
import com.babacode.walletexpensetracker.utiles.Extra.AMOUNT_CHECK_FOR_ADD
import com.babacode.walletexpensetracker.utiles.Extra.NOTE_LENGTH_VALIDATE
import com.babacode.walletexpensetracker.utiles.Extra.convertStringDateToLong
import com.babacode.walletexpensetracker.utiles.Extra.parseDouble
import com.babacode.walletexpensetracker.utiles.Extra.paymentMode
import com.babacode.walletexpensetracker.utiles.Extra.transactionTag
import com.babacode.walletexpensetracker.utiles.Extra.transactionType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class TransactionAddEditViewModel @Inject constructor(
    private val repository: TransactionRepository,
) : ViewModel() {


    private val addEditTransactionChannel = Channel<AddEditTransactionEvent>()
    val addEditTransactionEvent = addEditTransactionChannel.receiveAsFlow()



    fun validateAndInsertOrUpdate(
        transactionType: String,
        transactionAmount: String,
        transactionNote: String,
        transactionDate: String,
        transactionTag: String,
        transactionPaymentType: String,
        transactionID: Int
    ) {


        if (transactionType.isBlank()) {
            showSelectTransactionTypeMessage("Please Select Type Of Transaction")
            return
        }

        if (transactionAmount.isBlank()) {
            showInvalidAmountMessage("Please Enter Amount")
            return
        }
        if (transactionAmount.length > AMOUNT_CHECK_FOR_ADD) {
            showInvalidAmountMessage("Amount Must Be less than  1000000")
            return
        }
        if (transactionAmount.contains("#") || transactionAmount.contains("/") || transactionAmount.contains(
                "+"
            )
            || transactionAmount.contains("-") || transactionAmount.contains("*") || transactionAmount.contains(
                "."
            ) || transactionAmount == "0"
        ) {
            showInvalidAmountMessage("Please Enter Valid Amount")
            return
        }

        if (transactionNote.isBlank()) {
            showInvalidNoteMessage("Please Add Note With Transaction")
            return
        }

        if (transactionNote.length >= NOTE_LENGTH_VALIDATE) {
            showInvalidNoteMessage("Note character must Be less than 40")
            return
        }

        if (transactionTag.isBlank()) {
            showSelectTransactionTagMessage("Please Select Type Of Tag")
            return
        }

        if (transactionPaymentType.isBlank()) {
            showSelectTransactionPaymentModeMessage("Please Select Type Of Payment")
            return
        }


        val addAmount = parseDouble(transactionAmount)
        val addType = transactionType(transactionType)
        val addTag = transactionTag(transactionTag)
        val addDate = convertStringDateToLong(transactionDate)
        val addPaymentMode = paymentMode(transactionPaymentType)


        if (transactionID == 0) {

            val addNewTransaction = Transaction(
                transactionNote,
                addDate,
                addType,
                addAmount,
                addTag,
                addPaymentMode,
                transactionID
            )
            createTransaction(addNewTransaction)
        } else {
            val updateCurrentTransaction = Transaction(
                transactionNote,
                addDate,
                addType,
                addAmount,
                addTag,
                addPaymentMode,
                transactionID
            )
            updateTransaction(updateCurrentTransaction)
        }

    }


    private fun createTransaction(transaction: Transaction) = viewModelScope.launch {
        repository.insertNewTransaction(transaction)
        addEditTransactionChannel.send(
            AddEditTransactionEvent.NavigateBackWithResult(
                ADD_TRANSACTION_RESULT_OK
            )
        )
    }

    private fun updateTransaction(transaction: Transaction) = viewModelScope.launch {
        repository.updateTransaction(transaction)
        addEditTransactionChannel.send(
            AddEditTransactionEvent.NavigateBackWithResult(
                EDIT_TRANSACTION_RESULT_OK
            )
        )
    }

    private fun showInvalidNoteMessage(noteMsg: String) = viewModelScope.launch {
        addEditTransactionChannel.send(AddEditTransactionEvent.ShowInvalidNote(noteMsg))
    }

    private fun showSelectTransactionTypeMessage(typeMsg: String) = viewModelScope.launch {
        addEditTransactionChannel.send(AddEditTransactionEvent.ShowSelectTransactionType(typeMsg))
    }

    private fun showSelectTransactionTagMessage(tagMsg: String) = viewModelScope.launch {
        addEditTransactionChannel.send(AddEditTransactionEvent.ShowSelectTransactionTag(tagMsg))
    }

    private fun showSelectTransactionPaymentModeMessage(paymentMsg: String) =
        viewModelScope.launch {
            addEditTransactionChannel.send(
                AddEditTransactionEvent.ShowSelectTransactionPaymentMode(
                    paymentMsg
                )
            )
        }

    private fun showInvalidAmountMessage(amountMsg: String) = viewModelScope.launch {
        addEditTransactionChannel.send(AddEditTransactionEvent.ShowInvalidAmount(amountMsg))
    }


    sealed class AddEditTransactionEvent {
        data class ShowInvalidNote(val msg: String) : AddEditTransactionEvent()
        data class ShowInvalidAmount(val msg: String) : AddEditTransactionEvent()
        data class ShowSelectTransactionType(val msg: String) : AddEditTransactionEvent()
        data class ShowSelectTransactionTag(val msg: String) : AddEditTransactionEvent()
        data class ShowSelectTransactionPaymentMode(val msg: String) : AddEditTransactionEvent()
        data class NavigateBackWithResult(val result: Int) : AddEditTransactionEvent()
    }


}










