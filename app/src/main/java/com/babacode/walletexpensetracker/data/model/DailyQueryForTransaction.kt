package com.babacode.walletexpensetracker.data.model

data class DailyQueryForTransaction(
    val transactionType: TransactionType?,
    val dateForToday: Long
)
