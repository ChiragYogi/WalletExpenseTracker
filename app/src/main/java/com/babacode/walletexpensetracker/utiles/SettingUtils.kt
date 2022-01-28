package com.babacode.walletexpensetracker.utiles

import android.content.Context
import android.provider.Settings.Global.getString
import android.util.AttributeSet
import androidx.preference.PreferenceManager
import com.babacode.walletexpensetracker.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import org.jetbrains.annotations.NotNull

import androidx.annotation.NonNull

import com.google.android.material.textfield.MaterialAutoCompleteTextView




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
        val notificationPreference =
            mSharedPreferences.getBoolean(this.mContext.getString(R.string.notificationKey), true)

        return notificationPreference
    }



}