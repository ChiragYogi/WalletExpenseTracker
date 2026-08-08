package com.babacode.walletexpensetracker.ui.addedit


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.babacode.walletexpensetracker.ui.addedit.compose.AddTransactionRoute
import com.babacode.walletexpensetracker.ui.theme.WalletExpenseTheme
import com.babacode.walletexpensetracker.utiles.Extra.REQUEST_KEY_FOR_ADD_EDIT
import com.babacode.walletexpensetracker.utiles.SettingUtils
import com.babacode.walletexpensetracker.utiles.applyEdgeToEdgeInsetsPadding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddTransactionFragment : Fragment() {

    private val viewModel: TransactionAddEditViewModel by viewModels()
    private val transactionArgs: AddTransactionFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            applyEdgeToEdgeInsetsPadding()
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                WalletExpenseTheme {
                    AddTransactionRoute(
                        viewModel = viewModel,
                        editTransaction = transactionArgs.editTransaction,
                        currencyCode = SettingUtils(requireContext()).getCurrencyCode(),
                        onNavigateBackWithResult = { result ->
                            setFragmentResult(
                                REQUEST_KEY_FOR_ADD_EDIT,
                                bundleOf(REQUEST_KEY_FOR_ADD_EDIT to result)
                            )
                            findNavController().popBackStack()
                        }
                    )
                }
            }
        }
    }
}
