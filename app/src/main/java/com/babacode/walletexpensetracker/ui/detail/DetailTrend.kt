package com.babacode.walletexpensetracker.ui.detail

import com.babacode.walletexpensetracker.data.model.Transaction
import com.babacode.walletexpensetracker.data.model.TransactionType
import com.babacode.walletexpensetracker.utiles.Extra
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

data class DetailBucket(val label: String, val amount: Double)

private val monthLabelFormatter = DateTimeFormatter.ofPattern("MMM", Locale.US)

// Port of the reference design's per-period trend buckets (refrence/src/routes/detail.tsx:
// `buckets`) — always computed against [trendType], independent of the range-scoped
// transaction list shown below the chart, so the yearly view's 12 monthly bars (say)
// reflect the whole year even while paging through a single day/week/month elsewhere.
fun buildTrendBuckets(
    period: DetailPeriod,
    cursor: LocalDate,
    transactions: List<Transaction>,
    trendType: TransactionType
): List<DetailBucket> {
    val filtered = transactions.filter { it.transactionType == trendType }

    fun sumForDay(day: LocalDate): Double {
        val dayLong = Extra.convertLocalDateToLong(day)
        return filtered.filter { it.date == dayLong }.sumOf { it.amount }
    }

    fun weekdayLabel(day: LocalDate): String =
        day.dayOfWeek.getDisplayName(TextStyle.NARROW, Locale.US)

    fun weekRangeLabel(start: LocalDate, end: LocalDate): String {
        val month = start.format(monthLabelFormatter)
        return if (start == end) "${start.dayOfMonth} $month" else "${start.dayOfMonth}-${end.dayOfMonth} $month"
    }

    return when (period) {
        DetailPeriod.YEARLY -> (1..12).map { month ->
            val monthDate = LocalDate.of(cursor.year, month, 1)
            val range = Extra.getLocalDateStartEndDateMonth(monthDate)
            val amount = filtered.filter { it.date in range.startDate..range.endDate }.sumOf { it.amount }
            DetailBucket(label = monthDate.format(monthLabelFormatter), amount = amount)
        }

        DetailPeriod.MONTHLY -> {
            val monthStart = cursor.withDayOfMonth(1)
            (0 until cursor.lengthOfMonth())
                .map { offset -> monthStart.plusDays(offset.toLong()) }
                .chunked(7)
                .map { week ->
                    DetailBucket(
                        label = weekRangeLabel(week.first(), week.last()),
                        amount = week.sumOf { day -> sumForDay(day) }
                    )
                }
        }

        DetailPeriod.WEEKLY -> {
            val monday = cursor.with(DayOfWeek.MONDAY)
            (0..6L).map { offset ->
                val day = monday.plusDays(offset)
                DetailBucket(label = weekdayLabel(day), amount = sumForDay(day))
            }
        }

        DetailPeriod.DAILY -> (0..6L).map { offset ->
            val day = cursor.minusDays(6 - offset)
            DetailBucket(label = weekdayLabel(day), amount = sumForDay(day))
        }
    }
}
