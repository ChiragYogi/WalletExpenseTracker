package com.babacode.walletexpensetracker.data.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

// One budget per tag, implied monthly (always computed against the current
// calendar month) — matches refrence/src/lib/finance/types.ts: Budget.
@Serializable
@Parcelize
@Entity(tableName = "budget_table")
data class Budget(
    val tag: String,
    val limitAmount: Double,
    @PrimaryKey(autoGenerate = true) val id: Int = 0
) : Parcelable
