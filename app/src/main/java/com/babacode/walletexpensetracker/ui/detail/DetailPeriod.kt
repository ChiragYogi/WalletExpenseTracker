package com.babacode.walletexpensetracker.ui.detail

import androidx.annotation.StringRes
import com.babacode.walletexpensetracker.R
import com.babacode.walletexpensetracker.utiles.Extra
import java.time.LocalDate

enum class DetailPeriod(
    val tabLabel: String,
    @StringRes val fallbackTitleRes: Int,
    @StringRes val totalLabelRes: Int,
    val step: (LocalDate, Long) -> LocalDate,
    val dateLabel: (LocalDate) -> String
) {
    DAILY(
        tabLabel = Extra.DAILY_TAB_NAME,
        fallbackTitleRes = R.string.today,
        totalLabelRes = R.string.total_for_this_day,
        step = { date, amount -> date.plusDays(amount) },
        dateLabel = { date -> Extra.convertLongDateToStringDate(Extra.convertLocalDateToLong(date)) }
    ),
    WEEKLY(
        tabLabel = Extra.WEEKLY_TAB_NAME,
        fallbackTitleRes = R.string.thisWeek,
        totalLabelRes = R.string.total_this_week,
        step = { date, amount -> date.plusWeeks(amount) },
        dateLabel = { date ->
            val range = Extra.getLocalDateStartEndDateWeek(date)
            "${Extra.convertDateLongToDateStringWeekly(range.startDate)} to ${Extra.convertDateLongToDateStringWeekly(range.endDate)}"
        }
    ),
    MONTHLY(
        tabLabel = Extra.MONTHLY_TAB_NAME,
        fallbackTitleRes = R.string.thisMonth,
        totalLabelRes = R.string.total_this_month,
        step = { date, amount -> date.plusMonths(amount) },
        dateLabel = { date -> Extra.convertDateLongToDateStringMonthly(Extra.getLocalDateStartEndDateMonth(date).startDate) }
    ),
    YEARLY(
        tabLabel = Extra.YEARLY_TAB_NAME,
        fallbackTitleRes = R.string.thisYear,
        totalLabelRes = R.string.total_this_year,
        step = { date, amount -> date.plusYears(amount) },
        dateLabel = { date -> Extra.convertDateLongToDateStringYearly(Extra.getLocalDateStartEndDateYear(date).startDate) }
    )
}
