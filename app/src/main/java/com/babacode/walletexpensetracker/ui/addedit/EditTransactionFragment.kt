package com.babacode.walletexpensetracker.ui.addedit

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.babacode.walletexpensetracker.R
import com.babacode.walletexpensetracker.data.model.PaymentType
import com.babacode.walletexpensetracker.data.model.Transaction
import com.babacode.walletexpensetracker.data.model.TransactionTag
import com.babacode.walletexpensetracker.data.model.TransactionType
import com.babacode.walletexpensetracker.databinding.FragmentEditTransactionBinding
import com.babacode.walletexpensetracker.utiles.Extra
import com.babacode.walletexpensetracker.utiles.SettingUtils
import com.babacode.walletexpensetracker.utiles.transformDatePicker
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.*


@AndroidEntryPoint
class EditTransactionFragment : Fragment(R.layout.fragment_edit_transaction) {


    private var _binding: FragmentEditTransactionBinding? = null
    private val binding get() = _binding!!
    private val mViewModel: TransactionAddEditViewModel by viewModels()

    private val args: EditTransactionFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentEditTransactionBinding.bind(view)


        val transaction = args.transactionArgs
        initView()
        setDataToUi(transaction)
        if (savedInstanceState != null) {
            val type = savedInstanceState.getString("typeInEdit")
            val tag = savedInstanceState.getString("tagInEdit")
            val mode = savedInstanceState.getString("modeInEdit")

            binding.apply {
                editTransaction.typeEdt.setText(type, false)
                editTransaction.tagEdt.setText(tag, false)
                editTransaction.paymentEdt.setText(mode, false)
            }
        }
        eventObserver()
    }


    private fun eventObserver() {
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                mViewModel.addEditTransactionEvent.collect { event ->
                    when (event) {
                        is TransactionAddEditViewModel.AddEditTransactionEvent.NavigateBackWithResult -> {
                            setFragmentResult(
                                "add_edit_request",
                                bundleOf("add_edit_request" to event.result)
                            )
                            findNavController().popBackStack()
                        }
                        is TransactionAddEditViewModel.AddEditTransactionEvent.ShowInvalidAmount -> {
                            binding.editTransaction.amountEdt.error = event.msg
                            binding.editTransaction.amountTxtInputLayout.boxStrokeErrorColor
                            Snackbar.make(requireView(), event.msg, Snackbar.LENGTH_LONG).show()
                        }
                        is TransactionAddEditViewModel.AddEditTransactionEvent.ShowInvalidNote -> {
                            binding.editTransaction.transactionNoteEdt.error = event.msg
                            Snackbar.make(requireView(), event.msg, Snackbar.LENGTH_LONG).show()
                        }
                        is TransactionAddEditViewModel.AddEditTransactionEvent.ShowSelectTransactionPaymentMode -> {
                            Snackbar.make(requireView(), event.msg, Snackbar.LENGTH_LONG).show()
                        }
                        is TransactionAddEditViewModel.AddEditTransactionEvent.ShowSelectTransactionTag -> {
                            Snackbar.make(requireView(), event.msg, Snackbar.LENGTH_LONG).show()
                        }
                        is TransactionAddEditViewModel.AddEditTransactionEvent.ShowSelectTransactionType -> {
                            Snackbar.make(requireView(), event.msg, Snackbar.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }
    }


    private fun setDataToUi(transaction: Transaction?) = with(binding.editTransaction) {
        if (transaction != null) {
            typeEdt.setText(transaction.transactionType.toString(), false)
            paymentEdt.setText(transaction.paymentType.toString(), false)
            tagEdt.setText(transaction.tag.toString(), false)
            transactionNoteEdt.setText(transaction.note)
            amountEdt.setText(transaction.amount.toInt().toString())
            dateEdt.setText(Extra.convertLongDateToStringDate(transaction.date))
        }
    }

    private fun initView() {

        binding.apply {
            val typeAdepter = ArrayAdapter(
                requireContext(),
                R.layout.item_auto_complete_dropdown,
                TransactionType.values()
            )

            val tagAdepter = ArrayAdapter(
                requireContext(),
                R.layout.item_auto_complete_dropdown,
                TransactionTag.values()
            )

            val modeAdepter = ArrayAdapter(
                requireContext(),
                R.layout.item_auto_complete_dropdown,
                PaymentType.values()
            )

            editTransaction.typeEdt.setAdapter(typeAdepter)
            editTransaction.tagEdt.setAdapter(tagAdepter)
            editTransaction.paymentEdt.setAdapter(modeAdepter)

            editTransaction.dateEdt.transformDatePicker(requireContext(), "dd MMM, yyyy", Date())
            val currencyCode = SettingUtils(requireContext())
            editTransaction.amountTxtInputLayout.prefixText =
                currencyCode.getCurrencyCode()

            updateTransaction.setOnClickListener {
                updateTransactionData()
            }


        }

    }


    private fun updateTransactionData() = with(binding.editTransaction) {

        val note = transactionNoteEdt.text.toString().trim { it <= ' ' }
        val amount = amountEdt.text.toString().trim { it <= ' ' }
        val date = dateEdt.text.toString()
        val transactionType = typeEdt.text.toString()
        val transactionTag = tagEdt.text.toString()
        val transactionPaymentType = paymentEdt.text.toString()
        val transactionId = args.transactionArgs.id


        mViewModel.validateAndInsertOrUpdate(
            transactionType,
            amount,
            note,
            date,
            transactionTag,
            transactionPaymentType,
            transactionId
        )

    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        val type = binding.editTransaction.typeEdt.text.toString()
        val tag = binding.editTransaction.tagEdt.text.toString()
        val mode = binding.editTransaction.paymentEdt.text.toString()
        outState.putString("typeInEdit", type)
        outState.putString("tagInEdit", tag)
        outState.putString("modeInEdit", mode)

    }

}