package com.babacode.walletexpensetracker.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.DateFormat


@Entity(tableName = "transaction_table")
data class Transaction(
    val title: String,
    val date: String,
    val transactionType: String,
    val amount: Double,
    val image: String,
    val tag: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    @PrimaryKey(autoGenerate = true) val id: Int = 0
){
    val createdAtDate: String
    get() = DateFormat.getTimeInstance().format(createdAt)

}
