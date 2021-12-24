package com.babacode.walletexpensetracker.data.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.text.DateFormat


@Parcelize
@Entity(tableName = "transaction_table")
data class Transaction(
    val note: String,
    val date: Long,
    val transactionType: TransactionType,
    val amount: Double,
    val tag: TransactionTag,
    val paymentType: PaymentType,
    @PrimaryKey(autoGenerate = true) val id: Int = 0
): Parcelable














 
