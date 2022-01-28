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
import com.babacode.walletexpensetracker.R
import com.babacode.walletexpensetracker.data.model.PaymentType
import com.babacode.walletexpensetracker.data.model.TransactionTag
import com.babacode.walletexpensetracker.data.model.TransactionType
import com.babacode.walletexpensetracker.databinding.FragmentAddTransactionBinding
import com.babacode.walletexpensetracker.utiles.Extra.convertLongDateToStringDate
import com.babacode.walletexpensetracker.utiles.Extra.currentDayDate
import com.babacode.walletexpensetracker.utiles.SettingUtils
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



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAddTransactionBinding.bind(view)

        initView()
        if (savedInstanceState != null) {
            val type = savedInstanceState.getString("typeInEdit")
            val tag = savedInstanceState.getString("tagInEdit")
            val mode = savedInstanceState.getString("modeInEdit")

            binding.apply {
                addTransaction.typeEdt.setText(type, false)
                addTransaction.tagEdt.setText(tag, false)
                addTransaction.paymentEdt.setText(mode, false)
            }
        }
        eventObserver()

    }



    private fun initView() {

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

        with(binding) {
            addTransaction.typeEdt.setAdapter(typeAdepter)
            addTransaction.tagEdt.setAdapter(tagAdepter)
            addTransaction.paymentEdt.setAdapter(modeAdepter)

            val currencyCode = SettingUtils(requireContext())
            addTransaction.amountTxtInputLayout.prefixText =
                currencyCode.getCurrencyCode()

            val todayDate = currentDayDate()

            addTransaction.dateEdt.setText(convertLongDateToStringDate(todayDate))


            addTransaction.dateEdt.transformDatePicker(requireContext(), "dd MMM, yyyy", Date())

            saveTransaction.setOnClickListener {
                saveTransactionData()
            }
        }


    }


    private fun saveTransactionData() = binding.addTransaction.let {


        val note = it.transactionNoteEdt.text.toString().trim { note -> note <= ' ' }
        val amount = it.amountEdt.text.toString().trim { amount -> amount <= ' ' }
        val date = it.dateEdt.text.toString()
        val transactionType = it.transactionTypeLayout.editText?.text.toString()
        val transactionTag = it.transactionTagLayout.editText?.text.toString()
        val transactionPaymentType = it.transactionModeLayout.editText?.text.toString()
        val transactionId = 0


        viewModel.validateAndInsertOrUpdate(
            transactionType,
            amount,
            note,
            date,
            transactionTag,
            transactionPaymentType,
            transactionId
        )


    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    // event observer with flow channel and viewModel
    private fun eventObserver() {
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.addEditTransactionEvent.collect{ event ->
                    when (event) {
                        is TransactionAddEditViewModel.AddEditTransactionEvent.NavigateBackWithResult -> {
                            setFragmentResult(
                                "add_edit_request",
                                bundleOf("add_edit_request" to event.result)
                            )
                            findNavController().popBackStack()
                        }
                        is TransactionAddEditViewModel.AddEditTransactionEvent.ShowInvalidAmount -> {
                            binding.addTransaction.amountEdt.error = event.msg
                            binding.addTransaction.amountTxtInputLayout.boxStrokeErrorColor
                            Snackbar.make(requireView(), event.msg, Snackbar.LENGTH_LONG).show()
                        }
                        is TransactionAddEditViewModel.AddEditTransactionEvent.ShowInvalidNote -> {
                            binding.addTransaction.transactionNoteEdt.error = event.msg
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


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        val type = binding.addTransaction.typeEdt.text.toString()
        val tag = binding.addTransaction.tagEdt.text.toString()
        val mode = binding.addTransaction.paymentEdt.text.toString()
        outState.putString("type", type)
        outState.putString("tag", tag)
        outState.putString("mode", mode)

    }


}




