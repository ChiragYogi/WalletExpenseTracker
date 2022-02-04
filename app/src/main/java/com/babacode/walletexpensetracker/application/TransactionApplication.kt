package com.babacode.walletexpensetracker.application

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceManager
import com.babacode.walletexpensetracker.ui.setting.ThemeProvider

import dagger.hilt.android.HiltAndroidApp


@HiltAndroidApp
class TransactionApplication : Application() {

    private val mTheme by lazy {
        ThemeProvider(applicationContext).getThemeFromPreferences()
    }

    override fun onCreate() {
        super.onCreate()
         AppCompatDelegate.setDefaultNightMode(mTheme)

    }


}

