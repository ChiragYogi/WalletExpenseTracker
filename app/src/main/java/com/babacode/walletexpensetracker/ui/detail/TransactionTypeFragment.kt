package com.babacode.walletexpensetracker.ui.detail

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.navArgs
import com.babacode.walletexpensetracker.R
import com.babacode.walletexpensetracker.databinding.FragmentTransactionTypeBinding
import com.babacode.walletexpensetracker.ui.ADD_TRANSACTION_RESULT_OK
import com.babacode.walletexpensetracker.ui.EDIT_TRANSACTION_RESULT_OK
import com.babacode.walletexpensetracker.utiles.Extra.DAILY_TAB_NAME
import com.babacode.walletexpensetracker.utiles.Extra.MONTHLY_TAB_NAME
import com.babacode.walletexpensetracker.utiles.Extra.TRANSACTION_TYPE_KEY
import com.babacode.walletexpensetracker.utiles.Extra.WEEKLY_TAB_NAME
import com.babacode.walletexpensetracker.utiles.Extra.YEARLY_TAB_NAME
import com.babacode.walletexpensetracker.utiles.showSnackBar
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class TransactionTypeFragment : Fragment(R.layout.fragment_transaction_type) {


    private var _binding: FragmentTransactionTypeBinding? = null
    private val binding get() = _binding!!
    private val transactionTypeArgs: TransactionTypeFragmentArgs by navArgs()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentTransactionTypeBinding.bind(view)


        //get transaction Type
        val transactionTypeValue = transactionTypeArgs.transactionType

        //put transaction Type in bundle
        val transactionBundle = Bundle().apply {
            putParcelable(TRANSACTION_TYPE_KEY, transactionTypeValue)
        }

        //attach viewpager and pass the bundle in constructor
        val viewPager = binding.doppelgangerViewPager
        val viewPagerAdepter = ViewPagerAdepter(childFragmentManager, lifecycle, transactionBundle)
        viewPager.adapter = viewPagerAdepter
        val tabLayout = binding.tabLayout
        val tabName = arrayOf(
            DAILY_TAB_NAME,
            WEEKLY_TAB_NAME,
            MONTHLY_TAB_NAME,
            YEARLY_TAB_NAME
        )
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = tabName[position]

        }.attach()


        setFragmentResultListener("add_edit_request") { _, bundle ->
            when (bundle.getInt("add_edit_request")) {
                ADD_TRANSACTION_RESULT_OK -> binding.root.showSnackBar(R.string.transaction_added)
                EDIT_TRANSACTION_RESULT_OK -> binding.root.showSnackBar(R.string.transaction_update)
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null

    }

}