package com.babacode.walletexpensetracker.ui.home

import android.app.Dialog
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.babacode.walletexpensetracker.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class DeleteTransaction : DialogFragment() {

    private val viewModel: HomeViewModel by viewModels()
    private val deleteArgs: DeleteTransactionArgs by navArgs()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.delete_this_transaction)
            .setMessage(R.string.delete_this_transaction_msg)
            .setIcon(R.drawable.delete_vector)
            .setNegativeButton(getString(R.string.cancle_button)) { dialog, _ ->
                dialog.dismiss()
                Toast.makeText(requireContext(), R.string.operation_cancel, Toast.LENGTH_LONG)
                    .show()
            }
            .setPositiveButton(getString(R.string.yes_button)) { _, _ ->
                viewModel.deleteSingleTransaction(deleteArgs.transactionDelete)
                Toast.makeText(requireContext(), R.string.delete_transaction, Toast.LENGTH_LONG)
                    .show()

            }
            .create()


}