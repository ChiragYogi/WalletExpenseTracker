package com.babacode.walletexpensetracker.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
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
import androidx.navigation.fragment.navArgs
import com.babacode.walletexpensetracker.R
import com.babacode.walletexpensetracker.data.model.Transaction
import com.babacode.walletexpensetracker.ui.detail.compose.TransactionTypeRoute
import com.babacode.walletexpensetracker.ui.theme.WalletExpenseTheme
import com.babacode.walletexpensetracker.utiles.Extra.REQUEST_KEY_FOR_ADD_EDIT
import com.babacode.walletexpensetracker.utiles.SettingUtils
import com.babacode.walletexpensetracker.utiles.applyEdgeToEdgeInsetsPadding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TransactionTypeFragment : Fragment() {

    private val transactionTypeArgs: TransactionTypeFragmentArgs by navArgs()
    private val viewModel: DetailViewViewModel by viewModels()

    private var resultEvent by mutableStateOf<Int?>(null)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel.setTransactionType(transactionTypeArgs.transactionType)

        setFragmentResultListener(REQUEST_KEY_FOR_ADD_EDIT) { _, bundle ->
            resultEvent = bundle.getInt(REQUEST_KEY_FOR_ADD_EDIT)
        }

        return ComposeView(requireContext()).apply {
            applyEdgeToEdgeInsetsPadding()
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                WalletExpenseTheme {
                    TransactionTypeRoute(
                        transactionType = transactionTypeArgs.transactionType,
                        currencyCode = SettingUtils(requireContext()).getCurrencyCode(),
                        viewModel = viewModel,
                        resultEvent = resultEvent,
                        onResultEventConsumed = { resultEvent = null },
                        onTransactionClick = ::navigateToEdit,
                        onLongPress = ::navigateToDelete
                    )
                }
            }
        }
    }

    private fun navigateToEdit(transaction: Transaction) {
        val action = TransactionTypeFragmentDirections
            .actionTransactionTypeFragmentToAddTransactionFragment(
                transaction,
                getString(R.string.edit_transaction_title)
            )
        findNavController().navigate(action)
    }

    private fun navigateToDelete(transaction: Transaction) {
        val action = TransactionTypeFragmentDirections.actionGlobalDeleteTransaction(transaction)
        findNavController().navigate(action)
    }
}
