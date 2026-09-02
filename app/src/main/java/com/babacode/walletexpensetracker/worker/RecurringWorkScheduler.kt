package com.babacode.walletexpensetracker.worker

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

private const val PERIODIC_WORK_NAME = "recurring_post_periodic"
private const val CATCH_UP_WORK_NAME = "recurring_post_catchup"

// PeriodicWorkRequest's 15-minute minimum interval is not exact and can be deferred for
// hours under Doze/App Standby, so a due rule isn't guaranteed to post the moment it's
// due — schedule() also enqueues an immediate one-off "catch up" run, which is what
// actually guarantees a due rule posts promptly whenever the user opens the app.
object RecurringWorkScheduler {

    fun schedule(context: Context) {
        val workManager = WorkManager.getInstance(context)

        val periodicRequest = PeriodicWorkRequestBuilder<RecurringPostWorker>(15, TimeUnit.MINUTES).build()
        workManager.enqueueUniquePeriodicWork(
            PERIODIC_WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            periodicRequest
        )

        val catchUpRequest = OneTimeWorkRequestBuilder<RecurringPostWorker>().build()
        workManager.enqueueUniqueWork(
            CATCH_UP_WORK_NAME,
            ExistingWorkPolicy.REPLACE,
            catchUpRequest
        )
    }
}
