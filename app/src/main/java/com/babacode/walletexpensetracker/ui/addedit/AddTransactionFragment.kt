package com.babacode.walletexpensetracker.ui.addedit


import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.core.os.bundleOf
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doAfterTextChanged
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
import com.babacode.walletexpensetracker.databinding.FragmentAddTransactionBinding
import com.babacode.walletexpensetracker.utiles.Extra.convertDateLongToDateString
import com.babacode.walletexpensetracker.utiles.Extra.currentDayDate
import com.babacode.walletexpensetracker.utiles.transformDatePicker
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.*

@AndroidEntryPoint
class AddTransactionFragment : Fragment(R.layout.fragment_add_transaction) {

    private var _binding: FragmentAddTransactionBinding? = null
    private val binding get() = _binding!!
    private val viewModel: TransactionAddEditViewModel by viewModels()
    private val transactionFragmentArgs: AddTransactionFragmentArgs by navArgs()
    private var transaction: Transaction? = null


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAddTransactionBinding.bind(view)


        transaction = transactionFragmentArgs.transaction

        setDataToUi()
        initView()

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {

                viewModel.addEditTransactionEvent.collect { event ->
                    when (event) {
                        is TransactionAddEditViewModel.AddEditTransactionEvent.NavigateBackWithResult -> {
                            setFragmentResult(
                                "add_edit_request",
                                bundleOf("add_edit_request" to event.result)
                            )
                            findNavController().popBackStack()
                        }
                        is TransactionAddEditViewModel.AddEditTransactionEvent.ShowInvalidAmount -> {
                            binding.amountEdt.error = event.msg
                        }
                        is TransactionAddEditViewModel.AddEditTransactionEvent.ShowInvalidNote -> {
                            binding.transactionNoteEdt.error = event.msg
                        }
                        is TransactionAddEditViewModel.AddEditTransactionEvent.ShowSelectTransactionPaymentMode -> {
                            Snackbar.make(requireView(), event.msg, Snackbar.LENGTH_SHORT).show()
                        }
                        is TransactionAddEditViewModel.AddEditTransactionEvent.ShowSelectTransactionTag -> {
                            Snackbar.make(requireView(), event.msg, Snackbar.LENGTH_SHORT).show()
                        }
                        is TransactionAddEditViewModel.AddEditTransactionEvent.ShowSelectTransactionType -> {
                            Snackbar.make(requireView(), event.msg, Snackbar.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }

    private fun setDataToUi() {

        transaction?.let { transactionDetail ->

            binding.transactionTypeLayout.editText?.setText(transactionDetail.transactionType.toString())
            binding.transactionModeLayout.editText?.setText(transactionDetail.paymentType.toString())
            binding.transactionTagLayout.editText?.setText(transactionDetail.tag.toString())
            binding.transactionNoteEdt.setText(transactionDetail.note)
            binding.amountEdt.setText(transactionDetail.amount.toInt().toString())
            binding.dateEdt.setText(convertDateLongToDateString(transactionDetail.date))

        }


    }

    private fun initView() {


        binding.apply {

            //adepter for transaction type
            val transactionTypeAdepter = ArrayAdapter(
                requireContext(),
                R.layout.item_auto_complete_dropdown, TransactionType.values()
            )

            (binding.transactionTypeLayout.editText as? AutoCompleteTextView)?.setAdapter(
                transactionTypeAdepter
            )


            val tagAdepter = ArrayAdapter(
                requireContext(),
                R.layout.item_auto_complete_dropdown, TransactionTag.values()
            )

            (binding.transactionTagLayout.editText as? AutoCompleteTextView)?.setAdapter(tagAdepter)


            //adepter for transaction type
            val transactionModeAdepter = ArrayAdapter(
                requireContext(),
                R.layout.item_auto_complete_dropdown, PaymentType.values()
            )

            (binding.transactionModeLayout.editText as? AutoCompleteTextView)?.setAdapter(
                transactionModeAdepter
            )


        /*    val todayDate = currentDayDate()

            dateEdt.setText(convertDateLongToDateString(todayDate))*/
            dateEdt.transformDatePicker(requireContext(), "dd/MM/yyyy", Date())

            amountEdt.doAfterTextChanged {
                amountTxtInputLayout.error = null
            }
            transactionNoteEdt.doAfterTextChanged {
                transactionNoteLayout.error = null
            }


            saveTransaction.setOnClickListener{
                saveOrUpdateTransaction()
            }

        }



    }

    private fun saveOrUpdateTransaction() {

        val note = binding.transactionNoteEdt.text.toString().trim { it <= ' ' }
        val amount = binding.amountEdt.text.toString().trim { it <= ' ' }
        val transactionType = binding.transactionTypeLayout.editText?.text.toString()
        val transactionTag = binding.transactionTagLayout.editText?.text.toString()
        val transactionMode = binding.transactionModeLayout.editText?.text.toString()
        val date = binding.dateEdt.text.toString()

        var transactionId = 0

        transaction?.let {
            if (it.id != 0) {
                transactionId = it.id
            }
        }


        viewModel.validateAndInsertOrUpdate(
            note,
            date,
            transactionType,
            amount,
            transactionTag,
            transactionMode,
            transactionId
        )


    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}



