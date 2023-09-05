package com.babacode.walletexpensetracker.utiles

import com.babacode.walletexpensetracker.data.model.DateForQuery
import com.babacode.walletexpensetracker.data.model.PaymentType
import com.babacode.walletexpensetracker.data.model.TransactionTag
import com.babacode.walletexpensetracker.data.model.TransactionType
import java.text.SimpleDateFormat
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*


object Extra {


    const val AMOUNT_CHECK_FOR_ADD = 7
    const val NOTE_LENGTH_VALIDATE = 40
    const val TRANSACTION_TYPE_KEY = "transactionType"
    const val WEEKLY_TAB_NAME = "Weekly"
    const val DAILY_TAB_NAME = "Daily"
    const val MONTHLY_TAB_NAME = "Monthly"
    const val YEARLY_TAB_NAME = "Yearly"
    const val REQUEST_KEY_FOR_ADD_EDIT = "add_edit_request"
    const val privacy_policy_url = "https://github.com/ChiragYogi/WalletExpenseTracker/blob/master/PrivacyPolicy.md"


    fun transactionType(type: String): TransactionType {

        return when (type) {
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
        return when (type) {
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
        return when (tag) {
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
    fun parseDouble(value: String?): Double {
        return try {
            if (value == null || value.isEmpty()) Double.NaN else value.toDouble()
        } catch (e: Exception) {
            e.printStackTrace()
            return 0.0
        }
    }


    fun convertStringDateToLong(date: String): Long {

        val df = SimpleDateFormat("dd MMM, yyyy", Locale.US)
        return df.parse(date)!!.time
    }

    fun convertLongDateToStringDate(time: Long): String {

        val format = SimpleDateFormat("dd MMM, yyyy", Locale.US)
        return format.format(time)
    }

/*    fun convertLongDateTimeToStringDate(time: Long): String {

        val format = SimpleDateFormat("dd-MM-yyyy hh:mm:ss", Locale.US)
        return format.format(time)
    }*/

    fun convertDateLongToDateStringWeekly(time: Long): String {

        val format = SimpleDateFormat("dd MMM", Locale.US)
        return format.format(time)
    }

    fun convertDateLongToDateStringMonthly(time: Long): String {

        val format = SimpleDateFormat("MMM yyyy", Locale.US)
        return format.format(time)
    }

    fun convertDateLongToDateStringYearly(time: Long): String {

        val format = SimpleDateFormat("yyyy", Locale.US)
        return format.format(time)
    }


    fun currentDayDate(): Long {

        val date = Calendar.getInstance().time
        val df = SimpleDateFormat("dd MMM, yyyy", Locale.US)
        val stringDate = df.format(date)
        return df.parse(stringDate)!!.time
    }

    fun convertLocalDateToLong(currentDate: LocalDate): Long {

        val date = DateTimeFormatter.ofPattern("dd MMM, yyyy", Locale.US)
        val formatDateForTodayLocal = date.format(currentDate)
        return convertStringDateToLong(formatDateForTodayLocal)
    }

    fun convertLocalLongDateToStringAndGetLocalDate(date: Long): LocalDate {
        val newDate = convertLongDateToStringDate(date)
        val formatter = DateTimeFormatter.ofPattern("dd MMM, yyyy", Locale.US)
        return LocalDate.parse(newDate, formatter)
    }

    fun convertCalenderDateToLong(currentDate: Date): Long{

        val date = SimpleDateFormat("dd MMM, yyyy", Locale.US)
        val formatDateForTodayCalender = date.format(currentDate)
        return convertStringDateToLong(formatDateForTodayCalender)
    }


    fun getLocalDateStartEndDateYear(currentDate: LocalDate): DateForQuery {

        val dayOne = currentDate.withDayOfYear(1)
        val nextYear = dayOne.plusYears(1)
        val endOffMonth = nextYear.minusDays(1)

        val date1 = DateTimeFormatter.ofPattern("dd MMM, yyyy", Locale.US)
        val formatDate1 = date1.format(dayOne)
        val formatDate2 = date1.format(endOffMonth)


        val startDate = convertStringDateToLong(formatDate1)
        val endDate = convertStringDateToLong(formatDate2)
        return DateForQuery(startDate, endDate)

    }

    fun getLocalDateStartEndDateMonth(currentDate: LocalDate): DateForQuery {

        val dayOne = currentDate.withDayOfMonth(1)
        val nextMonth = dayOne.plusMonths(1)
        val endOffMonth = nextMonth.minusDays(1)


        val date1 = DateTimeFormatter.ofPattern("dd MMM, yyyy", Locale.US)
        val formatDate1 = date1.format(dayOne)
        val formatDate2 = date1.format(endOffMonth)

        val startDate = convertStringDateToLong(formatDate1)
        val endDate = convertStringDateToLong(formatDate2)
        return DateForQuery(startDate, endDate)

    }


    fun getLocalDateStartEndDateWeek(currentDate: LocalDate): DateForQuery {
        // Go backward to get Monday
        var monday = currentDate
        while (monday.dayOfWeek != DayOfWeek.MONDAY) {
            monday = monday.minusDays(1)
        }

        // Go forward to get Sunday
        var sunday = currentDate
        while (sunday.dayOfWeek != DayOfWeek.SUNDAY) {
            sunday = sunday.plusDays(1)
        }

        val date1 = DateTimeFormatter.ofPattern("dd MMM, yyyy", Locale.US)
        val formatDate1 = date1.format(monday)
        val formatDate2 = date1.format(sunday)

        val startDate = convertStringDateToLong(formatDate1)
        val endDate = convertStringDateToLong(formatDate2)

        return DateForQuery(startDate, endDate)

    }




}

