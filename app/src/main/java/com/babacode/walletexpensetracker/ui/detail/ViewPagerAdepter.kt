package com.babacode.walletexpensetracker.ui.detail

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter


class ViewPagerAdepter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle,
    private val bundle: Bundle
) :
    FragmentStateAdapter(fragmentManager, lifecycle) {


    private val fragmentArraylist = arrayListOf<Fragment>(
        DailyDetails(), WeeklyDetails(), MonthlyDetails(), YearlyDetails()
    )

    override fun getItemCount(): Int {
        return fragmentArraylist.size

    }

    override fun createFragment(position: Int): Fragment {
        fragmentArraylist[0].arguments = bundle
        fragmentArraylist[1].arguments = bundle
        fragmentArraylist[2].arguments = bundle
        fragmentArraylist[3].arguments = bundle

        return fragmentArraylist[position]
    }
}

