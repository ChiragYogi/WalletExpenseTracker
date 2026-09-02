package com.babacode.walletexpensetracker.data.settings

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.SharedPreferencesMigration
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore

// Matches androidx.preference.PreferenceManager's default shared prefs file naming
// (getDefaultSharedPreferencesName is private in androidx.preference 1.2.1).
private fun defaultSharedPreferencesName(context: Context) = "${context.packageName}_preferences"

val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "settings",
    produceMigrations = { context ->
        listOf(SharedPreferencesMigration(context, defaultSharedPreferencesName(context)))
    }
)
