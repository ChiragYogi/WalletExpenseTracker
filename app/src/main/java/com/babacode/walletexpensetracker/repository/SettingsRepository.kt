package com.babacode.walletexpensetracker.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.babacode.walletexpensetracker.R
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

class SettingsRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val dataStore: DataStore<Preferences>
) {

    private val currencyKey = stringPreferencesKey(context.getString(R.string.currencyKey))
    private val notificationKey = booleanPreferencesKey(context.getString(R.string.notificationKey))

    private val defaultCurrency = context.getString(R.string.usDollarCurrencyCodeValue)

    private val safeData: Flow<Preferences> = dataStore.data.catch { exception ->
        if (exception is IOException) emit(emptyPreferences()) else throw exception
    }

    val currency: Flow<String> = safeData.map { it[currencyKey] ?: defaultCurrency }
    val notificationsEnabled: Flow<Boolean> = safeData.map { it[notificationKey] ?: true }

    suspend fun setCurrency(value: String) {
        dataStore.edit { it[currencyKey] = value }
    }

    suspend fun setNotificationsEnabled(value: Boolean) {
        dataStore.edit { it[notificationKey] = value }
    }
}
