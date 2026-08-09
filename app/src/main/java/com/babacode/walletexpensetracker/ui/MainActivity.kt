package com.babacode.walletexpensetracker.ui

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.core.content.ContextCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
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
import com.babacode.walletexpensetracker.ui.detail.DetailViewViewModel
import com.babacode.walletexpensetracker.ui.detail.compose.TransactionTypeRoute
import com.babacode.walletexpensetracker.ui.home.HomeViewModel
import com.babacode.walletexpensetracker.ui.home.compose.DeleteTransactionDialog
import com.babacode.walletexpensetracker.ui.home.compose.HomeRoute
import com.babacode.walletexpensetracker.ui.navigation.AddTransaction
import com.babacode.walletexpensetracker.ui.navigation.CalenderView
import com.babacode.walletexpensetracker.ui.navigation.DeleteTransactionRoute
import com.babacode.walletexpensetracker.ui.navigation.Home
import com.babacode.walletexpensetracker.ui.navigation.AppSettings
import com.babacode.walletexpensetracker.ui.navigation.TransactionTypeDetail
import com.babacode.walletexpensetracker.ui.setting.SettingsViewModel
import com.babacode.walletexpensetracker.ui.setting.compose.SettingsRoute
import com.babacode.walletexpensetracker.ui.setting.notification.AlarmUtils
import com.babacode.walletexpensetracker.ui.theme.WalletExpenseTheme
import com.babacode.walletexpensetracker.data.model.TransactionType
import com.babacode.walletexpensetracker.utiles.Extra.privacy_policy_url
import com.babacode.walletexpensetracker.utiles.SettingUtils
import dagger.hilt.android.AndroidEntryPoint
import androidx.navigationevent.compose.LocalNavigationEventDispatcherOwner
import java.util.Calendar

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val settingUtilsForNotification by lazy {
        SettingUtils(this)
    }
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

        val toSetAlarm = settingUtilsForNotification.notificationForAlarm()
        if (toSetAlarm) {
            alarmUtils.initAlarmForNotification(Calendar.getInstance())
        } else {
            alarmUtils.cancelNotificationAlarm()
        }

        checkForNotificationPermission()

        setContent {
            CompositionLocalProvider(LocalNavigationEventDispatcherOwner provides this) {
                WalletExpenseTheme {
                    MainNavigation(
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
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_DENIED
        ) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    private fun openNotificationSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        intent.data = Uri.parse("package:$packageName")
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
            data = Uri.parse("mailto:")
        }
        val emailIntent = Intent(Intent.ACTION_SEND).apply {
            putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
            putExtra(Intent.EXTRA_SUBJECT, subject)
            this.selector = selectIntent
        }

        try {
            startActivity(Intent.createChooser(emailIntent, "send mail using..."))
        } catch (e: Exception) {
            Toast.makeText(this, getString(R.string.emailError), Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }

    private fun openPrivacyPolicy() {
        try {
            val intent = Intent.makeMainSelectorActivity(Intent.ACTION_MAIN, Intent.CATEGORY_APP_BROWSER)
            intent.data = Uri.parse(privacy_policy_url)
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainNavigation(
    showNotificationSnackbar: Boolean,
    onNotificationSnackbarShown: () -> Unit,
    onOpenNotificationSettings: () -> Unit,
    onReportBugClick: () -> Unit,
    onRequestFeatureClick: () -> Unit,
    onPrivacyPolicyClick: () -> Unit
) {
    val context = LocalContext.current
    val backStack = rememberNavBackStack(Home)
    val dialogSceneStrategy = remember { DialogSceneStrategy<NavKey>() }
    val snackbarHostState = remember { SnackbarHostState() }

    var resultEvent by remember { mutableStateOf<Int?>(null) }

    val notificationPermissionMessage = stringResource(R.string.notification_permission_text)
    val notificationPermissionActionLabel = stringResource(R.string.open)
    LaunchedEffect(showNotificationSnackbar) {
        if (showNotificationSnackbar) {
            val result = snackbarHostState.showSnackbar(
                message = notificationPermissionMessage,
                actionLabel = notificationPermissionActionLabel,
                duration = SnackbarDuration.Long
            )
            if (result == SnackbarResult.ActionPerformed) {
                onOpenNotificationSettings()
            }
            onNotificationSnackbarShown()
        }
    }

    val addTransactionTitle = stringResource(R.string.add_transaction_title)
    val editTransactionTitle = stringResource(R.string.edit_transaction_title)

    val onNavigateToEdit: (com.babacode.walletexpensetracker.data.model.Transaction) -> Unit = { transaction ->
        backStack.add(AddTransaction(transaction, editTransactionTitle))
    }
    val onNavigateToDelete: (com.babacode.walletexpensetracker.data.model.Transaction) -> Unit = { transaction ->
        backStack.add(DeleteTransactionRoute(transaction))
    }

    val currentRoute = backStack.lastOrNull { it !is DeleteTransactionRoute } ?: Home

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            AppTopBar(
                currentRoute = currentRoute,
                onBack = { backStack.removeLastOrNull() },
                onOpenAnalysis = { backStack.add(TransactionTypeDetail(null)) },
                onOpenCalender = { backStack.add(CalenderView) },
                onOpenSettings = { backStack.add(AppSettings) }
            )
        }
    ) { innerPadding ->
        NavDisplay(
            backStack = backStack,
            onBack = { backStack.removeLastOrNull() },
            modifier = Modifier.padding(innerPadding),
            sceneStrategies = listOf(dialogSceneStrategy),
            entryDecorators = listOf(
                rememberSaveableStateHolderNavEntryDecorator(),
                rememberViewModelStoreNavEntryDecorator()
            ),
            entryProvider = entryProvider {
                entry<Home> {
                    val viewModel = hiltViewModel<HomeViewModel>()
                    HomeRoute(
                        currencyCode = SettingUtils(context).getCurrencyCode(),
                        viewModel = viewModel,
                        resultEvent = resultEvent,
                        onResultEventConsumed = { resultEvent = null },
                        onAddClick = { backStack.add(AddTransaction(null, addTransactionTitle)) },
                        onIncomeClick = { backStack.add(TransactionTypeDetail(TransactionType.INCOME)) },
                        onExpenseClick = { backStack.add(TransactionTypeDetail(TransactionType.EXPENSE)) },
                        onTransactionClick = onNavigateToEdit,
                        onLongPress = onNavigateToDelete
                    )
                }
                entry<AddTransaction> { key ->
                    val viewModel = hiltViewModel<TransactionAddEditViewModel>()
                    AddTransactionRoute(
                        viewModel = viewModel,
                        editTransaction = key.editTransaction,
                        currencyCode = SettingUtils(context).getCurrencyCode(),
                        onNavigateBackWithResult = { result ->
                            resultEvent = result
                            backStack.removeLastOrNull()
                        }
                    )
                }
                entry<TransactionTypeDetail> { key ->
                    val viewModel = hiltViewModel<DetailViewViewModel>()
                    LaunchedEffect(key.transactionType) {
                        viewModel.setTransactionType(key.transactionType)
                    }
                    TransactionTypeRoute(
                        transactionType = key.transactionType,
                        currencyCode = SettingUtils(context).getCurrencyCode(),
                        viewModel = viewModel,
                        resultEvent = resultEvent,
                        onResultEventConsumed = { resultEvent = null },
                        onTransactionClick = onNavigateToEdit,
                        onLongPress = onNavigateToDelete
                    )
                }
                entry<AppSettings> {
                    val viewModel = hiltViewModel<SettingsViewModel>()
                    SettingsRoute(
                        viewModel = viewModel,
                        onPrivacyPolicyClick = onPrivacyPolicyClick,
                        onContactSupportClick = onRequestFeatureClick,
                        onReportBugClick = onReportBugClick
                    )
                }
                entry<CalenderView> {
                    val viewModel = hiltViewModel<CalenderViewViewModel>()
                    CalenderRoute(
                        currencyCode = SettingUtils(context).getCurrencyCode(),
                        viewModel = viewModel,
                        resultEvent = resultEvent,
                        onResultEventConsumed = { resultEvent = null },
                        onTransactionClick = onNavigateToEdit,
                        onLongPress = onNavigateToDelete
                    )
                }
                entry<DeleteTransactionRoute>(metadata = DialogSceneStrategy.dialog()) { key ->
                    val viewModel = hiltViewModel<HomeViewModel>()
                    DeleteTransactionDialog(
                        onDismissRequest = { backStack.removeLastOrNull() },
                        onCancelClick = {
                            Toast.makeText(context, R.string.operation_cancel, Toast.LENGTH_LONG).show()
                            backStack.removeLastOrNull()
                        },
                        onConfirmClick = {
                            viewModel.deleteSingleTransaction(key.transaction)
                            Toast.makeText(context, R.string.delete_transaction, Toast.LENGTH_LONG).show()
                            backStack.removeLastOrNull()
                        }
                    )
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppTopBar(
    currentRoute: NavKey,
    onBack: () -> Unit,
    onOpenAnalysis: () -> Unit,
    onOpenCalender: () -> Unit,
    onOpenSettings: () -> Unit
) {
    val title = when (currentRoute) {
        is Home -> "Home"
        is AddTransaction -> currentRoute.title
        is TransactionTypeDetail -> "Detail View"
        is AppSettings -> stringResource(R.string.setting)
        is CalenderView -> stringResource(R.string.calender)
        else -> ""
    }
    TopAppBar(
        title = { Text(title) },
        navigationIcon = {
            if (currentRoute != Home) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                }
            }
        },
        actions = {
            if (currentRoute == Home) {
                IconButton(onClick = onOpenAnalysis) {
                    Icon(
                        painter = painterResource(R.drawable.ic_baseline_bar_chart_24),
                        contentDescription = stringResource(R.string.calender)
                    )
                }
                IconButton(onClick = onOpenCalender) {
                    Icon(
                        painter = painterResource(R.drawable.yearly_calender),
                        contentDescription = stringResource(R.string.calender)
                    )
                }
                IconButton(onClick = onOpenSettings) {
                    Icon(
                        painter = painterResource(R.drawable.setting_vector),
                        contentDescription = stringResource(R.string.setting_icon)
                    )
                }
            }
        }
    )
}

const val ADD_TRANSACTION_RESULT_OK = Activity.RESULT_FIRST_USER
const val EDIT_TRANSACTION_RESULT_OK = Activity.RESULT_FIRST_USER + 1
