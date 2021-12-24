package com.babacode.walletexpensetracker.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
enum class TransactionType: Parcelable {
    EXPENSE {
        override fun toString(): String = "Expense"
    },
    INCOME {
        override fun toString(): String = "Income"
    }
}