package com.babacode.walletexpensetracker.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable


@Serializable
@Parcelize
enum class PaymentType : Parcelable {
    CASH {
        override fun toString(): String = "Cash"
    },
    CARD {
        override fun toString(): String = "Card"
    },
    ONLINE {
        override fun toString(): String = "Online Banking"
    },
    GIFT {
        override fun toString(): String = "Gift Coupons"
    }
}