package com.babacode.walletexpensetracker.application

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.WorkManager
import com.babacode.walletexpensetracker.worker.RecurringWorkScheduler
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class TransactionApplication : Application(), Configuration.Provider {

    @Inject lateinit var workerFactory: HiltWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder().setWorkerFactory(workerFactory).build()

    override fun onCreate() {
        super.onCreate()
        // androidx.work's androidx-startup initializer no longer auto-detects Configuration.Provider
        // (disabled in the manifest), so WorkManager must be initialized manually with the
        // Hilt-backed configuration before anything calls WorkManager.getInstance().
        WorkManager.initialize(this, workManagerConfiguration)
        RecurringWorkScheduler.schedule(this)
    }
}
