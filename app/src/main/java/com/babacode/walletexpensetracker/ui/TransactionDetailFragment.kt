package com.babacode.walletexpensetracker.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.babacode.walletexpensetracker.R
import com.babacode.walletexpensetracker.databinding.FragmentTransactionDetailBinding


class TransactionDetailFragment : Fragment(R.layout.fragment_transaction_detail) {


    private var _binding: FragmentTransactionDetailBinding ? = null
    private val binding get() = _binding!!
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentTransactionDetailBinding.bind(view)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}