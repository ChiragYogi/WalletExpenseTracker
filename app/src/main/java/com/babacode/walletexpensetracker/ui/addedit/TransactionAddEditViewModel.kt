package com.babacode.walletexpensetracker.ui.addedit


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.babacode.walletexpensetracker.data.model.Transaction
import com.babacode.walletexpensetracker.repository.TransactionRepository
import com.babacode.walletexpensetracker.ui.ADD_TRANSACTION_RESULT_OK
import com.babacode.walletexpensetracker.ui.EDIT_TRANSACTION_RESULT_OK
import com.babacode.walletexpensetracker.ui.addedit.compose.TransactionAddEditUiState
import com.babacode.walletexpensetracker.utiles.Extra.AMOUNT_CHECK_FOR_ADD
import com.babacode.walletexpensetracker.utiles.Extra.NOTE_LENGTH_VALIDATE
import com.babacode.walletexpensetracker.utiles.Extra.convertLongDateToStringDate
import com.babacode.walletexpensetracker.utiles.Extra.convertStringDateToLong
import com.babacode.walletexpensetracker.utiles.Extra.currentDayDate
import com.babacode.walletexpensetracker.utiles.Extra.parseDouble
import com.babacode.walletexpensetracker.utiles.Extra.paymentMode
import com.babacode.walletexpensetracker.utiles.Extra.transactionTag
import com.babacode.walletexpensetracker.utiles.Extra.transactionType
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class TransactionAddEditViewModel @Inject constructor(
    private val repository: TransactionRepository,
) : ViewModel() {

    private var hasInitialized = false

    private val _uiState = MutableStateFlow(TransactionAddEditUiState.Default)
    val uiState: StateFlow<TransactionAddEditUiState> = _uiState.asStateFlow()

    private val addEditTransactionChannel = Channel<AddEditTransactionEvent>()
    val addEditTransactionEvent = addEditTransactionChannel.receiveAsFlow()

    fun initialize(editTransaction: Transaction?) {
        if (hasInitialized) return
        hasInitialized = true
        _uiState.update {
            it.copy(
                transactionId = editTransaction?.id ?: 0,
                type = editTransaction?.transactionType?.toString().orEmpty(),
                amount = editTransaction?.amount?.toInt()?.toString().orEmpty(),
                note = editTransaction?.note.orEmpty(),
                date = editTransaction?.let { transaction -> convertLongDateToStringDate(transaction.date) }
                    ?: convertLongDateToStringDate(currentDayDate()),
                tag = editTransaction?.tag?.toString().orEmpty(),
                paymentMode = editTransaction?.paymentType?.toString().orEmpty(),
            )
        }
    }

    fun onTypeChanged(value: String) = _uiState.update { it.copy(type = value) }

    fun onAmountChanged(value: String) = _uiState.update { it.copy(amount = value) }

    fun onNoteChanged(value: String) {
        if (value.length <= NOTE_LENGTH_VALIDATE) _uiState.update { it.copy(note = value) }
    }

    fun onDateChanged(value: String) = _uiState.update { it.copy(date = value) }

    fun onTagChanged(value: String) = _uiState.update { it.copy(tag = value) }

    fun onPaymentModeChanged(value: String) = _uiState.update { it.copy(paymentMode = value) }

    fun onSaveClicked() {
        val state = _uiState.value

        if (state.type.isBlank()) {
            showSelectTransactionTypeError()
            return
        }

        if (state.amount.isBlank()) {
            showInvalidAmountError(TransactionValidationError.ENTER_AMOUNT)
            return
        }
        if (state.amount.length > AMOUNT_CHECK_FOR_ADD) {
            showInvalidAmountError(TransactionValidationError.AMOUNT_TOO_LARGE)
            return
        }
        if (state.amount.contains("#") || state.amount.contains("/") || state.amount.contains("+")
            || state.amount.contains("-") || state.amount.contains("*") || state.amount.contains(".")
            || state.amount == "0"
        ) {
            showInvalidAmountError(TransactionValidationError.INVALID_AMOUNT)
            return
        }

        if (state.note.isBlank()) {
            showInvalidNoteError(TransactionValidationError.ENTER_NOTE)
            return
        }

        if (state.note.length >= NOTE_LENGTH_VALIDATE) {
            showInvalidNoteError(TransactionValidationError.NOTE_TOO_LONG)
            return
        }

        if (state.tag.isBlank()) {
            showSelectTransactionTagError()
            return
        }

        if (state.paymentMode.isBlank()) {
            showSelectTransactionPaymentModeError()
            return
        }

        val addDate = try {
            convertStringDateToLong(state.date)
        } catch (e: IllegalArgumentException) {
            showInvalidDateError()
            return
        }

        val addAmount = parseDouble(state.amount)
        val addType = transactionType(state.type)
        val addTag = transactionTag(state.tag)
        val addPaymentMode = paymentMode(state.paymentMode)

        if (state.transactionId == 0) {
            val addNewTransaction = Transaction(
                state.note,
                addDate,
                addType,
                addAmount,
                addTag,
                addPaymentMode,
                state.transactionId
            )
            createTransaction(addNewTransaction)
        } else {
            val updateCurrentTransaction = Transaction(
                state.note,
                addDate,
                addType,
                addAmount,
                addTag,
                addPaymentMode,
                state.transactionId
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
            AddEditTransactionEvent.ShowSaveError(TransactionValidationError.SAVE_ERROR)
        )
    }

    private fun showInvalidNoteError(error: TransactionValidationError) = viewModelScope.launch {
        addEditTransactionChannel.send(AddEditTransactionEvent.ShowInvalidNote(error))
    }

    private fun showSelectTransactionTypeError() = viewModelScope.launch {
        addEditTransactionChannel.send(
            AddEditTransactionEvent.ShowSelectTransactionType(TransactionValidationError.SELECT_TRANSACTION_TYPE)
        )
    }

    private fun showSelectTransactionTagError() = viewModelScope.launch {
        addEditTransactionChannel.send(
            AddEditTransactionEvent.ShowSelectTransactionTag(TransactionValidationError.SELECT_TAG)
        )
    }

    private fun showSelectTransactionPaymentModeError() = viewModelScope.launch {
        addEditTransactionChannel.send(
            AddEditTransactionEvent.ShowSelectTransactionPaymentMode(TransactionValidationError.SELECT_PAYMENT_MODE)
        )
    }

    private fun showInvalidAmountError(error: TransactionValidationError) = viewModelScope.launch {
        addEditTransactionChannel.send(AddEditTransactionEvent.ShowInvalidAmount(error))
    }

    private fun showInvalidDateError() = viewModelScope.launch {
        addEditTransactionChannel.send(
            AddEditTransactionEvent.ShowInvalidDate(TransactionValidationError.INVALID_DATE)
        )
    }


    sealed class AddEditTransactionEvent {
        data class ShowInvalidNote(val error: TransactionValidationError) : AddEditTransactionEvent()
        data class ShowInvalidAmount(val error: TransactionValidationError) : AddEditTransactionEvent()
        data class ShowInvalidDate(val error: TransactionValidationError) : AddEditTransactionEvent()
        data class ShowSaveError(val error: TransactionValidationError) : AddEditTransactionEvent()
        data class ShowSelectTransactionType(val error: TransactionValidationError) : AddEditTransactionEvent()
        data class ShowSelectTransactionTag(val error: TransactionValidationError) : AddEditTransactionEvent()
        data class ShowSelectTransactionPaymentMode(val error: TransactionValidationError) : AddEditTransactionEvent()
        data class NavigateBackWithResult(val result: Int) : AddEditTransactionEvent()
    }


}
