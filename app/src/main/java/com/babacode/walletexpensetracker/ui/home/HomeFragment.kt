package com.babacode.walletexpensetracker.ui.home

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.babacode.walletexpensetracker.R
import com.babacode.walletexpensetracker.data.model.Transaction
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


@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home), OnItemClick {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val mAdepter = HomeAdepter(this)

    private val viewModel: HomeViewModel by viewModels()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentHomeBinding.bind(view)

        setUpRecyclerView()
        initPieChart()
        setUpObserver()

        binding.addFab.setOnClickListener {
            viewModel.onAddNewTransactionClick()
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

    private fun setUpObserver() {

        viewModel.recentTransaction.observe(viewLifecycleOwner, Observer { transactionList ->


            mAdepter.submitList(transactionList)


            val (totalIncome, totalExpense) = transactionList.partition { transaction ->
                transaction.transactionType == "Income" ||
                        transaction.transactionType == "Expense"
            }

            val incomeTotal = totalIncome.sumOf { transaction ->
                transaction.amount
            }
            val expenseTotal = totalExpense.sumOf { transaction ->
                transaction.amount
            }
            binding.incomeView.incomeTotal.text = incomeTotal.toString()
            binding.expenseView.expenseTotal.text = expenseTotal.toString()
            setDataToPieChart(incomeTotal, expenseTotal)
        })

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.transactionEvent.collect { event ->
                when (event) {
                    is HomeViewModel.TransactionEvent.NavigateToAddTransactionScreen -> {
                        val action =
                            HomeFragmentDirections.actionHomeFragmentToAddTransactionFragment(
                                null,
                                "Add Transaction"
                            )
                        findNavController().navigate(action)
                    }
                    is HomeViewModel.TransactionEvent.NavigateToDetailScreen -> {
                        val action =
                            HomeFragmentDirections.actionHomeFragmentToTransactionDetailFragment(
                                event.transaction
                            )
                        findNavController().navigate(action)
                    }
                    is HomeViewModel.TransactionEvent.ShowTransactionSavedConfirmationMessage -> {
                        //nothing
                    }
                }
            }
        }.exhaustive

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


}