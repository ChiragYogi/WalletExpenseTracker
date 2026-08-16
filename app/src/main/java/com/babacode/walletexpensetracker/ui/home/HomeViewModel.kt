package com.babacode.walletexpensetracker.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.babacode.walletexpensetracker.data.model.Transaction
import com.babacode.walletexpensetracker.repository.TransactionRepository
import com.babacode.walletexpensetracker.utiles.recoverWithDefault
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: TransactionRepository
) : ViewModel() {

    val recentTransaction: StateFlow<List<Transaction>> = repository.getAllTransaction()
        .recoverWithDefault(emptyList())
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    suspend fun deleteSingleTransaction(transaction: Transaction): Result<Unit> =
        runCatching { repository.deleteSingleTransaction(transaction) }
            .onFailure { exception -> FirebaseCrashlytics.getInstance().recordException(exception) }

}