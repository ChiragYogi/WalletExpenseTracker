package com.babacode.walletexpensetracker.ui.home


import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.babacode.walletexpensetracker.R
import com.babacode.walletexpensetracker.data.model.Transaction
import com.babacode.walletexpensetracker.data.model.TransactionType
import com.babacode.walletexpensetracker.databinding.FragmentHomeBinding
import com.babacode.walletexpensetracker.utiles.exhaustive
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlin.collections.ArrayList
import com.babacode.walletexpensetracker.utiles.Extra.BASE_AMOUNT
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch


@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home), HomeAdepter.OnItemClick {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val mAdepter = HomeAdepter(this)
    private val viewModel: HomeViewModel by viewModels()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentHomeBinding.bind(view)


        setUpRecyclerView()
        initPieChart()
        setUpCurrentMonthObserver()
        setRecentTransactionToAdepter()


        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.transactionEvent.collect { event ->
                    when (event) {
                        is HomeViewModel.TransactionEvent.NavigateToAddTransactionScreen -> {
                            val action =
                                HomeFragmentDirections.actionHomeFragmentToAddTransactionFragment(
                                    "Add Transaction",
                                    null
                                )
                            findNavController().navigate(action)
                        }

                        is HomeViewModel.TransactionEvent.ShowTransactionSavedConfirmationMessage -> {
                            Snackbar.make(requireView(), event.message, Snackbar.LENGTH_SHORT)
                                .show()
                        }
                        is HomeViewModel.TransactionEvent.NavigateToDeleteTransactionScreen -> {
                            val action =
                                HomeFragmentDirections.actionGlobalDeleteTransaction(event.transaction)
                            findNavController().navigate(action)
                        }
                        is HomeViewModel.TransactionEvent.NavigateToEditScreen -> {
                            val action =
                                HomeFragmentDirections.actionHomeFragmentToAddTransactionFragment(
                                    "Edit Transaction",
                                    event.transaction

                                )
                            findNavController().navigate(action)
                        }
                        is HomeViewModel.TransactionEvent.NavigateToAllTransactionTypeScreen -> {
                            val action =
                                HomeFragmentDirections.actionHomeFragmentToTransactionTypeFragment(
                                    event.transactionType
                                )
                            findNavController().navigate(action)
                        }
                    }
                }
            }
        }.exhaustive



        binding.incomeView.incomeViewClickId.setOnClickListener {
            viewModel.onTransactionTotalSelected(TransactionType.INCOME)
        }

        binding.expenseView.expenseViewClickId.setOnClickListener {
            viewModel.onTransactionTotalSelected(TransactionType.EXPENSE)
        }
        binding.addFab.setOnClickListener {
            viewModel.onAddNewTransactionClick()
        }

        setFragmentResultListener("add_edit_request") { _, bundle ->
            val result = bundle.getInt("add_edit_request")
            viewModel.onAddEditResult(result)
        }


    }

    private fun setUpCurrentMonthObserver() {
        viewModel.currentMonthTransaction.observe(viewLifecycleOwner) { transactionList ->
            if (transactionList.isNotEmpty()) {
                val (income, expense) = transactionList.partition { transaction ->
                    transaction.transactionType.toString() == TransactionType.INCOME.toString()
                }

                val incomeTotal = income.sumOf { transaction ->
                    transaction.amount
                }
                val expenseTotal = expense.sumOf { transaction ->
                    transaction.amount
                }

                binding.apply {
                    incomeView.incomeTotal.text = incomeTotal.toString()
                    expenseView.expenseTotal.text = expenseTotal.toString()
                }

                setDataToPieChart(incomeTotal, expenseTotal)


            } else {
                binding.apply {
                    incomeView.incomeTotal.text = BASE_AMOUNT.toString()
                    expenseView.expenseTotal.text = BASE_AMOUNT.toString()
                }

            }

        }
    }


    private fun initPieChart() {
        binding.pieChart.pieChartInCardView.apply {
            setUsePercentValues(true)
            description.text = ""
            //hollow pie chart
            isDrawHoleEnabled = false
            setTouchEnabled(false)
            setDrawEntryLabels(false)
            //adding padding
            setExtraOffsets(20f, 0f, 20f, 20f)
            setUsePercentValues(true)
            isRotationEnabled = false
            setDrawEntryLabels(false)
            legend.orientation = Legend.LegendOrientation.VERTICAL
            legend.isWordWrapEnabled = true
        }
    }


    private fun setRecentTransactionToAdepter() {

        viewModel.recentTransaction.observe(viewLifecycleOwner, { transactionList ->
            mAdepter.submitList(transactionList)
        })
    }


    private fun setDataToPieChart(incomeTotal: Double, expenseTotal: Double) {

        binding.pieChart.pieChartInCardView.apply {

            setUsePercentValues(true)
            val dataEntries = ArrayList<PieEntry>()
            dataEntries.add(PieEntry(incomeTotal.toFloat(), "Income"))
            dataEntries.add(PieEntry(expenseTotal.toFloat(), "Expense"))


            val colors: ArrayList<Int> = ArrayList()
            colors.add(Color.parseColor("#4DD0E1"))
            colors.add(Color.parseColor("#FFF176"))


            val dataSet = PieDataSet(dataEntries, "")
            val data = PieData(dataSet)
            // In Percentage
            data.setValueFormatter(PercentFormatter())
            dataSet.sliceSpace = 1f
            dataSet.colors = colors
            this.data = data
            data.setValueTextSize(15f)
            setExtraOffsets(5f, 10f, 5f, 5f)
            animateY(1400, Easing.EaseInOutQuad)

            //create hole in center
            holeRadius = 20f
            transparentCircleRadius = 31f
            isDrawHoleEnabled = true
            setHoleColor(Color.WHITE)
            invalidate()


        }

    }


    private fun setUpRecyclerView() {
        binding.rvView.apply {
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
        viewModel.onTransactionSelected(transaction)

    }

    override fun OnLongPress(transaction: Transaction) {
        viewModel.onTransactionDeleteClick(transaction)
    }


}