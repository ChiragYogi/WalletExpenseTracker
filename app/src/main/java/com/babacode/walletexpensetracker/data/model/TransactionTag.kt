package com.babacode.walletexpensetracker.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
enum class TransactionTag : Parcelable {

    OTHER {
        override fun toString() = "Other"
    },
    FOOD {
        override fun toString() = "Food"
    },
    SHOPPING {
        override fun toString() = "Shopping"
    },
    TRAVELLING {
        override fun toString() = "Travelling"
    },
    ENTERTAINMENT {
        override fun toString() = "Entertainment"
    },
    HEALTH {
        override fun toString() = "Health"
    },
    EDUCATION {
        override fun toString() = "Education"
    },
    RENT {
        override fun toString() = "Rent"
    },
    BILLS {
        override fun toString() = "Bills"
    },
    GIFT {
        override fun toString() = "Gift"
    },
    INVESTMENT {
        override fun toString() = "Investment"
    },
    UTILS {
        override fun toString() = "Utils"
    },
    SALARY {
        override fun toString() = "Salary"
    },
    COUPONS {
        override fun toString() = "Coupons"
    },
    CASHBACK {
        override fun toString() = "CashBack"
    },
}