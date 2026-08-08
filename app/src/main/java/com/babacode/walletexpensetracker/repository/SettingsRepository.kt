package com.babacode.walletexpensetracker.repository

import android.content.Context
import androidx.core.content.edit
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.preference.PreferenceManager
import com.babacode.walletexpensetracker.R
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SettingsRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val dataStore: DataStore<Preferences>
) {

    private val themeKey = stringPreferencesKey(context.getString(R.string.themeKey))
    private val currencyKey = stringPreferencesKey(context.getString(R.string.currencyKey))
    private val notificationKey = booleanPreferencesKey(context.getString(R.string.notificationKey))

    private val defaultTheme = context.getString(R.string.system_theme_preference_value)
    private val defaultCurrency = context.getString(R.string.usDollarCurrencyCodeValue)

    val theme: Flow<String> = dataStore.data.map { it[themeKey] ?: defaultTheme }
    val currency: Flow<String> = dataStore.data.map { it[currencyKey] ?: defaultCurrency }
    val notificationsEnabled: Flow<Boolean> = dataStore.data.map { it[notificationKey] ?: true }

    suspend fun setTheme(value: String) {
        dataStore.edit { it[themeKey] = value }
        mirrorToLegacySharedPreferences { putString(context.getString(R.string.themeKey), value) }
    }

    suspend fun setCurrency(value: String) {
        dataStore.edit { it[currencyKey] = value }
        mirrorToLegacySharedPreferences { putString(context.getString(R.string.currencyKey), value) }
    }

    suspend fun setNotificationsEnabled(value: Boolean) {
        dataStore.edit { it[notificationKey] = value }
        mirrorToLegacySharedPreferences { putBoolean(context.getString(R.string.notificationKey), value) }
    }

    private inline fun mirrorToLegacySharedPreferences(crossinline write: android.content.SharedPreferences.Editor.() -> Unit) {
        try {
            PreferenceManager.getDefaultSharedPreferences(context).edit { write() }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
