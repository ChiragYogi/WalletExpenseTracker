package com.babacode.walletexpensetracker.utiles

import android.annotation.SuppressLint
import com.babacode.walletexpensetracker.data.model.Month
import java.text.SimpleDateFormat
import java.util.*

object Extra {


    const val AMOUNT_DIGIT = 8

    val transactionType = listOf("Income", "Expense")

    val transactionPayment = listOf("Cash", "Card", "Online Banking"," UPI")

    val transactionTag = listOf(
        "Other",
        "Food",
        "Shopping",
        "Travelling",
        "Entertainment",
        "Health",
        "Education",
        "Rent",
        "bills",
        "Gift",
        "utils",
        "Salary",
        "Coupons",
        "CashBack",
    )

    @SuppressLint("SimpleDateFormat")
    fun convertDateToLong(date: String): Long {
        val df = SimpleDateFormat("dd/MM/yyyy")
        return df.parse(date).time
    }

    @SuppressLint("SimpleDateFormat")
    fun convertDateToDateFromLong(date: Date): Long {
        val df = SimpleDateFormat("dd/MM/yyyy")
        return df.parse(date.toString()).toString().toLong()
    }

    @SuppressLint("SimpleDateFormat")
    fun convertLongToTime(time: Long): String {
        val date = Date(time)
        val format = SimpleDateFormat("dd/MM/yyyy")
        return format.format(date)
    }


    @SuppressLint("SimpleDateFormat")
    fun getStartDateAndEndDate(): Month{

        val calendar = Calendar.getInstance()
        calendar.add(Calendar.MONTH, 0)
        calendar.set(Calendar.DATE, calendar.getActualMinimum(Calendar.DAY_OF_MONTH))
        val monthFirstDate: Date = calendar.time
        calendar.set(Calendar.DATE, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
        val monthLastDate: Date = calendar.time

        val sdf = SimpleDateFormat("dd/MM/yyyy")

        val stringStartDate = sdf.format(monthFirstDate)
        val stringEndDate = sdf.format(monthLastDate)

        return Month(stringStartDate, stringEndDate)
    }

}