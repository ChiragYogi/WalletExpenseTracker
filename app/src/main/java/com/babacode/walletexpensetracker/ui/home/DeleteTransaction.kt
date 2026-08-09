package com.babacode.walletexpensetracker.ui.home

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.babacode.walletexpensetracker.R
import com.babacode.walletexpensetracker.ui.home.compose.DeleteTransactionDialog
import com.babacode.walletexpensetracker.ui.theme.WalletExpenseTheme
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class DeleteTransaction : DialogFragment() {

    private val viewModel: HomeViewModel by viewModels()
    private val deleteArgs: DeleteTransactionArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                WalletExpenseTheme {
                    DeleteTransactionDialog(
                        onDismissRequest = { dismiss() },
                        onCancelClick = {
                            Toast.makeText(requireContext(), R.string.operation_cancel, Toast.LENGTH_LONG).show()
                            dismiss()
                        },
                        onConfirmClick = {
                            viewModel.deleteSingleTransaction(deleteArgs.transactionDelete)
                            Toast.makeText(requireContext(), R.string.delete_transaction, Toast.LENGTH_LONG).show()
                            dismiss()
                        }
                    )
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }
}
