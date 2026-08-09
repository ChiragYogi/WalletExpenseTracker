package com.babacode.walletexpensetracker.ui.navigation

import androidx.navigation3.runtime.NavKey
import com.babacode.walletexpensetracker.data.model.Transaction
import com.babacode.walletexpensetracker.data.model.TransactionType
import kotlinx.serialization.Serializable

@Serializable
data object Home : NavKey

@Serializable
data class AddTransaction(val editTransaction: Transaction?, val title: String) : NavKey

@Serializable
data class TransactionTypeDetail(val transactionType: TransactionType?) : NavKey

@Serializable
data object AppSettings : NavKey

@Serializable
data object CalenderView : NavKey

@Serializable
data class DeleteTransactionRoute(val transaction: Transaction) : NavKey
