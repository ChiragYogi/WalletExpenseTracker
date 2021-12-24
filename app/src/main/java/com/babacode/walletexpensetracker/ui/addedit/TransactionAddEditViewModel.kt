package com.babacode.walletexpensetracker.ui.addedit


import android.os.Parcelable
import androidx.lifecycle.*
import com.babacode.walletexpensetracker.data.model.PaymentType
import com.babacode.walletexpensetracker.data.model.Transaction
import com.babacode.walletexpensetracker.data.model.TransactionTag
import com.babacode.walletexpensetracker.data.model.TransactionType
import com.babacode.walletexpensetracker.repository.TransactionRepository
import com.babacode.walletexpensetracker.ui.ADD_TRANSACTION_RESULT_OK
import com.babacode.walletexpensetracker.ui.EDIT_TRANSACTION_RESULT_OK
import com.babacode.walletexpensetracker.ui.parseDouble
import com.babacode.walletexpensetracker.utiles.Extra
import com.babacode.walletexpensetracker.utiles.Extra.AMOUNT_CHECK_FOR_ADD
import com.babacode.walletexpensetracker.utiles.Extra.convertDateToLong
import com.babacode.walletexpensetracker.utiles.Extra.currentDayDate
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
    private val repository: TransactionRepository
) : ViewModel() {


    private val addEditTransactionChannel = Channel<AddEditTransactionEvent>()
    val addEditTransactionEvent = addEditTransactionChannel.receiveAsFlow()


    fun validateAndInsert(
        note: String, date: String, transactionType: String, amount: String,
        tag: String,
        paymentType: String, id: Int
    ) {


        if (transactionType.isBlank()) {
            showSelectTransactionTypeMessage("Please Select Type Of Transaction")
            return
        }

        if (amount.isBlank()) {
            showInvalidAmountMessage("Please Enter Amount")
            return
        }
        if (amount.length > AMOUNT_CHECK_FOR_ADD) {
            showInvalidAmountMessage("Amount Must Be less than  1000000")
            return
        }
        if (amount.contains("#") || amount.contains("/") || amount.contains("+")
            || amount.contains("-") || amount.contains("*")
        ) {
            showInvalidAmountMessage("Please Enter Valid Amount")
            return
        }

        if (note.isBlank()) {
            showInvalidNoteMessage("Please Add Note With Transaction")
            return
        }

        if (tag.isBlank()) {
            showSelectTransactionTagMessage("Please Select Type Of Tag")
            return
        }

        if (paymentType.isBlank()) {
            showSelectTransactionPaymentModeMessage("Please Select Type Of Payment")
            return
        }

        val addDate = convertDateToLong(date)
        val trnType = transactionType(transactionType)
        val trnTag = transactionTag(tag)
        val trnPaymentType = paymentMode(paymentType)

        val addAmount = parseDouble(amount)

        val addNewTransaction = Transaction(
            note, addDate!!, trnType, addAmount, trnTag,
            trnPaymentType, id
        )

        createTransaction(transaction = addNewTransaction)

    }

    fun validateAndUpdate(
        note: String, date: String, transactionType: String, amount: String,
        tag: String,
        paymentType: String, id: Int
    ) {

        if (transactionType.isBlank()) {
            showSelectTransactionTypeMessage("Please Select Type Of Transaction")
            return
        }

        if (amount.isBlank()) {
            showInvalidAmountMessage("Please Enter Amount")
            return
        }
        if (amount.length > AMOUNT_CHECK_FOR_ADD) {
            showInvalidAmountMessage("Amount Must Be less than  1000000")
            return
        }
        if (amount.contains("#") || amount.contains("/") || amount.contains("+")
            || amount.contains("-") || amount.contains("*")
        ) {
            showInvalidAmountMessage("Please Enter Valid Amount")
            return
        }

        if (note.isBlank()) {
            showInvalidNoteMessage("Please Add Note With Transaction")
            return
        }




        if (tag.isBlank()) {
            showSelectTransactionTagMessage("Please Select Type Of Tag")
            return
        }

        if (paymentType.isBlank()) {
            showSelectTransactionPaymentModeMessage("Please Select Type Of Payment")
            return
        }


        val updateDate = convertDateToLong(date)

        val updateTrnType = transactionType(transactionType)
        val updateTrnTag = transactionTag(tag)
        val updateTrnPaymentType = paymentMode(paymentType)

        val updateAmount = parseDouble(amount)
        val updateTransaction = Transaction(
            note, updateDate!!, updateTrnType, updateAmount, updateTrnTag,
            updateTrnPaymentType, id
        )

        updateTransaction(transaction = updateTransaction)

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










