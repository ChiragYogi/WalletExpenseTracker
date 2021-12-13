package com.babacode.walletexpensetracker.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.babacode.walletexpensetracker.R
import com.babacode.walletexpensetracker.databinding.FragmentTransactionDetailBinding
import com.babacode.walletexpensetracker.ui.home.HomeViewModel
import com.babacode.walletexpensetracker.utiles.Extra.convertDateToLong
import com.babacode.walletexpensetracker.utiles.Extra.convertLongToTime
import com.babacode.walletexpensetracker.utiles.exhaustive
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect



@AndroidEntryPoint
class TransactionDetailFragment : Fragment(R.layout.fragment_transaction_detail) {


    private var _binding: FragmentTransactionDetailBinding ? = null
    private val binding get() = _binding!!
    private val args: TransactionDetailFragmentArgs by navArgs()

    private val viewModel: HomeViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentTransactionDetailBinding.bind(view)

        val transaction = args.transactionArgs

        binding.apply {
            transactionTypeResultTxt.text = transaction?.transactionType ?: ""
            transactionAmountResultTxt.text = transaction?.amount.toString() ?: ""
            val date = transaction?.date
            transactionDateResultTxt.text = date?.let { convertLongToTime(it) }
            transactionNoteResultTxt.text = transaction?.title ?: ""
            transactionTagResultTxt.text = transaction?.tag ?: ""
            transactionWhenCreatedResultTxt.text = transaction?.createdAtDate ?: ""
        }

        binding.editTransactionButton.setOnClickListener {

            viewModel.onEditTransactionClick(transaction= transaction)

        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.transactionEditEvent.collect { event ->

                when(event){
                   is HomeViewModel.TransactionEditEvent.NavigateToEditTransaction -> {
                       val action = TransactionDetailFragmentDirections.
                       actionTransactionDetailFragmentToAddTransactionFragment(transaction,"Edit Transaction")
                       findNavController().navigate(action)
                   }
                }

            }
        }.exhaustive


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}