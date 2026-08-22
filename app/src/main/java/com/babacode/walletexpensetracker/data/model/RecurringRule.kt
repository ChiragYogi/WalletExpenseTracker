package com.babacode.walletexpensetracker.data.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

// Recurring transaction rule, matching refrence/src/lib/finance/types.ts: RecurringRule,
// plus `active` used to pause a rule without deleting it. Unlike the reference (manual
// "Run now" only), this app auto-posts due rules via RecurringPostWorker (Phase 13).
@Serializable
@Parcelize
@Entity(tableName = "recurring_table")
data class RecurringRule(
    val type: TransactionType,
    val amount: Double,
    val note: String,
    val tag: String,
    val mode: PaymentMode,
    val frequency: Frequency,
    val nextDate: Long,
    val active: Boolean = true,
    @PrimaryKey(autoGenerate = true) val id: Int = 0
) : Parcelable
