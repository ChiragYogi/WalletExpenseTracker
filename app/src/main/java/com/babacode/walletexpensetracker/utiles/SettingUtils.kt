package com.babacode.walletexpensetracker.utiles

import android.content.Context
import androidx.preference.PreferenceManager
import com.babacode.walletexpensetracker.R


class SettingUtils constructor(private val mContext: Context) {


    private val mSharedPreferences by lazy {
        PreferenceManager.getDefaultSharedPreferences(mContext)
    }

    fun getCurrencyCode(): String {

        val currencyCode = mSharedPreferences.getString(
            this.mContext.getString(R.string.currencyKey),
            this.mContext.getString(R.string.usDollarCurrencyCodeValue)
        )
        return currencyCode!!
    }

    fun notificationForAlarm(): Boolean {

        return mSharedPreferences.getBoolean(mContext.getString(R.string.notificationKey), true)
    }



}