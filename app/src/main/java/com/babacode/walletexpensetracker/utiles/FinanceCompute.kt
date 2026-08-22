package com.babacode.walletexpensetracker.utiles

import com.babacode.walletexpensetracker.data.model.Transaction
import com.babacode.walletexpensetracker.data.model.TransactionType

// Port of the reference design's compute helpers (refrence/src/lib/finance/compute.ts).
// Pure functions over an in-memory transaction list, shared by Home/Insights/Budgets
// rather than aggregate SQL queries — matches the reference's client-side-compute
// approach and is simpler to keep correct at this app's (personal-scale) data volume.
object FinanceCompute {

    data class Totals(val income: Double, val expense: Double, val net: Double)

    fun inRange(transactions: List<Transaction>, startDate: Long, endDate: Long): List<Transaction> =
        transactions.filter { it.date in startDate..endDate }

    fun totals(transactions: List<Transaction>): Totals {
        val income = transactions.filter { it.transactionType == TransactionType.INCOME }.sumOf { it.amount }
        val expense = transactions.filter { it.transactionType == TransactionType.EXPENSE }.sumOf { it.amount }
        return Totals(income = income, expense = expense, net = income - expense)
    }

    fun byTag(
        transactions: List<Transaction>,
        type: TransactionType = TransactionType.EXPENSE
    ): List<Pair<String, Double>> =
        transactions
            .filter { it.transactionType == type }
            .groupBy { it.tag }
            .map { (tag, txs) -> tag to txs.sumOf { it.amount } }
            .sortedByDescending { it.second }

    fun byMode(transactions: List<Transaction>): List<Pair<String, Double>> =
        transactions
            .groupBy { it.paymentType.toString() }
            .map { (mode, txs) -> mode to txs.sumOf { it.amount } }
            .sortedByDescending { it.second }

    fun sortByDateDesc(transactions: List<Transaction>): List<Transaction> =
        transactions.sortedWith(compareByDescending<Transaction> { it.date }.thenByDescending { it.id })
}
