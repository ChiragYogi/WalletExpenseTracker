package com.babacode.walletexpensetracker.ui.calender

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.babacode.walletexpensetracker.R
import com.babacode.walletexpensetracker.data.model.Transaction
import com.babacode.walletexpensetracker.databinding.FragmentTransactionCalenderViewBinding
import com.babacode.walletexpensetracker.ui.ADD_TRANSACTION_RESULT_OK
import com.babacode.walletexpensetracker.ui.EDIT_TRANSACTION_RESULT_OK
import com.babacode.walletexpensetracker.ui.home.HomeAdepter
import com.babacode.walletexpensetracker.utiles.Extra.convertCalenderDateToLong
import com.babacode.walletexpensetracker.utiles.hide
import com.babacode.walletexpensetracker.utiles.show
import com.babacode.walletexpensetracker.utiles.showSnackBar
import dagger.hilt.android.AndroidEntryPoint
import java.util.*


@AndroidEntryPoint
class TransactionCalenderViewFragment : Fragment(R.layout.fragment_transaction_calender_view),
    HomeAdepter.OnItemClick {


    private var _binding: FragmentTransactionCalenderViewBinding? = null
    private val binding get() = _binding!!
    private lateinit var mAdepter: HomeAdepter
    private val viewModel: CalenderViewViewModel by viewModels()
    private var currentDayDate = Calendar.getInstance().time

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentTransactionCalenderViewBinding.bind(view)
        mAdepter = HomeAdepter(this)
        setUpRecyclerView()
        setUpObserver()

        getTransactionForSelectedDate(currentDayDate)
        binding.calendarView.date = convertCalenderDateToLong(currentDayDate)
        binding.calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val calender = Calendar.getInstance()
            calender.set(Calendar.YEAR, year)
            calender.set(Calendar.MONTH, month)
            calender.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            currentDayDate = calender.time

            getTransactionForSelectedDate(currentDayDate)
            Log.d("date", currentDayDate.toString())


        }

        setFragmentResultListener("add_edit_request") { _, bundle ->
            when(bundle.getInt("add_edit_request")){
                ADD_TRANSACTION_RESULT_OK -> binding.root.showSnackBar(R.string.transaction_added)
                EDIT_TRANSACTION_RESULT_OK -> binding.root.showSnackBar(R.string.transaction_update)
            }
        }


    }



    private fun getTransactionForSelectedDate(date: Date) {
        val newDate = convertCalenderDateToLong(date)
        viewModel.getSelectedDateFromCalender(newDate)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null


    }

    private fun setUpObserver() {
        viewModel.selectedDateTransaction.observe(viewLifecycleOwner) { transactionList ->
            if (transactionList.isEmpty()) {
                binding.noTransactionTv.show()
                binding.calenderRecyclerView.hide()
            } else {
                binding.noTransactionTv.hide()
                binding.calenderRecyclerView.show()
                mAdepter.submitList(transactionList)
            }

        }
    }

    private fun setUpRecyclerView() {
        binding.calenderRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = mAdepter
         }
    }


    override fun OnTransactionClick(transaction: Transaction) {
        val action =
            TransactionCalenderViewFragmentDirections.actionCalenderViewFragmentToEditTransactionFragment(
                transaction
            )

        findNavController().navigate(action)
    }

    override fun OnLongPress(transaction: Transaction) {
        val action =
            TransactionCalenderViewFragmentDirections.actionGlobalDeleteTransaction(
                transaction
            )
        findNavController().navigate(action)
    }


}