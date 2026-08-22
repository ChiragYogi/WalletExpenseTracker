package com.babacode.walletexpensetracker.ui.navigation

import com.babacode.walletexpensetracker.data.model.Transaction

// Outer-backstack-bound navigation callbacks needed by the 4 tab screens (and the FAB)
// hosted inside MainTabsScaffold. Tab-switch navigation (moving between Home/Detail/
// Insights/Budgets) stays local to MainTabsScaffold's own inner backstack and is
// deliberately not part of this bundle.
data class TabActions(
    val onAddTransactionClick: () -> Unit,
    val onEditTransaction: (Transaction) -> Unit,
    val onDeleteTransaction: (Transaction) -> Unit,
    val onOpenCalendar: () -> Unit,
    val onOpenSettings: () -> Unit,
    val onOpenSearch: () -> Unit,
    val onOpenRecurring: () -> Unit,
    val onOpenInsightsDetail: (InsightsDetail) -> Unit
)
