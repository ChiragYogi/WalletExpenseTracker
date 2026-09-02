package com.babacode.walletexpensetracker.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.babacode.walletexpensetracker.repository.RecurringRepository
import com.babacode.walletexpensetracker.repository.TransactionRepository
import com.babacode.walletexpensetracker.ui.recurring.postRecurringRule
import com.babacode.walletexpensetracker.utiles.Extra
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

// Posts every recurring rule whose nextDate is due (Phase 13) — the reference design only
// has a manual "Run now" button, but this app auto-posts on schedule. Runs both on a
// periodic 15-minute cadence (see RecurringWorkScheduler) and as an immediate app-open
// catch-up, since periodic work can be deferred for hours under Doze/App Standby.
@HiltWorker
class RecurringPostWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val recurringRepository: RecurringRepository,
    private val transactionRepository: TransactionRepository
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            val today = Extra.currentDayDate()
            val dueRules = recurringRepository.getDueRecurringRules(today)
            dueRules.forEach { rule ->
                postRecurringRule(rule, transactionRepository, recurringRepository, postDate = today)
            }
            Result.success()
        } catch (exception: Exception) {
            FirebaseCrashlytics.getInstance().recordException(exception)
            Result.retry()
        }
    }
}
