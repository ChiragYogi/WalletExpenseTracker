package com.babacode.walletexpensetracker.data.model

data class QueryForTransaction(
    val transactionType: TransactionType? = null,
    val startDate: Long,
    val endDate: Long
)
