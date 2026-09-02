package com.babacode.walletexpensetracker.data.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable


@Serializable
@Parcelize
@Entity(tableName = "transaction_table")
data class Transaction(
    val note: String,
    val date: Long,
    val transactionType: TransactionType,
    val amount: Double,
    val tag: String,
    val paymentType: PaymentMode,
    @PrimaryKey(autoGenerate = true) val id: Int = 0
): Parcelable














 
