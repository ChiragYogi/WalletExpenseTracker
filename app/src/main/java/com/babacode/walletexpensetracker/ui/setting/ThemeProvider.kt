package com.babacode.walletexpensetracker.ui.setting

import android.content.Context
import com.babacode.walletexpensetracker.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class ThemeProvider @Inject constructor(@ApplicationContext private val context: Context) {

    // Fixed fallback used only as the initial value before the DataStore-backed
    // SettingsRepository.theme flow (collected in Compose) emits its first value.
    // DataStore has no synchronous read, so this intentionally can't reflect a
    // previously saved preference for that first frame.
    fun getInitialThemePreference(): String =
        context.getString(R.string.system_theme_preference_value)

    fun getThemeDescriptionFromPreference(preferencesValue: String): String =
        when (preferencesValue) {
            context.getString(R.string.dark_theme_preference_value) -> context.getString(R.string.dark_theme_description)
            context.getString(R.string.light_theme_preference_value) -> context.getString(R.string.light_theme_description)
            else -> context.getString(R.string.system_theme_description)
        }
}