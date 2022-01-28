package com.babacode.walletexpensetracker.ui.setting

import android.app.UiModeManager
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceManager
import com.babacode.walletexpensetracker.R
import java.security.InvalidParameterException

class ThemeProvider constructor(private val context: Context) {


    fun getThemeFromPreferences(): Int {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val selectedTheme = sharedPreferences.getString(
            context.getString(R.string.themeKey),
            context.getString(R.string.system_theme_preference_value)
        )
        return selectedTheme?.let {
            getTheme(it)
        } ?: AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
    }

    fun getThemeDescriptionFromPreference(preferencesValue: String): String =
        when (preferencesValue) {
            context.getString(R.string.dark_theme_preference_value) -> context.getString(R.string.dark_theme_description)
            context.getString(R.string.light_theme_preference_value) -> context.getString(R.string.light_theme_description)
            else -> context.getString(R.string.system_theme_description)
        }

    fun getTheme(selectedTheme: String): Int = when (selectedTheme) {
        context.getString(R.string.dark_theme_preference_value) -> UiModeManager.MODE_NIGHT_YES
        context.getString(R.string.light_theme_preference_value) -> UiModeManager.MODE_NIGHT_NO
        context.getString(R.string.system_theme_preference_value) -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM

        else -> UiModeManager.MODE_NIGHT_NO

    }
}