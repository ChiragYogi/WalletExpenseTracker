package com.babacode.walletexpensetracker.fake

import com.babacode.walletexpensetracker.data.model.PaymentMode
import com.babacode.walletexpensetracker.data.model.Transaction
import com.babacode.walletexpensetracker.data.model.TransactionType
import com.babacode.walletexpensetracker.utiles.Extra
import java.time.LocalDate

// Shared test-only builder for compute-layer unit tests (FinanceCompute, DetailTrend,
// InsightsCompute, BudgetsCompute, SearchCompute, ...). `date` is a LocalDate for
// readability at call sites; converted via the same Extra helper the app itself uses so
// dates round-trip identically to how Transaction.date is actually populated/compared.
fun sampleTransaction(
    id: Int = 0,
    date: LocalDate = LocalDate.now(),
    type: TransactionType = TransactionType.EXPENSE,
    amount: Double = 100.0,
    note: String = "note-$id",
    tag: String = "Other",
    mode: PaymentMode = PaymentMode.CASH
): Transaction = Transaction(
    note = note,
    date = Extra.convertLocalDateToLong(date),
    transactionType = type,
    amount = amount,
    tag = tag,
    paymentType = mode,
    id = id
)
