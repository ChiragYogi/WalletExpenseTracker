package com.babacode.walletexpensetracker.ui.home


import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.babacode.walletexpensetracker.R
import com.babacode.walletexpensetracker.data.model.Transaction
import com.babacode.walletexpensetracker.data.model.TransactionType
import com.babacode.walletexpensetracker.ui.home.compose.HomeRoute
import com.babacode.walletexpensetracker.ui.theme.WalletExpenseTheme
import com.babacode.walletexpensetracker.utiles.Extra.REQUEST_KEY_FOR_ADD_EDIT
import com.babacode.walletexpensetracker.utiles.SettingUtils
import com.babacode.walletexpensetracker.utiles.applyEdgeToEdgeInsetsPadding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class HomeFragment : Fragment() {

    private val viewModel: HomeViewModel by viewModels()

    private var resultEvent by mutableStateOf<Int?>(null)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setFragmentResultListener(REQUEST_KEY_FOR_ADD_EDIT) { _, bundle ->
            resultEvent = bundle.getInt(REQUEST_KEY_FOR_ADD_EDIT)
        }

        setHasOptionsMenu(true)

        return ComposeView(requireContext()).apply {
            applyEdgeToEdgeInsetsPadding()
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                WalletExpenseTheme {
                    HomeRoute(
                        currencyCode = SettingUtils(requireContext()).getCurrencyCode(),
                        viewModel = viewModel,
                        resultEvent = resultEvent,
                        onResultEventConsumed = { resultEvent = null },
                        onAddClick = ::navigateToAdd,
                        onIncomeClick = { navigateToDetail(TransactionType.INCOME) },
                        onExpenseClick = { navigateToDetail(TransactionType.EXPENSE) },
                        onTransactionClick = ::navigateToEdit,
                        onLongPress = ::navigateToDelete
                    )
                }
            }
        }
    }

    private fun navigateToAdd() {
        val action = HomeFragmentDirections.actionHomeFragmentToAddTransactionFragment(
            null,
            getString(R.string.add_transaction_title)
        )
        findNavController().navigate(action)
    }

    private fun navigateToDetail(transactionType: TransactionType) {
        val action = HomeFragmentDirections.actionHomeFragmentToTransactionTypeFragment(transactionType)
        findNavController().navigate(action)
    }

    private fun navigateToEdit(transaction: Transaction) {
        val action = HomeFragmentDirections.actionHomeFragmentToAddTransactionFragment(
            transaction,
            getString(R.string.edit_transaction_title)
        )
        findNavController().navigate(action)
    }

    private fun navigateToDelete(transaction: Transaction) {
        val action = HomeFragmentDirections.actionGlobalDeleteTransaction(transaction)
        findNavController().navigate(action)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.detail_screen_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.analysisViewFragment -> {
                val action = HomeFragmentDirections.actionHomeFragmentToTransactionTypeFragment(null)
                findNavController().navigate(action)
            }
            R.id.calenderViewFragment -> {
                val action = HomeFragmentDirections.actionHomeFragmentToCalenderViewFragment()
                findNavController().navigate(action)
            }
            R.id.settingsFragment -> {
                val action = HomeFragmentDirections.actionHomeFragmentToSettingsFragment()
                findNavController().navigate(action)
            }
        }

        return super.onOptionsItemSelected(item)
    }
}
