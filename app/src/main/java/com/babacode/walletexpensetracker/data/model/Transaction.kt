package com.babacode.walletexpensetracker.data.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.text.DateFormat
import java.util.*

@Parcelize
@Entity(tableName = "transaction_table")
data class Transaction(
    val title: String,
    val date: String,
    val transactionType: String,
    val amount: Double,
    val tag: String,
    val paymentType: String,
    val createdAt: Long = System.currentTimeMillis(),
    @PrimaryKey(autoGenerate = true) val id: Int = 0
): Parcelable{
    val createdAtDate: String
    get() = DateFormat.getTimeInstance().format(createdAt)

}
