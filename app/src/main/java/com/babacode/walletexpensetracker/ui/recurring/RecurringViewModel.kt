package com.babacode.walletexpensetracker.ui.recurring

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.babacode.walletexpensetracker.data.model.Frequency
import com.babacode.walletexpensetracker.data.model.RecurringRule
import com.babacode.walletexpensetracker.data.model.TagCatalog
import com.babacode.walletexpensetracker.repository.RecurringRepository
import com.babacode.walletexpensetracker.repository.TransactionRepository
import com.babacode.walletexpensetracker.utiles.Extra
import com.babacode.walletexpensetracker.utiles.Extra.currentDayDate
import com.babacode.walletexpensetracker.utiles.recoverWithDefault
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class RecurringViewModel @Inject constructor(
    private val recurringRepository: RecurringRepository,
    private val transactionRepository: TransactionRepository
) : ViewModel() {

    val uiState: StateFlow<RecurringUiState> = recurringRepository.getAllRecurringRules()
        .recoverWithDefault(emptyList())
        .map { rules -> RecurringUiState(isLoading = false, rules = rules) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), RecurringUiState.Loading)

    private val _formState = MutableStateFlow(RecurringFormState(nextDate = Extra.convertLongDateToStringDate(currentDayDate())))
    val formState: StateFlow<RecurringFormState> = _formState.asStateFlow()

    fun onTypeChanged(type: String) = _formState.update {
        it.copy(type = type, tag = TagCatalog.tagsFor(Extra.transactionType(type)).first())
    }

    fun onAmountChanged(value: String) = _formState.update { it.copy(amount = value.filter(Char::isDigit)) }

    fun onNoteChanged(value: String) = _formState.update { it.copy(note = value) }

    fun onTagChanged(value: String) = _formState.update { it.copy(tag = value) }

    fun onModeChanged(value: String) = _formState.update { it.copy(mode = value) }

    fun onFrequencyChanged(value: Frequency) = _formState.update { it.copy(frequency = value) }

    fun onNextDateChanged(value: String) = _formState.update { it.copy(nextDate = value) }

    fun onSaveRuleClicked(onSaved: () -> Unit) {
        val state = _formState.value
        val amountValue = state.amount.toDoubleOrNull()
        if (amountValue == null || amountValue <= 0) return
        val nextDateLong = try {
            Extra.convertStringDateToLong(state.nextDate)
        } catch (e: IllegalArgumentException) {
            return
        }

        viewModelScope.launch {
            recurringRepository.insertRecurringRule(
                RecurringRule(
                    type = Extra.transactionType(state.type),
                    amount = amountValue,
                    note = state.note,
                    tag = state.tag,
                    mode = Extra.paymentMode(state.mode),
                    frequency = state.frequency,
                    nextDate = nextDateLong,
                    active = true
                )
            )
            _formState.value = RecurringFormState(nextDate = Extra.convertLongDateToStringDate(currentDayDate()))
            onSaved()
        }
    }

    fun onRunNowClicked(rule: RecurringRule) = viewModelScope.launch {
        postRecurringRule(rule, transactionRepository, recurringRepository)
    }

    fun onToggleActive(rule: RecurringRule) = viewModelScope.launch {
        recurringRepository.updateRecurringRule(rule.copy(active = !rule.active))
    }

    fun onDeleteRule(rule: RecurringRule) = viewModelScope.launch {
        recurringRepository.deleteRecurringRule(rule)
    }
}
