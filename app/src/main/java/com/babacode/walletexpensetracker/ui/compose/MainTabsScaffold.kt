package com.babacode.walletexpensetracker.ui.compose

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.babacode.walletexpensetracker.data.model.TransactionType
import com.babacode.walletexpensetracker.ui.budgets.BudgetsViewModel
import com.babacode.walletexpensetracker.ui.budgets.compose.BudgetsRoute
import com.babacode.walletexpensetracker.ui.detail.DetailViewViewModel
import com.babacode.walletexpensetracker.ui.detail.TransactionTypeRoute
import com.babacode.walletexpensetracker.ui.home.HomeViewModel
import com.babacode.walletexpensetracker.ui.home.compose.HomeRoute
import com.babacode.walletexpensetracker.ui.insights.InsightsViewModel
import com.babacode.walletexpensetracker.ui.insights.compose.InsightsRoute
import com.babacode.walletexpensetracker.ui.navigation.Budgets
import com.babacode.walletexpensetracker.ui.navigation.Home
import com.babacode.walletexpensetracker.ui.navigation.InsightKind
import com.babacode.walletexpensetracker.ui.navigation.InsightsDetail
import com.babacode.walletexpensetracker.ui.navigation.Insights
import com.babacode.walletexpensetracker.ui.navigation.TabActions
import com.babacode.walletexpensetracker.ui.navigation.TabRoute
import com.babacode.walletexpensetracker.ui.navigation.TransactionTypeDetail
import com.babacode.walletexpensetracker.ui.navigation.navInstantPredictiveBackTransitionSpec
import com.babacode.walletexpensetracker.ui.navigation.navInstantTransitionSpec

// Hosts the 4-tab section (Home, Detail, Insights, Budgets) behind its own inner
// NavDisplay + bottom nav, as a single destination on the outer NavDisplay
// (MainActivity.kt). Off-tab screens (AddTransaction, Settings, Calendar, delete
// dialog, InsightsDetail, Recurring, Search) live outside this composable entirely,
// on the outer backstack, so the bottom nav is structurally absent for them rather
// than conditionally hidden.
//
// contentWindowInsets is zeroed here (matching the old AppScaffold) so this only
// reserves space for the bottom nav's own measured height — each tab screen's own
// inner Scaffold/TopAppBar still owns the system bar insets, avoiding doubled padding.
@Composable
fun MainTabsScaffold(
    currencyCode: String,
    resultEvent: Int?,
    onResultEventConsumed: () -> Unit,
    showNotificationPermissionSnackbar: Boolean,
    onNotificationPermissionSnackbarShown: () -> Unit,
    onOpenNotificationSettings: () -> Unit,
    actions: TabActions,
    modifier: Modifier = Modifier
) {
    val tabBackStack = rememberNavBackStack(Home)

    val onBottomNavigate: (TabRoute) -> Unit = { route ->
        if (tabBackStack.lastOrNull() != route) {
            tabBackStack.clear()
            tabBackStack.add(route)
        }
    }

    Scaffold(
        modifier = modifier,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            BottomNav(
                currentRoute = tabBackStack.lastOrNull() as? TabRoute,
                onNavigate = onBottomNavigate,
                onAddClick = actions.onAddTransactionClick
            )
        }
    ) { innerPadding ->
        NavDisplay(
            modifier = Modifier
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding),
            backStack = tabBackStack,
            onBack = { tabBackStack.removeLastOrNull() },
            entryDecorators = listOf(
                rememberSaveableStateHolderNavEntryDecorator(),
                // Bottom-nav switches replace the whole backstack (see onBottomNavigate below),
                // which pops the tab being left. Without this override, Nav3's default
                // behavior would clear that tab's ViewModelStore on every switch, forcing
                // Home/Insights/Budgets/Detail to reload from the database each time they're
                // revisited instead of reusing their already-loaded state.
                rememberViewModelStoreNavEntryDecorator(removeViewModelStoreOnPop = { false })
            ),
            entryProvider = entryProvider {
                entry<Home> {
                    val viewModel = hiltViewModel<HomeViewModel>()
                    HomeRoute(
                        currencyCode = currencyCode,
                        viewModel = viewModel,
                        resultEvent = resultEvent,
                        onResultEventConsumed = onResultEventConsumed,
                        onIncomeClick = { tabBackStack.add(TransactionTypeDetail(TransactionType.INCOME)) },
                        onExpenseClick = { tabBackStack.add(TransactionTypeDetail(TransactionType.EXPENSE)) },
                        onTransactionClick = actions.onEditTransaction,
                        onLongPress = actions.onDeleteTransaction,
                        onOpenCalenderClick = actions.onOpenCalendar,
                        onOpenSettingsClick = actions.onOpenSettings,
                        onOpenSearchClick = actions.onOpenSearch,
                        onOpenRecurringClick = actions.onOpenRecurring,
                        onManageBudgetsClick = { onBottomNavigate(Budgets) },
                        onSeeAllTransactionsClick = actions.onOpenSearch,
                        showNotificationPermissionSnackbar = showNotificationPermissionSnackbar,
                        onNotificationPermissionSnackbarShown = onNotificationPermissionSnackbarShown,
                        onOpenNotificationSettings = onOpenNotificationSettings
                    )
                }
                entry<TransactionTypeDetail> { key ->
                    val viewModel = hiltViewModel<DetailViewViewModel>()
                    LaunchedEffect(key.transactionType) {
                        viewModel.setTransactionType(key.transactionType)
                    }
                    TransactionTypeRoute(
                        transactionType = key.transactionType,
                        currencyCode = currencyCode,
                        viewModel = viewModel,
                        resultEvent = resultEvent,
                        onResultEventConsumed = onResultEventConsumed,
                        onTransactionClick = actions.onEditTransaction,
                        onLongPress = actions.onDeleteTransaction
                    )
                }
                entry<Insights> {
                    val viewModel = hiltViewModel<InsightsViewModel>()
                    InsightsRoute(
                        currencyCode = currencyCode,
                        viewModel = viewModel,
                        onCategoryClick = { tag ->
                            actions.onOpenInsightsDetail(InsightsDetail(kind = InsightKind.TAG, value = tag))
                        },
                        onModeClick = { mode ->
                            actions.onOpenInsightsDetail(InsightsDetail(kind = InsightKind.MODE, value = mode))
                        },
                        onMonthClick = { offset ->
                            actions.onOpenInsightsDetail(InsightsDetail(kind = InsightKind.MONTH, offset = offset))
                        },
                        onSeeAllTopSpendsClick = {
                            actions.onOpenInsightsDetail(InsightsDetail(kind = InsightKind.TOP))
                        },
                        onTransactionClick = actions.onEditTransaction
                    )
                }
                entry<Budgets> {
                    val viewModel = hiltViewModel<BudgetsViewModel>()
                    BudgetsRoute(
                        currencyCode = currencyCode,
                        viewModel = viewModel
                    )
                }
            },
            transitionSpec = navInstantTransitionSpec(),
            popTransitionSpec = navInstantTransitionSpec(),
            predictivePopTransitionSpec = navInstantPredictiveBackTransitionSpec()
        )
    }
}
