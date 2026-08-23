package com.babacode.walletexpensetracker.ui.setting.notification

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import com.babacode.walletexpensetracker.repository.SettingsRepository
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class AlarmReceiver : BroadcastReceiver() {

    @Inject lateinit var settingsRepository: SettingsRepository

    override fun onReceive(context: Context?, intent: Intent?) {
        val ctx = context ?: return
        val alarmUtils = AlarmUtils(ctx)
        val pendingResult = goAsync()

        CoroutineScope(SupervisorJob() + Dispatchers.Default).launch {
            try {
                val notificationsEnabled = settingsRepository.notificationsEnabled.first()
                val hasPermission = hasNotificationPermission(ctx)

                if (!notificationsEnabled || !hasPermission) {
                    if (notificationsEnabled) {
                        // Permission was revoked outside the app (system settings) after the
                        // toggle was enabled — reflect that in Settings instead of leaving a
                        // stale checkmark that silently does nothing.
                        settingsRepository.setNotificationsEnabled(false)
                    }
                    alarmUtils.cancelNotificationAlarm()
                    return@launch
                }

                try {
                    NotificationUtils(ctx).launchNotification()
                } catch (e: Exception) {
                    FirebaseCrashlytics.getInstance().recordException(e)
                }

                // Reschedule for tomorrow regardless of whether today's notification
                // succeeded, so one failure doesn't silently kill the daily reminder loop.
                val calendar = Calendar.getInstance()
                calendar.add(Calendar.DAY_OF_YEAR, 1)
                alarmUtils.initAlarmForNotification(calendar)
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
            } finally {
                pendingResult.finish()
            }
        }
    }

    private fun hasNotificationPermission(context: Context): Boolean =
        Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
}
