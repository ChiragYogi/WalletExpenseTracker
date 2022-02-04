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

        mAdepter = HomeAdepter(this)
        setUpObserver()
        setUpRecyclerView()

        getTheDataForMonth(monthlyDate)

        binding.monthlyDetails.nextWeekAction.setOnClickListener {
            monthlyDate = monthlyDate.plusMonths(1)
            getTheDataForMonth(monthlyDate)
        }

        binding.monthlyDetails.previousWeekAction.setOnClickListener {
            monthlyDate = monthlyDate.minusMonths(1)
            getTheDataForMonth(monthlyDate)
        }

    }


    private fun getTheDataForMonth(currentDate: LocalDate) {
        val date = Extra.getLocalDateStartEndDateMonth(currentDate)
        val monthStartDateLong = date.startDate
        val monthEndDateLong = date.endDate
        val startDateForTxt = Extra.convertDateLongToDateStringMonthly(monthStartDateLong)
        val monthQuery = QueryForTransaction(transactionType, monthStartDateLong, monthEndDateLong)
        mViewModel.getTheQueryDate(queryData = monthQuery)
        binding.monthlyDetails.dateTxtView.text = startDateForTxt

    }

    private fun setUpObserver() {
        mViewModel.allDataBetweenStartAndEndDate.observe(viewLifecycleOwner) { transactionList ->

            if (transactionList.isNotEmpty()) {

                showViewInMonthly(transactionList)

            } else {

                hideViewInMonthly()

            }


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
        binding.monthlyDetails.totalRvView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = mAdepter
        }
    }


    private fun showViewInMonthly(transactionList: List<Transaction>) {
        binding.monthlyDetails.apply {

            noTransactionTv.hide()

            total.totalCardView.show()
            if (transactionType != null) {
                total.transactionTypeTxt.text = transactionType.toString()
            } else {
                total.transactionTypeTxt.text =
                    context?.getString(R.string.thisMonth)
            }
            total.currencySymbol.text =
                currencyCode.getCurrencyCode()

            totalRvView.show()
            mAdepter.submitList(transactionList)
            val totalForMonth = transactionList.sumOf { transaction ->
                transaction.amount
            }
            binding.monthlyDetails.total.expenseTotal.text = totalForMonth.toString()


        }
    }

    private fun hideViewInMonthly() {
        binding.apply {
            monthlyDetails.totalRvView.hide()
            monthlyDetails.total.totalCardView.hide()
            monthlyDetails.noTransactionTv.show()
        }

    }

    override fun onDestroyView() {
        binding.monthlyDetails.totalRvView.adapter = null
        super.onDestroyView()
        _binding = null

    }


}