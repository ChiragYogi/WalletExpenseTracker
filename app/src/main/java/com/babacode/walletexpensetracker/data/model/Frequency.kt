package com.babacode.walletexpensetracker.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
enum class Frequency : Parcelable {
    DAILY {
        override fun toString(): String = "Daily"
    },
    WEEKLY {
        override fun toString(): String = "Weekly"
    },
    MONTHLY {
        override fun toString(): String = "Monthly"
    },
    YEARLY {
        override fun toString(): String = "Yearly"
    }
}
