package com.babacode.walletexpensetracker.ui.home


import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.babacode.walletexpensetracker.R
import com.babacode.walletexpensetracker.data.model.Transaction
import com.babacode.walletexpensetracker.data.model.TransactionType
import com.babacode.walletexpensetracker.databinding.FragmentHomeBinding
import com.babacode.walletexpensetracker.ui.ADD_TRANSACTION_RESULT_OK
import com.babacode.walletexpensetracker.ui.EDIT_TRANSACTION_RESULT_OK
import com.babacode.walletexpensetracker.utiles.SettingUtils
import com.babacode.walletexpensetracker.utiles.showSnackBar
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home), HomeAdepter.OnItemClick {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var mAdepter: HomeAdepter
    private val viewModel: HomeViewModel by viewModels()
    private val currencyCode by lazy {
        SettingUtils(requireContext())
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentHomeBinding.bind(view)
        mAdepter = HomeAdepter(this)

        binding.apply {
            expenseView.currencySymbol.text = currencyCode.getCurrencyCode()
            incomeView.currencySymbol.text = currencyCode.getCurrencyCode()
        }


        initPieChart()
        setUpCurrentMonthObserver()
        setUpRecyclerView()

        binding.incomeView.incomeViewClickId.setOnClickListener {

            val action = HomeFragmentDirections.actionHomeFragmentToTransactionTypeFragment(
                TransactionType.INCOME
            )
            findNavController().navigate(action)
        }

        binding.expenseView.expenseViewClickId.setOnClickListener {

            val action =
                HomeFragmentDirections.actionHomeFragmentToTransactionTypeFragment(
                    TransactionType.EXPENSE
                )
            findNavController().navigate(action)
        }
        binding.addFab.setOnClickListener {
            val action =
                HomeFragmentDirections.actionHomeFragmentToAddTransactionFragment(
                    null
                )
            findNavController().navigate(action)
        }

        setFragmentResultListener("add_edit_request") { _, bundle ->
            when (bundle.getInt("add_edit_request")) {
                ADD_TRANSACTION_RESULT_OK -> binding.root.showSnackBar(R.string.transaction_added)
                EDIT_TRANSACTION_RESULT_OK -> binding.root.showSnackBar(R.string.transaction_update)
            }

        }

        setHasOptionsMenu(true)

    }


    private fun setUpCurrentMonthObserver() {


        viewModel.currentMonthTransaction.observe(viewLifecycleOwner) { currentMonthTransaction ->

            getTotalForIncomeAndExpense(currentMonthTransaction)

        }

        viewModel.recentTransaction.observe(viewLifecycleOwner) { transactionList ->
            mAdepter.submitList(transactionList)
            Log.d("work", transactionList.toString())
        }

    }

    private fun getTotalForIncomeAndExpense(list: List<Transaction>?) {

        var incomeTotal = 0.0
        var expenseTotal = 0.0

        if (list != null) {
            val (income, expense) = list.partition { transaction ->
                transaction.transactionType.toString() == TransactionType.INCOME.toString()
            }

            incomeTotal = income.sumOf { incomeTransaction ->
                incomeTransaction.amount
            }

            expenseTotal = expense.sumOf { expenseTransaction ->
                expenseTransaction.amount
            }

        }
        setDataToPieChart(incomeTotal, expenseTotal)

        binding.incomeView.incomeTotal.text = incomeTotal.toString()
        binding.expenseView.expenseTotal.text = expenseTotal.toString()


    }


    private fun initPieChart() {
        binding.pieChart.pieChartInCardView.apply {
            setUsePercentValues(true)
            isDrawHoleEnabled = false
            description.isEnabled = false
            //hollow pie chart
            setTouchEnabled(false)
            setDrawEntryLabels(false)
            //adding padding
            setExtraOffsets(1f, 1f, 1f, 0f)
            isRotationEnabled = false
            setDrawEntryLabels(false)
            legend.orientation = Legend.LegendOrientation.VERTICAL
            legend.direction = Legend.LegendDirection.LEFT_TO_RIGHT
            legend.isWordWrapEnabled = true
        }
    }


    private fun setDataToPieChart(incomeTotal: Double, expenseTotal: Double) {

        binding.pieChart.pieChartInCardView.apply {
            setUsePercentValues(true)
            val dataEntries = ArrayList<PieEntry>()
            dataEntries.add(PieEntry(expenseTotal.toFloat(), "Expense"))
            dataEntries.add(PieEntry(incomeTotal.toFloat(), "Income"))


            val colors: ArrayList<Int> = ArrayList()
            colors.add(Color.parseColor("#EF2727"))
            colors.add(Color.parseColor("#86DF3B"))


            val dataSet = PieDataSet(dataEntries, "")
            val data = PieData(dataSet)
            // In Percentage
            data.setDrawValues(true)
            data.setValueFormatter(PercentFormatter(this))
            dataSet.sliceSpace = 3f
            dataSet.colors = colors
            this.data = data
            data.setValueTextSize(12f)
            data.setValueTextColor(Color.BLACK)
            setExtraOffsets(1f, 1f, 1f, 0f)
            animateY(1400, Easing.EaseInOutQuad)
            invalidate()


        }

    }


    private fun setUpRecyclerView() {
        binding.rvView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = mAdepter
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.detail_screen_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.analysisViewFragment -> {
                val action =
                    HomeFragmentDirections.actionHomeFragmentToTransactionTypeFragment(
                        null
                    )
                findNavController().navigate(action)
            }
            R.id.calenderViewFragment -> {
                val action =
                    HomeFragmentDirections.actionHomeFragmentToCalenderViewFragment()
                findNavController().navigate(action)
            }
            R.id.settingsFragment -> {
                val action =
                    HomeFragmentDirections.actionHomeFragmentToSettingsFragment()
                findNavController().navigate(action)
            }
        }

        return super.onOptionsItemSelected(item)
    }


    override fun onDestroyView() {
        binding.rvView.adapter = null
        super.onDestroyView()
        _binding = null

    }

    override fun OnTransactionClick(transaction: Transaction) {
        val action =
            HomeFragmentDirections.actionHomeFragmentToEditTransactionFragment(transaction)
        findNavController().navigate(action)

    }

    override fun OnLongPress(transaction: Transaction) {
        val action =
            HomeFragmentDirections.actionGlobalDeleteTransaction(transaction)
        findNavController().navigate(action)
    }


}