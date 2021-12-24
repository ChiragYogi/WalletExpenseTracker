package com.babacode.walletexpensetracker.utiles

import com.babacode.walletexpensetracker.data.model.Month
import com.babacode.walletexpensetracker.data.model.PaymentType
import com.babacode.walletexpensetracker.data.model.TransactionTag
import com.babacode.walletexpensetracker.data.model.TransactionType
import java.text.SimpleDateFormat
import java.util.*

object Extra {


    const val AMOUNT_CHECK_FOR_ADD = 7
    const val BASE_AMOUNT = 0.0

    fun transactionType(type: String): TransactionType {

        return when(type){
            TransactionType.EXPENSE.toString() -> {
                TransactionType.EXPENSE
            }
            TransactionType.INCOME.toString() -> {
                TransactionType.INCOME
            }

            else -> TransactionType.EXPENSE
        }
    }

    fun paymentMode(type: String): PaymentType {
        return when(type){
            PaymentType.CASH.toString() -> {
                PaymentType.CASH
            }
            PaymentType.CARD.toString() -> {
                PaymentType.CARD
            }
            PaymentType.ONLINE.toString() -> {
                PaymentType.ONLINE
            }
            PaymentType.GIFT.toString() -> {
                PaymentType.GIFT
            }

            else -> PaymentType.CASH
        }
    }


    fun transactionTag(tag: String): TransactionTag {
        return when(tag){
            TransactionTag.OTHER.toString() -> {
                TransactionTag.OTHER
            }
            TransactionTag.FOOD.toString() -> {
                TransactionTag.FOOD
            }
            TransactionTag.SHOPPING.toString() -> {
                TransactionTag.SHOPPING
            }
            TransactionTag.TRAVELLING.toString() -> {
                TransactionTag.TRAVELLING
            }
            TransactionTag.ENTERTAINMENT.toString() -> {
                TransactionTag.ENTERTAINMENT
            }
            TransactionTag.HEALTH.toString() -> {
                TransactionTag.HEALTH
            }
            TransactionTag.EDUCATION.toString() -> {
                TransactionTag.EDUCATION
            }
            TransactionTag.RENT.toString() -> {
                TransactionTag.RENT
            }
            TransactionTag.GIFT.toString() -> {
                TransactionTag.GIFT
            }
            TransactionTag.UTILS.toString() -> {
                TransactionTag.UTILS
            }
            TransactionTag.SALARY.toString() -> {
                TransactionTag.SALARY
            }
            TransactionTag.COUPONS.toString() -> {
                TransactionTag.COUPONS
            }
            TransactionTag.CASHBACK.toString() -> {
                TransactionTag.CASHBACK
            }


            else -> TransactionTag.OTHER
        }
    }

    //pars double
    fun parseDouble(value: String?) : Double{
        return try {
            if (value == null || value.isEmpty()) Double.NaN else value.toDouble()
        }catch (e: Exception){
            e.printStackTrace()
            return 0.0
        }
    }


    fun convertDateToLong(date: String): Long? {
        val df = SimpleDateFormat("dd/MM/yyyy", Locale.US)
        return df.parse(date)?.time
    }

    fun convertDateLongToDateString(time: Long?): String {
        val date = time?.let { Date(it) }
        val format = SimpleDateFormat("dd/MM/yyyy", Locale.US)
        return format.format(date)
    }

    fun currentDayDate(): Long {

       val date = Calendar.getInstance().time
        val df = SimpleDateFormat("dd/MM/yyyy", Locale.US)
        val stringDate = df.format(date)
        return df.parse(stringDate)!!.time
    }

    fun getStartDateAndEndDate(): Month {

        val calendar = Calendar.getInstance()
        calendar.add(Calendar.MONTH, 0)
        //take First Day Of Month
        calendar.set(Calendar.DATE, calendar.getActualMinimum(Calendar.DAY_OF_MONTH))
        val monthFirstDate: Date = calendar.time
        //take First Day Of Month
        calendar.set(Calendar.DATE, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
        val monthLastDate: Date = calendar.time

        //Format date to string
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.US)
        val formatOne = sdf.format(monthFirstDate)
        val formatTwo = sdf.format(monthLastDate)
        val stringStartDate = convertDateToLong(formatOne)
        val stringEndDate = convertDateToLong(formatTwo)

        return Month(stringStartDate!!, stringEndDate!!)
    }
}

