package com.babacode.walletexpensetracker.ui.detail


import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.babacode.walletexpensetracker.R
import com.babacode.walletexpensetracker.data.model.DailyQueryForTransaction
import com.babacode.walletexpensetracker.data.model.Transaction
import com.babacode.walletexpensetracker.data.model.TransactionType
import com.babacode.walletexpensetracker.databinding.FragmentDailyDetailsBinding
import com.babacode.walletexpensetracker.ui.home.HomeAdepter
import com.babacode.walletexpensetracker.utiles.Extra
import com.babacode.walletexpensetracker.utiles.SettingUtils
import com.babacode.walletexpensetracker.utiles.hide
import com.babacode.walletexpensetracker.utiles.show
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDate

@AndroidEntryPoint
class DailyDetails : Fragment(R.layout.fragment_daily_details), HomeAdepter.OnItemClick {

    private var _binding: FragmentDailyDetailsBinding? = null
    private val binding get() = _binding!!
    private val mViewModel: DetailViewViewModel by viewModels()
    private var transactionType: TransactionType? = null
    private lateinit var mAdepter: HomeAdepter

    private var todayDate:LocalDate = LocalDate.now()

    private val currencyCode by lazy {
        SettingUtils(requireContext())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentDailyDetailsBinding.bind(view)

        transactionType = arguments?.getParcelable(Extra.TRANSACTION_TYPE_KEY)



        Log.d("dateForToday",todayDate.toString())
        mAdepter = HomeAdepter(this)
        setUpObserver()
        setUpRecyclerView()
        getDataForToday(todayDate)

        binding.dailyDetails.nextWeekAction.setOnClickListener {
            todayDate = todayDate.plusDays(1)
            getDataForToday(todayDate)
        }
        binding.dailyDetails.previousWeekAction.setOnClickListener {
            todayDate = todayDate.minusDays(1)
            getDataForToday(todayDate)
        }


    }

    private fun getDataForToday(todayDate: LocalDate) {

        val dateToLong = Extra.convertLocalDateToLong(todayDate)

        val newQuery = DailyQueryForTransaction(transactionType, dateToLong)
        mViewModel.getDailyDateForQuery(newQuery)

        val dateText = Extra.convertLongDateToStringDate(dateToLong)
        binding.dailyDetails.dateTxtView.text = dateText
    }

    private fun setUpRecyclerView() {
        binding.dailyDetails.totalRvView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = mAdepter
        }
    }

    private fun setUpObserver() {

        mViewModel.dailyData.observe(viewLifecycleOwner) { transactionList ->

            if (transactionList.isNotEmpty()) {
                showViewInDaily(transactionList)
            } else {
                hideViewInDaily()
            }
        }


    }

    private fun showViewInDaily(transactionList: List<Transaction>) {
        binding.dailyDetails.apply {
            noTransactionTv.hide()
            total.totalCardView.show()

            totalRvView.show()
            binding.dailyDetails.total.currencySymbol.text =
                currencyCode.getCurrencyCode()
            if (transactionType != null) {
                binding.dailyDetails.total.transactionTypeTxt.text = transactionType.toString()
            } else {
                binding.dailyDetails.total.transactionTypeTxt.text =
                    context?.getString(R.string.today)
            }
            val totalForDaily = transactionList.sumOf { transaction ->
                transaction.amount
            }
            total.expenseTotal.text = totalForDaily.toString()

        }
        mAdepter.submitList(transactionList)

    }

    private fun hideViewInDaily() {
        binding.dailyDetails.apply {
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


    override fun onDestroyView() {
        binding.dailyDetails.totalRvView.adapter = null
        super.onDestroyView()
        _binding = null
    }

}