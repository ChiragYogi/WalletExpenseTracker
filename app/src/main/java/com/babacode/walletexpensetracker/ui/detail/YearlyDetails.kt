package com.babacode.walletexpensetracker.ui.detail

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.babacode.walletexpensetracker.R
import com.babacode.walletexpensetracker.data.model.QueryForTransaction
import com.babacode.walletexpensetracker.data.model.Transaction
import com.babacode.walletexpensetracker.data.model.TransactionType
import com.babacode.walletexpensetracker.databinding.FragmentYearlyDetailsBinding
import com.babacode.walletexpensetracker.ui.home.HomeAdepter
import com.babacode.walletexpensetracker.utiles.*
import com.babacode.walletexpensetracker.utiles.Extra.TRANSACTION_TYPE_KEY
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDate


@AndroidEntryPoint
class YearlyDetails : Fragment(R.layout.fragment_yearly_details), HomeAdepter.OnItemClick {

    private var _binding: FragmentYearlyDetailsBinding? = null
    private val binding get() = _binding!!
    private lateinit var mAdepter: HomeAdepter
    private val mViewModel: DetailViewViewModel by viewModels()
    private var transactionType: TransactionType? = null
    private var yearlyDate = LocalDate.now()!!
    private val currencyCode by lazy {
        SettingUtils(requireContext())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentYearlyDetailsBinding.bind(view)

        transactionType = arguments?.getParcelable(TRANSACTION_TYPE_KEY)


        mAdepter = HomeAdepter(this)
        setUpObserver()
        setUpRecyclerView()

        getTheDataForYear(yearlyDate)

        binding.yearlyDetails.nextWeekAction.setOnClickListener {
            yearlyDate = yearlyDate.plusYears(1)
            getTheDataForYear(yearlyDate)

        }
        binding.yearlyDetails.previousWeekAction.setOnClickListener {

            yearlyDate = yearlyDate.minusYears(1)
            getTheDataForYear(yearlyDate)

        }


    }


    private fun getTheDataForYear(nowDate: LocalDate) {
        val date = Extra.getLocalDateStartEndDateYear(nowDate)
        val yearStartDate = date.startDate
        val yearEndDate = date.endDate
        val startDateForTxt = Extra.convertDateLongToDateStringYearly(yearStartDate)
        val newQuery = QueryForTransaction(transactionType, yearStartDate, yearEndDate)
        mViewModel.getTheQueryDate(queryData = newQuery)
        binding.yearlyDetails.dateTxtView.text = startDateForTxt
    }


    private fun setUpObserver() {

        mViewModel.allDataBetweenStartAndEndDate.observe(
            viewLifecycleOwner
        )
        { transactionList ->

            if (transactionList.isNotEmpty()) {

                showViewInYearly(transactionList)

            } else {

                hideViewInYearly()

            }


        }
    }

    private fun showViewInYearly(transactionList: List<Transaction>) {
        binding.yearlyDetails.apply {
            noTransactionTv.hide()

            total.totalCardView.show()
            val totalForYear = transactionList.sumOf { transaction ->
                transaction.amount
            }
            total.expenseTotal.text = totalForYear.toString()

            total.currencySymbol.text =
                currencyCode.getCurrencyCode()
            if (transactionType != null) {
                total.transactionTypeTxt.text = transactionType.toString()
            } else {
                total.transactionTypeTxt.text =
                    context?.getString(R.string.thisYear)
            }

            totalRvView.show()
            mAdepter.submitList(transactionList)

        }

    }

    private fun hideViewInYearly() {
        binding.yearlyDetails.apply {
            totalRvView.hide()
            total.totalCardView.hide()
            noTransactionTv.show()
        }
    }

    override fun onTransactionClick(transaction: Transaction) {
        val action =
            TransactionTypeFragmentDirections.actionTransactionTypeFragmentToAddTransactionFragment(
                transaction,
                getString(
                    R.string.edit_transaction_title
                )
            )
        findNavController().navigate(action)
    }

    override fun onLongPress(transaction: Transaction) {
        val action =
            TransactionTypeFragmentDirections.actionGlobalDeleteTransaction(
                transaction
            )
        findNavController().navigate(action)
    }


    private fun setUpRecyclerView() {
        binding.yearlyDetails.totalRvView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = mAdepter
        }
    }

    override fun onDestroyView() {
        binding.yearlyDetails.totalRvView.adapter = null
        super.onDestroyView()
        _binding = null

    }


}