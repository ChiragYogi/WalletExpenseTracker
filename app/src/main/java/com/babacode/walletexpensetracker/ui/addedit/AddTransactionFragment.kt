package com.babacode.walletexpensetracker.ui.addedit

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.babacode.walletexpensetracker.R
import com.babacode.walletexpensetracker.data.model.Transaction
import com.babacode.walletexpensetracker.databinding.FragmentAddTransactionBinding
import com.babacode.walletexpensetracker.utiles.Extra.convertDateToLong
import com.babacode.walletexpensetracker.utiles.Extra.transactionPayment
import com.babacode.walletexpensetracker.utiles.Extra.transactionTag


import com.babacode.walletexpensetracker.utiles.Extra.transactionType
import com.babacode.walletexpensetracker.utiles.parseDouble
import com.babacode.walletexpensetracker.utiles.transformDatePicker
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class AddTransactionFragment : Fragment(R.layout.fragment_add_transaction) {

    private var _binding: FragmentAddTransactionBinding? = null
    private val binding get() = _binding!!
    private val addEditViewModel: TransactionAddEditViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAddTransactionBinding.bind(view)

        initView()
    }


    private fun initView() {
        //adepter for transaction type
        val transactionTypeAdepter = ArrayAdapter(
            requireContext(),
            R.layout.item_auto_complete_dropdown, transactionType
        )
        binding.transactionTypeEdt.setAdapter(transactionTypeAdepter)

        //adepter for transaction tag
        val tagAdepter = ArrayAdapter(
            requireContext(),
            R.layout.item_auto_complete_dropdown, transactionTag
        )
        binding.transactionTagEdt.setAdapter(tagAdepter)


        //adepter for transaction type
        val transactionModeAdepter = ArrayAdapter(
            requireContext(),
            R.layout.item_auto_complete_dropdown, transactionPayment
        )
        binding.transactionModeEdt.setAdapter(transactionModeAdepter)

        binding.dateEdt.transformDatePicker(requireContext(), "dd/MM/yyyy", Date())


        binding.saveTransaction.setOnClickListener {
            insertTransactionInDatabase()
        }


    }

    private fun insertTransactionInDatabase() {
        addEditViewModel.insertTransaction(saveTransaction())
        findNavController().navigate(R.id.action_addTransactionFragment_to_homeFragment)
    }


    private fun saveTransaction(): Transaction = binding.let {
        val note = binding.transactionNoteEdt.text.toString()
        val dateFromEditText = binding.dateEdt.text.toString()
        val date = convertDateToLong(dateFromEditText)
        val amount = parseDouble(binding.amountEdt.text.toString())
        val transactionType = binding.transactionTypeEdt.text.toString()
        val transactionTag = binding.transactionTagEdt.text.toString()
        val transactionMode = binding.transactionModeEdt.text.toString()
        return Transaction(note, date, transactionType, amount, transactionTag, transactionMode)

    }




    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}



