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
import com.babacode.walletexpensetracker.databinding.FragmentWeeklyDetailsBinding
import com.babacode.walletexpensetracker.ui.home.HomeAdepter
import com.babacode.walletexpensetracker.utiles.Extra.TRANSACTION_TYPE_KEY
import com.babacode.walletexpensetracker.utiles.Extra.convertDateLongToDateStringWeekly
import com.babacode.walletexpensetracker.utiles.Extra.getLocalDateStartEndDateWeek
import com.babacode.walletexpensetracker.utiles.SettingUtils
import com.babacode.walletexpensetracker.utiles.hide
import com.babacode.walletexpensetracker.utiles.show
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDate


@AndroidEntryPoint
class WeeklyDetails : Fragment(R.layout.fragment_weekly_details), HomeAdepter.OnItemClick {

    private var _binding: FragmentWeeklyDetailsBinding? = null
    private val binding get() = _binding!!
    private lateinit var mAdepter: HomeAdepter
    private val mViewModel: DetailViewViewModel by viewModels()
    private var transactionType: TransactionType? = null
    private var weeklyDate = LocalDate.now()!!
    private val currencyCode by lazy {
        SettingUtils(requireContext())
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentWeeklyDetailsBinding.bind(view)

        transactionType = arguments?.getParcelable(TRANSACTION_TYPE_KEY)

        binding.weeklyDetails.total.currencySymbol.text =
            currencyCode.getCurrencyCode()
        if (transactionType != null) {
            binding.weeklyDetails.total.transactionTypeTxt.text = transactionType.toString()
        } else {
            binding.weeklyDetails.total.transactionTypeTxt.text =
                context?.getString(R.string.thisWeek)
        }


        mAdepter = HomeAdepter(this)


        getTheDataForWeek(weeklyDate)

        binding.weeklyDetails.nextWeekAction.setOnClickListener {
            weeklyDate = weeklyDate.plusWeeks(1)
            getTheDataForWeek(weeklyDate)

        }
        binding.weeklyDetails.previousWeekAction.setOnClickListener {

            weeklyDate = weeklyDate.minusWeeks(1)
            getTheDataForWeek(weeklyDate)

        }
        setUpObserver()
        setUpRecyclerView()


    }


    private fun getTheDataForWeek(nowDate: LocalDate) {
        val date = getLocalDateStartEndDateWeek(nowDate)
        val weekStartDateLong = date.startDate
        val weekEndDateLong = date.endDate
        val startDateForTxt = convertDateLongToDateStringWeekly(weekStartDateLong)
        val endDateForTxt = convertDateLongToDateStringWeekly(weekEndDateLong)
        val newQuery =
                QueryForTransaction(transactionType, weekStartDateLong, weekEndDateLong)
            mViewModel.getTheQueryDate(queryData = newQuery)


        binding.weeklyDetails.dateTxtView.text = "$startDateForTxt to $endDateForTxt"
    }


    private fun setUpObserver() {

        mViewModel.allDataBetweenStartAndEndDate.observe(
            viewLifecycleOwner)

             { transactionList ->

                if (transactionList.isEmpty()) {
                    binding.weeklyDetails.totalRvView.hide()
                    binding.weeklyDetails.total.totalCardView.hide()
                    binding.weeklyDetails.noTransactionTv.show()

                } else {
                    binding.weeklyDetails.noTransactionTv.hide()
                    binding.weeklyDetails.totalRvView.show()
                    binding.weeklyDetails.total.totalCardView.show()
                    mAdepter.submitList(transactionList)
                    val total = transactionList.sumOf { transaction ->
                        transaction.amount
                    }
                    binding.weeklyDetails.total.expenseTotal.text = total.toString()
                }


            }


        }



    private fun setUpRecyclerView() {
        binding.weeklyDetails.totalRvView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = mAdepter
          }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null

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

}