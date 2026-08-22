package com.babacode.walletexpensetracker.utiles

import com.babacode.walletexpensetracker.data.model.PaymentMode
import com.babacode.walletexpensetracker.data.model.TransactionType
import com.babacode.walletexpensetracker.fake.sampleTransaction
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

class CsvExporterTest {

    @Test
    fun `empty list produces just the header row`() {
        assertEquals("date,type,amount,note,tag,payment_mode", CsvExporter.buildCsv(emptyList()))
    }

    @Test
    fun `a transaction becomes one row with an ISO date`() {
        val transaction = sampleTransaction(
            id = 1,
            date = LocalDate.of(2026, 2, 5),
            type = TransactionType.EXPENSE,
            amount = 450.0,
            note = "Groceries",
            tag = "Food",
            mode = PaymentMode.CASH
        )

        val csv = CsvExporter.buildCsv(listOf(transaction))

        assertEquals(
            "date,type,amount,note,tag,payment_mode\n2026-02-05,Expense,450.0,\"Groceries\",Food,Cash",
            csv
        )
    }

    @Test
    fun `a quote in the note is escaped by doubling it`() {
        val transaction = sampleTransaction(id = 1, note = "Bob said \"hi\"")

        val csv = CsvExporter.buildCsv(listOf(transaction))

        assertEquals(true, csv.contains("\"Bob said \"\"hi\"\"\""))
    }

    @Test
    fun `multiple transactions become multiple rows in list order`() {
        val first = sampleTransaction(id = 1, note = "first")
        val second = sampleTransaction(id = 2, note = "second")

        val csv = CsvExporter.buildCsv(listOf(first, second))

        assertEquals(3, csv.lines().size) // header + 2 rows
    }
}
