package com.babacode.walletexpensetracker.ui.recurring

import com.babacode.walletexpensetracker.data.model.Frequency
import com.babacode.walletexpensetracker.data.model.RecurringRule
import com.babacode.walletexpensetracker.data.model.Transaction
import com.babacode.walletexpensetracker.repository.RecurringRepository
import com.babacode.walletexpensetracker.repository.TransactionRepository
import com.babacode.walletexpensetracker.utiles.Extra

// Port of the reference design's `next()` helper (refrence/src/routes/recurring.tsx),
// used both for the rule list's "then <date>" preview and to advance a rule after posting.
fun nextOccurrence(date: Long, frequency: Frequency): Long {
    val localDate = Extra.convertLocalLongDateToStringAndGetLocalDate(date)
    val next = when (frequency) {
        Frequency.DAILY -> localDate.plusDays(1)
        Frequency.WEEKLY -> localDate.plusWeeks(1)
        Frequency.MONTHLY -> localDate.plusMonths(1)
        Frequency.YEARLY -> localDate.plusYears(1)
    }
    return Extra.convertLocalDateToLong(next)
}

// Posts a transaction for [rule] and advances its nextDate by one cycle. Shared by
// RecurringPostWorker (automatic due-rule posting) and RecurringViewModel's manual
// "Run now" action, so both paths stay consistent — a manual run also advances nextDate
// so the automatic worker doesn't immediately re-post the same occurrence.
suspend fun postRecurringRule(
    rule: RecurringRule,
    transactionRepository: TransactionRepository,
    recurringRepository: RecurringRepository,
    postDate: Long = Extra.currentDayDate()
) {
    transactionRepository.insertNewTransaction(
        Transaction(
            note = rule.note,
            date = postDate,
            transactionType = rule.type,
            amount = rule.amount,
            tag = rule.tag,
            paymentType = rule.mode
        )
    )
    recurringRepository.updateRecurringRule(rule.copy(nextDate = nextOccurrence(rule.nextDate, rule.frequency)))
}
