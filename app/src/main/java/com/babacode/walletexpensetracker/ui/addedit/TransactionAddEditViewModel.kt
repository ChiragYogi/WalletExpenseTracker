package com.babacode.walletexpensetracker.ui.addedit


import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.babacode.walletexpensetracker.R
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
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class TransactionAddEditViewModel @Inject constructor(
    private val repository: TransactionRepository,
    @ApplicationContext private val context: Context,
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
            showSelectTransactionTypeMessage(context.getString(R.string.error_select_transaction_type))
            return
        }

        if (transactionAmount.isBlank()) {
            showInvalidAmountMessage(context.getString(R.string.error_enter_amount))
            return
        }
        if (transactionAmount.length > AMOUNT_CHECK_FOR_ADD) {
            showInvalidAmountMessage(context.getString(R.string.error_amount_too_large))
            return
        }
        if (transactionAmount.contains("#") || transactionAmount.contains("/") || transactionAmount.contains(
                "+"
            )
            || transactionAmount.contains("-") || transactionAmount.contains("*") || transactionAmount.contains(
                "."
            ) || transactionAmount == "0"
        ) {
            showInvalidAmountMessage(context.getString(R.string.error_invalid_amount))
            return
        }

        if (transactionNote.isBlank()) {
            showInvalidNoteMessage(context.getString(R.string.error_enter_note))
            return
        }

        if (transactionNote.length >= NOTE_LENGTH_VALIDATE) {
            showInvalidNoteMessage(context.getString(R.string.error_note_too_long))
            return
        }

        if (transactionTag.isBlank()) {
            showSelectTransactionTagMessage(context.getString(R.string.error_select_tag))
            return
        }

        if (transactionPaymentType.isBlank()) {
            showSelectTransactionPaymentModeMessage(context.getString(R.string.error_select_payment_mode))
            return
        }


        val addDate = try {
            convertStringDateToLong(transactionDate)
        } catch (e: IllegalArgumentException) {
            showInvalidDateMessage(context.getString(R.string.error_invalid_date))
            return
        }

        val addAmount = parseDouble(transactionAmount)
        val addType = transactionType(transactionType)
        val addTag = transactionTag(transactionTag)
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
        runCatching { repository.insertNewTransaction(transaction) }
            .onSuccess {
                addEditTransactionChannel.send(
                    AddEditTransactionEvent.NavigateBackWithResult(ADD_TRANSACTION_RESULT_OK)
                )
            }
            .onFailure { exception -> showSaveErrorMessage(exception) }
    }

    private fun updateTransaction(transaction: Transaction) = viewModelScope.launch {
        runCatching { repository.updateTransaction(transaction) }
            .onSuccess {
                addEditTransactionChannel.send(
                    AddEditTransactionEvent.NavigateBackWithResult(EDIT_TRANSACTION_RESULT_OK)
                )
            }
            .onFailure { exception -> showSaveErrorMessage(exception) }
    }

    private suspend fun showSaveErrorMessage(exception: Throwable) {
        FirebaseCrashlytics.getInstance().recordException(exception)
        addEditTransactionChannel.send(
            AddEditTransactionEvent.ShowSaveError(context.getString(R.string.database_error))
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

    private fun showInvalidDateMessage(dateMsg: String) = viewModelScope.launch {
        addEditTransactionChannel.send(AddEditTransactionEvent.ShowInvalidDate(dateMsg))
    }


    sealed class AddEditTransactionEvent {
        data class ShowInvalidNote(val msg: String) : AddEditTransactionEvent()
        data class ShowInvalidAmount(val msg: String) : AddEditTransactionEvent()
        data class ShowInvalidDate(val msg: String) : AddEditTransactionEvent()
        data class ShowSaveError(val msg: String) : AddEditTransactionEvent()
        data class ShowSelectTransactionType(val msg: String) : AddEditTransactionEvent()
        data class ShowSelectTransactionTag(val msg: String) : AddEditTransactionEvent()
        data class ShowSelectTransactionPaymentMode(val msg: String) : AddEditTransactionEvent()
        data class NavigateBackWithResult(val result: Int) : AddEditTransactionEvent()
    }


}










