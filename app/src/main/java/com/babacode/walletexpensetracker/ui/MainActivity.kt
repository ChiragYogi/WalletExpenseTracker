package com.babacode.walletexpensetracker.ui

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.content.ContextCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.scene.DialogSceneStrategy
import androidx.navigation3.ui.NavDisplay
import com.babacode.walletexpensetracker.R
import com.babacode.walletexpensetracker.ui.addedit.TransactionAddEditViewModel
import com.babacode.walletexpensetracker.ui.addedit.compose.AddTransactionRoute
import com.babacode.walletexpensetracker.ui.calender.CalenderViewViewModel
import com.babacode.walletexpensetracker.ui.calender.compose.CalenderRoute
import com.babacode.walletexpensetracker.ui.compose.MainTabsScaffold
import com.babacode.walletexpensetracker.ui.home.HomeViewModel
import com.babacode.walletexpensetracker.ui.home.compose.DeleteTransactionDialog
import com.babacode.walletexpensetracker.ui.insights.InsightsDetailViewModel
import com.babacode.walletexpensetracker.ui.insights.compose.InsightsDetailRoute
import com.babacode.walletexpensetracker.ui.navigation.AddTransaction
import com.babacode.walletexpensetracker.ui.navigation.CalenderView
import com.babacode.walletexpensetracker.ui.navigation.DeleteTransactionRoute
import com.babacode.walletexpensetracker.ui.navigation.MainTabs
import com.babacode.walletexpensetracker.ui.navigation.AppSettings
import com.babacode.walletexpensetracker.ui.navigation.InsightsDetail
import com.babacode.walletexpensetracker.ui.navigation.Recurring
import com.babacode.walletexpensetracker.ui.navigation.TabActions
import com.babacode.walletexpensetracker.ui.navigation.navBackTransitionSpec
import com.babacode.walletexpensetracker.ui.navigation.navForwardTransitionSpec
import com.babacode.walletexpensetracker.ui.navigation.navPredictiveBackTransitionSpec
import com.babacode.walletexpensetracker.ui.recurring.RecurringViewModel
import com.babacode.walletexpensetracker.ui.recurring.compose.RecurringRoute
import com.babacode.walletexpensetracker.ui.navigation.Search
import com.babacode.walletexpensetracker.ui.search.SearchViewModel
import com.babacode.walletexpensetracker.ui.search.compose.SearchRoute
import com.babacode.walletexpensetracker.repository.SettingsRepository
import com.babacode.walletexpensetracker.ui.setting.SettingsViewModel
import com.babacode.walletexpensetracker.ui.setting.compose.SettingsRoute
import com.babacode.walletexpensetracker.ui.setting.notification.AlarmUtils
import com.babacode.walletexpensetracker.ui.theme.WalletExpenseTheme
import com.babacode.walletexpensetracker.utiles.Extra.privacy_policy_url
import dagger.hilt.android.AndroidEntryPoint
import androidx.navigationevent.compose.LocalNavigationEventDispatcherOwner
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject
import androidx.core.net.toUri

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject lateinit var settingsRepository: SettingsRepository

    private val alarmUtils by lazy {
        AlarmUtils(applicationContext)
    }

    private var showNotificationSnackbar by mutableStateOf(false)

    private val notificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (!isGranted) {
                showNotificationSnackbar = true
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        checkForNotificationPermission()

        setContent {
            CompositionLocalProvider(LocalNavigationEventDispatcherOwner provides this) {
                val notificationsEnabled by settingsRepository.notificationsEnabled.collectAsStateWithLifecycle(
                    initialValue = true
                )
                LaunchedEffect(notificationsEnabled) {
                    if (notificationsEnabled) {
                        alarmUtils.initAlarmForNotification(Calendar.getInstance())
                    } else {
                        alarmUtils.cancelNotificationAlarm()
                    }
                }

                val currencyCode by settingsRepository.currency.collectAsStateWithLifecycle(initialValue = stringResource(R.string.usDollarCurrencyCodeValue))
                WalletExpenseTheme {
                    MainNavigation(
                        currencyCode = currencyCode,
                        showNotificationSnackbar = showNotificationSnackbar,
                        onNotificationSnackbarShown = { showNotificationSnackbar = false },
                        onOpenNotificationSettings = ::openNotificationSettings,
                        onReportBugClick = ::reportBug,
                        onRequestFeatureClick = ::requestNewFeature,
                        onPrivacyPolicyClick = ::openPrivacyPolicy
                    )
                }
            }
        }
    }

    private fun checkForNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_DENIED
            ) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

    }

    private fun openNotificationSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        intent.data = "package:$packageName".toUri()
        startActivity(intent)
    }

    private fun reportBug() {
        val subject = getString(R.string.foundABugInApp)
        val email = getString(R.string.emailForQuery)
        sendSupportEmail(subject, email)
    }

    private fun requestNewFeature() {
        val subject = getString(R.string.requestFromUser)
        val email = getString(R.string.emailForQuery)
        sendSupportEmail(subject, email)
    }

    private fun sendSupportEmail(subject: String, email: String) {
        val selectIntent = Intent().apply {
            action = Intent.ACTION_SENDTO
            data = "mailto:".toUri()
        }
        val emailIntent = Intent(Intent.ACTION_SEND).apply {
            putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
            putExtra(Intent.EXTRA_SUBJECT, subject)
            this.selector = selectIntent
        }

        try {
            startActivity(Intent.createChooser(emailIntent, getString(R.string.send_mail_chooser_title)))
        } catch (e: Exception) {
            Toast.makeText(this, getString(R.string.emailError), Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }

    private fun openPrivacyPolicy() {
        try {
            val intent = Intent.makeMainSelectorActivity(Intent.ACTION_MAIN, Intent.CATEGORY_APP_BROWSER)
            intent.data = privacy_policy_url.toUri()
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show()
        }
    }
}

@Composable
private fun MainNavigation(
    currencyCode: String,
    showNotificationSnackbar: Boolean,
    onNotificationSnackbarShown: () -> Unit,
    onOpenNotificationSettings: () -> Unit,
    onReportBugClick: () -> Unit,
    onRequestFeatureClick: () -> Unit,
    onPrivacyPolicyClick: () -> Unit
) {
    val context = LocalContext.current
    val backStack = rememberNavBackStack(MainTabs)
    val dialogSceneStrategy = remember { DialogSceneStrategy<NavKey>() }

    var resultEvent by remember { mutableStateOf<Int?>(null) }

    val addTransactionTitle = stringResource(R.string.add_transaction_title)
    val editTransactionTitle = stringResource(R.string.edit_transaction_title)

    val onNavigateToEdit: (com.babacode.walletexpensetracker.data.model.Transaction) -> Unit = { transaction ->
        backStack.add(AddTransaction(transaction, editTransactionTitle))
    }
    val onNavigateToDelete: (com.babacode.walletexpensetracker.data.model.Transaction) -> Unit = { transaction ->
        backStack.add(DeleteTransactionRoute(transaction))
    }

    val tabActions = TabActions(
        onAddTransactionClick = { backStack.add(AddTransaction(null, addTransactionTitle)) },
        onEditTransaction = onNavigateToEdit,
        onDeleteTransaction = onNavigateToDelete,
        onOpenCalendar = { backStack.add(CalenderView) },
        onOpenSettings = { backStack.add(AppSettings) },
        onOpenSearch = { backStack.add(Search) },
        onOpenRecurring = { backStack.add(Recurring) },
        onOpenInsightsDetail = { detail -> backStack.add(detail) }
    )

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        sceneStrategies = listOf(dialogSceneStrategy),
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        entryProvider = entryProvider {
            entry<MainTabs> {
                MainTabsScaffold(
                    currencyCode = currencyCode,
                    resultEvent = resultEvent,
                    onResultEventConsumed = { resultEvent = null },
                    showNotificationPermissionSnackbar = showNotificationSnackbar,
                    onNotificationPermissionSnackbarShown = onNotificationSnackbarShown,
                    onOpenNotificationSettings = onOpenNotificationSettings,
                    actions = tabActions
                )
            }
            entry<AddTransaction> { key ->
                val viewModel = hiltViewModel<TransactionAddEditViewModel>()
                AddTransactionRoute(
                    viewModel = viewModel,
                    editTransaction = key.editTransaction,
                    currencyCode = currencyCode,
                    title = key.title,
                    onBack = { backStack.removeLastOrNull() },
                    onDeleteClick = onNavigateToDelete,
                    onNavigateBackWithResult = { result ->
                        resultEvent = result
                        backStack.removeLastOrNull()
                    }
                )
            }
            entry<AppSettings> {
                val viewModel = hiltViewModel<SettingsViewModel>()
                SettingsRoute(
                    viewModel = viewModel,
                    onBack = { backStack.removeLastOrNull() },
                    onPrivacyPolicyClick = onPrivacyPolicyClick,
                    onContactSupportClick = onRequestFeatureClick,
                    onReportBugClick = onReportBugClick
                )
            }
            entry<CalenderView> {
                val viewModel = hiltViewModel<CalenderViewViewModel>()
                CalenderRoute(
                    currencyCode = currencyCode,
                    viewModel = viewModel,
                    resultEvent = resultEvent,
                    onResultEventConsumed = { resultEvent = null },
                    onTransactionClick = onNavigateToEdit,
                    onLongPress = onNavigateToDelete,
                    onBack = { backStack.removeLastOrNull() }
                )
            }
            entry<DeleteTransactionRoute>(metadata = DialogSceneStrategy.dialog()) { key ->
                val viewModel = hiltViewModel<HomeViewModel>()
                val deleteScope = rememberCoroutineScope()
                DeleteTransactionDialog(
                    onDismissRequest = { backStack.removeLastOrNull() },
                    onCancelClick = {
                        Toast.makeText(context, R.string.operation_cancel, Toast.LENGTH_LONG).show()
                        backStack.removeLastOrNull()
                    },
                    onConfirmClick = {
                        deleteScope.launch {
                            val result = viewModel.deleteSingleTransaction(key.transaction)
                            val messageRes =
                                if (result.isSuccess) R.string.delete_transaction else R.string.database_error
                            Toast.makeText(context, messageRes, Toast.LENGTH_LONG).show()
                            backStack.removeLastOrNull()
                            // Deleting from the edit screen (rather than a long-press) leaves a now-stale
                            // AddTransaction screen exposed underneath the dialog — pop it too so the user
                            // lands back on whatever was below it instead of an editor for a deleted row.
                            if (result.isSuccess && backStack.lastOrNull() is AddTransaction) {
                                backStack.removeLastOrNull()
                            }
                        }
                    }
                )
            }
            entry<InsightsDetail> { key ->
                val viewModel = hiltViewModel<InsightsDetailViewModel>()
                LaunchedEffect(key) {
                    viewModel.setParams(key.kind, key.value, key.offset)
                }
                InsightsDetailRoute(
                    currencyCode = currencyCode,
                    viewModel = viewModel,
                    onBack = { backStack.removeLastOrNull() },
                    onTransactionClick = onNavigateToEdit,
                    onLongPress = onNavigateToDelete,
                    onSearchAllClick = { backStack.add(Search) }
                )
            }
            entry<Recurring> {
                val viewModel = hiltViewModel<RecurringViewModel>()
                RecurringRoute(
                    currencyCode = currencyCode,
                    viewModel = viewModel,
                    onBack = { backStack.removeLastOrNull() }
                )
            }
            entry<Search> {
                val viewModel = hiltViewModel<SearchViewModel>()
                SearchRoute(
                    currencyCode = currencyCode,
                    viewModel = viewModel,
                    onBack = { backStack.removeLastOrNull() },
                    onTransactionClick = onNavigateToEdit,
                    onLongPress = onNavigateToDelete
                )
            }
        },
        transitionSpec = navForwardTransitionSpec<NavKey>(),
        popTransitionSpec = navBackTransitionSpec<NavKey>(),
        predictivePopTransitionSpec = navPredictiveBackTransitionSpec<NavKey>()
    )
}

const val ADD_TRANSACTION_RESULT_OK = Activity.RESULT_FIRST_USER
const val EDIT_TRANSACTION_RESULT_OK = Activity.RESULT_FIRST_USER + 1
