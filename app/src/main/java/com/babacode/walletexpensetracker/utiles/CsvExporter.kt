package com.babacode.walletexpensetracker.utiles

import com.babacode.walletexpensetracker.data.model.Transaction
import java.time.format.DateTimeFormatter
import java.util.Locale

// Port of the reference design's exportCsv (refrence/src/routes/search.tsx). Dates are
// written as ISO yyyy-MM-dd — more portable for a CSV than this app's on-screen
// "dd MMM, yyyy" display format.
object CsvExporter {
    private const val HEADER = "date,type,amount,note,tag,payment_mode"
    private val isoDateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.US)

    fun buildCsv(transactions: List<Transaction>): String {
        val rows = transactions.joinToString(separator = "\n") { transaction ->
            listOf(
                Extra.convertLocalLongDateToStringAndGetLocalDate(transaction.date).format(isoDateFormatter),
                transaction.transactionType.toString(),
                transaction.amount.toString(),
                "\"${transaction.note.replace("\"", "\"\"")}\"",
                transaction.tag,
                transaction.paymentType.toString()
            ).joinToString(separator = ",")
        }
        return if (rows.isEmpty()) HEADER else "$HEADER\n$rows"
    }
}
