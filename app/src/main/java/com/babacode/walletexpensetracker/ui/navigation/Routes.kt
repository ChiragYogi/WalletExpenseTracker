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

@Serializable
data object Insights : NavKey

// kind/value/offset mirror the reference design's typed search params
// (refrence/src/routes/insights-detail.tsx: validateSearch) for drilling into a
// filtered transaction list from Insights or Home.
@Serializable
data class InsightsDetail(val kind: InsightKind, val value: String? = null, val offset: Int = 0) : NavKey

@Serializable
data object Budgets : NavKey

@Serializable
data object Recurring : NavKey

@Serializable
data object Search : NavKey
