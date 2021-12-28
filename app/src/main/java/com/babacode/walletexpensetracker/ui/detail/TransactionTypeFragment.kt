package com.babacode.walletexpensetracker.ui.detail

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.babacode.walletexpensetracker.R
import com.babacode.walletexpensetracker.data.model.Transaction
import com.babacode.walletexpensetracker.databinding.FragmentTransactionTypeBinding
import com.babacode.walletexpensetracker.ui.home.HomeAdepter
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class TransactionTypeFragment : Fragment(R.layout.fragment_transaction_type),
    HomeAdepter.OnItemClick {


    private var _binding: FragmentTransactionTypeBinding? = null
    private val binding get() = _binding!!
    private val mAdepter = HomeAdepter(this)
    private val transactionTypeArgs: TransactionTypeFragmentArgs by navArgs()

    private val mViewModel: DetailViewViewModel by viewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentTransactionTypeBinding.bind(view)

        mViewModel.getTransactionByTransactionType(transactionTypeArgs.trnasactionType)
        Log.d("tran",transactionTypeArgs.trnasactionType.toString())
        setUpRecyclerView()
        setUpObserver()


    }

    private fun setUpObserver() {
        mViewModel.allTransaction.observe(viewLifecycleOwner, Observer { transactionList ->

            mAdepter.submitList(transactionList)
            Log.d("tran",transactionList.toString())

            binding.total.expenseTextTitle.text = "Total ${transactionTypeArgs.trnasactionType}"
            binding.total.expenseTotal.text = transactionList.sumOf {
                it.amount
            }.toString()



        })
    }


    private fun setUpRecyclerView() {
        binding.totalRvView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = mAdepter
            setHasFixedSize(true)
        }
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun OnTransactionClick(transaction: Transaction) {
        // do nothing in detail screen
    }

    override fun OnLongPress(transaction: Transaction) {
       // do nothing in detail screen
    }
}