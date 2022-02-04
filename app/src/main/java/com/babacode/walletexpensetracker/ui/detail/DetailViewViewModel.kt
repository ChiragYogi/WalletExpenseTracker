package com.babacode.walletexpensetracker.ui.detail

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.switchMap
import com.babacode.walletexpensetracker.data.model.DailyQueryForTransaction
import com.babacode.walletexpensetracker.data.model.QueryForTransaction
import com.babacode.walletexpensetracker.repository.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DetailViewViewModel @Inject constructor(
    private val repo: TransactionRepository
) : ViewModel() {

    private val queryDataForDB = MutableLiveData<QueryForTransaction>()
    private val queryForToday = MutableLiveData<DailyQueryForTransaction>()

    // for week,month,year
    val allDataBetweenStartAndEndDate = queryDataForDB.switchMap { query ->
        if (query.transactionType != null){
            repo.getTransactionByTransactionType(query.transactionType,query.startDate,query.endDate).asLiveData()
        }else{
            repo.getTransactionForSelectedDate(query.startDate,query.endDate).asLiveData()
        }

    }

    //for daily
    val dailyData = queryForToday.switchMap { todayQuery ->
        if (todayQuery.transactionType != null){
            repo.getSingleDayTransactionByType(todayQuery.transactionType,todayQuery.dateForToday).asLiveData()
        }else{
            repo.getSingleDayTransaction(todayQuery.dateForToday).asLiveData()
        }

    }


    // for week,month,year
    fun getTheQueryDate(queryData: QueryForTransaction){
        queryDataForDB.value = queryData
        Log.d("weekly",queryDataForDB.value.toString())
    }

    //for daily
    fun getDailyDateForQuery(queryForTransaction: DailyQueryForTransaction){
        queryForToday.value = queryForTransaction
    }




}