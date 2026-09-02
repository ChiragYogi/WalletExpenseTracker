package com.babacode.walletexpensetracker.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

// Closed set of payment modes, matching the reference design
// (refrence/src/lib/finance/types.ts: PaymentMode).
@Serializable
@Parcelize
enum class PaymentMode : Parcelable {
    CASH {
        override fun toString(): String = "Cash"
    },
    ONLINE_BANKING {
        override fun toString(): String = "Online Banking"
    },
    UPI {
        override fun toString(): String = "UPI"
    },
    CREDIT_CARD {
        override fun toString(): String = "Credit Card"
    },
    DEBIT_CARD {
        override fun toString(): String = "Debit Card"
    }
}
