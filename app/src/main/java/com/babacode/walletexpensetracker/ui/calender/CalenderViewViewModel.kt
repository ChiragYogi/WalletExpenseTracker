package com.babacode.walletexpensetracker.ui.calender

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.switchMap
import com.babacode.walletexpensetracker.repository.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class CalenderViewViewModel @Inject constructor(
    private val repository: TransactionRepository
) : ViewModel() {

    private val currentDate = MutableLiveData<Long>()

    val selectedDateTransaction = currentDate.switchMap { date ->
        repository.getSingleDayTransaction(date).asLiveData()
    }

    fun getSelectedDateFromCalender(date: Long) {
        currentDate.value = date
    }


}