package com.babacode.walletexpensetracker.ui.navigation

import androidx.navigation3.runtime.NavKey
import com.babacode.walletexpensetracker.data.model.Transaction
import com.babacode.walletexpensetracker.data.model.TransactionType
import kotlinx.serialization.Serializable

// Destination for the outer NavDisplay that hosts the 4-tab section (its own nested
// NavDisplay + bottom nav). Deliberately not named "Home" to avoid confusion with the
// Home tab itself.
@Serializable
data object MainTabs : NavKey

// Routes that live on the tab section's own inner backstack, rendered inside
// MainTabsScaffold behind the bottom nav bar.
sealed interface TabRoute : NavKey

@Serializable
data object Home : TabRoute

@Serializable
data class AddTransaction(val editTransaction: Transaction?, val title: String) : NavKey

@Serializable
data class TransactionTypeDetail(val transactionType: TransactionType?) : TabRoute

@Serializable
data object AppSettings : NavKey

@Serializable
data object CalenderView : NavKey

@Serializable
data class DeleteTransactionRoute(val transaction: Transaction) : NavKey

@Serializable
data object Insights : TabRoute

// kind/value/offset mirror the reference design's typed search params
// (refrence/src/routes/insights-detail.tsx: validateSearch) for drilling into a
// filtered transaction list from Insights or Home.
@Serializable
data class InsightsDetail(val kind: InsightKind, val value: String? = null, val offset: Int = 0) : NavKey

@Serializable
data object Budgets : TabRoute

@Serializable
data object Recurring : NavKey

@Serializable
data object Search : NavKey
