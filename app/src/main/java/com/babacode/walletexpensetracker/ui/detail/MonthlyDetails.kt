package com.babacode.walletexpensetracker.ui.detail

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.babacode.walletexpensetracker.R
import com.babacode.walletexpensetracker.data.model.QueryForTransaction
import com.babacode.walletexpensetracker.data.model.Transaction
import com.babacode.walletexpensetracker.data.model.TransactionType
import com.babacode.walletexpensetracker.databinding.FragmentMonthlyDetailsBinding
import com.babacode.walletexpensetracker.ui.home.HomeAdepter
import com.babacode.walletexpensetracker.utiles.Extra
import com.babacode.walletexpensetracker.utiles.Extra.TRANSACTION_TYPE_KEY
import com.babacode.walletexpensetracker.utiles.SettingUtils
import com.babacode.walletexpensetracker.utiles.hide
import com.babacode.walletexpensetracker.utiles.show
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDate


@AndroidEntryPoint
class MonthlyDetails : Fragment(R.layout.fragment_monthly_details), HomeAdepter.OnItemClick {

    private var _binding: FragmentMonthlyDetailsBinding? = null
    private val binding get() = _binding!!
    private lateinit var mAdepter: HomeAdepter
    private val mViewModel: DetailViewViewModel by viewModels()
    private var transactionType: TransactionType? = null
    private var monthlyDate = LocalDate.now()!!
    private val currencyCode by lazy {
        SettingUtils(requireContext())
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentMonthlyDetailsBinding.bind(view)

        transactionType = arguments?.getParcelable(TRANSACTION_TYPE_KEY)

        binding.monthlyDetails.total.currencySymbol.text =
            currencyCode.getCurrencyCode()
        if (transactionType != null) {
            binding.monthlyDetails.total.transactionTypeTxt.text = transactionType.toString()
        } else {
            binding.monthlyDetails.total.transactionTypeTxt.text =
                context?.getString(R.string.thisMonth)
        }


        mAdepter = HomeAdepter(this)

        getTheDataForMonth(monthlyDate)
        binding.monthlyDetails.nextWeekAction.setOnClickListener {
            monthlyDate = monthlyDate.plusMonths(1)
            getTheDataForMonth(monthlyDate)
        }

        binding.monthlyDetails.previousWeekAction.setOnClickListener {
            monthlyDate = monthlyDate.minusMonths(1)
            getTheDataForMonth(monthlyDate)
        }
        setUpObserver()
        setUpRecyclerView()


    }


    private fun getTheDataForMonth(currentDate: LocalDate) {
        val date = Extra.getLocalDateStartEndDateMonth(currentDate)
        val monthStartDateLong = date.startDate
        val monthEndDateLong = date.endDate
        val startDateForTxt = Extra.convertDateLongToDateStringMonthly(monthStartDateLong)
        val monthQuery = QueryForTransaction(transactionType, monthStartDateLong, monthEndDateLong)
        mViewModel.getTheQueryDate(queryData = monthQuery)
        binding.monthlyDetails . dateTxtView . text = startDateForTxt

    }

    private fun setUpObserver() {
        mViewModel.allDataBetweenStartAndEndDate.observe(viewLifecycleOwner) { transactionList ->

            if (transactionList.isEmpty()) {
                binding.monthlyDetails.totalRvView.hide()
                binding.monthlyDetails.total.totalCardView.hide()
                binding.monthlyDetails.noTransactionTv.show()
            } else {
                binding.monthlyDetails.noTransactionTv.hide()
                binding.monthlyDetails.total.totalCardView.show()
                binding.monthlyDetails.totalRvView.show()
                mAdepter.submitList(transactionList)
                val total = transactionList.sumOf { transaction ->
                    transaction.amount
                }
                binding.monthlyDetails.total.expenseTotal.text = total.toString()
            }


        }

    }

    override fun OnTransactionClick(transaction: Transaction) {
        val action =
            TransactionTypeFragmentDirections.actionTransactionTypeFragmentToEditTransactionFragment(
                transaction
            )
        findNavController().navigate(action)
    }

    override fun OnLongPress(transaction: Transaction) {
        val action =
            TransactionTypeFragmentDirections.actionGlobalDeleteTransaction(
                transaction
            )
        findNavController().navigate(action)
    }


    private fun setUpRecyclerView() {
        binding.monthlyDetails.totalRvView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = mAdepter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null

    }


}